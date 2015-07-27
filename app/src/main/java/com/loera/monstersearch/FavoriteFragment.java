package com.loera.monstersearch;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;


public class FavoriteFragment extends android.app.Fragment {

    private View view;
    private boolean selecting;
    static ArrayList<Monster> favs;
    Context context;

    private class BoxImageAdapter extends BaseAdapter {

        Context context;

        public BoxImageAdapter(Context c){

            this.context = c;
        }

        public int getCount() {
            return favs.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            int width = 0;

            switch (Home.screenSize){

                case Configuration.SCREENLAYOUT_SIZE_LARGE:

                    width = 90;

                    break;

                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                    width = 50;

                    break;

                default: width = 170;
            }

            if(convertView  == null){

                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(width,width));

            }else{

                imageView = (ImageView)convertView;
            }

            Bitmap b = BitmapFactory.decodeFile(favs.get(position).thumb);
            Drawable d = new BitmapDrawable(context.getResources(),b);
            d.setBounds(0,0,b.getWidth()*2,b.getHeight()*2);
            imageView.setImageDrawable(d);

            return imageView;
        }
    }



    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        if(getArguments() == null)
            selecting = false;
        else
           selecting = true;

        return inflater.inflate(R.layout.fragment_favorite,container,false);
    }

    @Override
    public void onStart(){

        super.onStart();

        view = getView();
        context = getActivity();

        GridView gridView = (GridView)view.findViewById(R.id.monsterBoxGrid);
        gridView.setAdapter(new BoxImageAdapter(context));


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              if(!selecting){
                Intent intent = new Intent(context,MonsterPage.class);
                intent.putExtra("selection",position);
                intent.putExtra("monsters",favs);
                  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                      context.startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                        } else
                      context.startActivity(intent);
              }else{
                  try{
                  File internal = context.getDir("Homescreen",Context.MODE_PRIVATE);
                   if(!internal.exists())
                       internal.mkdir();
                      File imageFile = new File(internal,"image.png");
                     FileOutputStream out = new FileOutputStream(imageFile);
                      Bitmap image  = BitmapFactory.decodeFile(favs.get(position).bitmap);
                      image.compress(Bitmap.CompressFormat.PNG,100,out);
                      out.close();
                      Toast.makeText(context.getApplicationContext(),"Homescreen image selected",Toast.LENGTH_SHORT).show();
                      getFragmentManager().beginTransaction()
                              .setCustomAnimations(R.anim.slideinleft,R.anim.slideoutright)
                              .replace(R.id.settingsActivity,new SettingsActivity.SettingsPage()).commit();

                  }catch(Exception e){
                      e.printStackTrace();

                  }

              }

            }
        });


        if(favs.isEmpty()) {
           emptyError();
        }
    }

    public void emptyError(){

        AlertDialog.Builder empty = new AlertDialog.Builder(context);
        empty.setTitle("No Monsters Found");
        empty.setIcon(R.drawable.heart_outline);
        empty.setMessage("Try tapping the heart icon to add Monsters to your Box.");
        empty.setPositiveButton("OK :(",null);
        empty.show();


    }

    public static void storeFavorites(Context c){

        File internal = c.getDir("Monsters",Context.MODE_PRIVATE);

        favs = new ArrayList<>();

        File[] monsters  = internal.listFiles();

        for(File f:monsters){

            Monster m = new Monster();

            m.favorited = true;
            m.ascLinks = "";

            File [] temp = f.listFiles();

            for(File f2:temp){

                String path = f2.getPath();

                if(path.contains("data.txt")){
                    try{

                    FileReader fr = new FileReader(f2);


                        BufferedReader reader  = new BufferedReader(fr);

                        String line = "";
                        while((line = reader.readLine()) != null){

                         if(line.equals("num"))
                             m.num = reader.readLine();
                         if(line.equals("name"))
                             m.name = reader.readLine();
                         if(line.equals("class"))
                             m.monClass = reader.readLine();
                         if(line.equals("health")){
                             m.maxHealth = reader.readLine();
                             m.plusHealth = reader.readLine();
                         }if(line.equals("attack")){
                                m.maxAttack = reader.readLine();
                                m.plusAttack = reader.readLine();

                            }if(line.equals("speed")){
                                m.maxSpeed = reader.readLine();
                                m.plusSpeed = reader.readLine();
                            }
                           if(line.equals("impact"))
                               m.impact = reader.readLine();
                           if(line.equals("ability"))
                               m.ability = reader.readLine();
                           if(line.equals("type"))
                               m.type  = reader.readLine();
                            if(line.equals("strikename"))
                                m.strikeName = reader.readLine();
                            if(line.equals("strikeinfo")){
                                String strikeInfo = "";
                                String t = "";
                                while(!(t = reader.readLine()).equals("cooldown")){

                                    strikeInfo += t;

                                }

                                m.strikeInfo = strikeInfo;
                                m.cooldown = reader.readLine();

                            }
                            if(line.equals("bcname"))
                                    m.bcName = reader.readLine();
                            if(line.equals("bcinfo")){
                                String bcInfo = "";
                                String t = "";
                                while(!(t = reader.readLine()).equals("bcpower")){

                                    bcInfo += t;

                                }

                                m.bcInfo = bcInfo;
                                m.bcPower = reader.readLine();
                                String mat = reader.readLine();
                                if(mat.equals("null"))
                                    m.evoMat = null;
                                else
                                    m.evoMat = mat;
                                mat = reader.readLine();
                                if(mat.equals("null"))
                                    m.ascMat = null;
                                else
                                    m.ascMat = mat;

                            }







                        }

                    }catch(Exception e){

                        e.printStackTrace();
                    }




                }else if(path.contains("thumb")){

                    m.thumb = f2.getPath();


                }else if(path.endsWith(".png")){

                    m.bitmap = f2.getPath();
                  }else{

                    m.ascLinks += f2.getPath() + "END";

                }

            }

            String level = "";
            switch(m.name.charAt(m.name.length()-1)){

                case '1':level = "5";
                    break;
                case '2': level = "15";
                    break;
                case '3': level = "20";
                    break;
                case '4': level = "40";
                    break;
                case '5': level = "70";
                    break;
                case '6': level = "99";



            }

            m.maxLevel = level;

            Log.i("Favorite","created Monster " + m.name );
            favs.add(m);
        }


    }


    public static void addFavorite(Monster m, Context c){


        //opens Monsters folder
       File internal = c.getDir("Monsters",Context.MODE_PRIVATE);

        Log.i("Favorite","Getting internal storage at " + internal.toString());

        if(!internal.exists())
            internal.mkdir();

        //opens specific monster folder
        File monsterFolder = new File(internal,m.num);
        Log.i("Favorite","Getting Specific Monster Folder " + monsterFolder.toString() );
        if(!monsterFolder.exists())
            monsterFolder.mkdir();
        Log.i("Favorite","Creating data File");

        //creates data file
        File monster  = new File(monsterFolder,"data.txt");
        File monsterImage = new File(monsterFolder,m.num+".png");
        File monsterThumbnail = new File(monsterFolder,"thumbnail.jpg");

        String[] links = null;
        File[] ascPics = null;
        if(m.ascLinks != null){
         links = m.ascLinks.split("END");
         ascPics = new File[links.length];
        for(int a  = 0;a<ascPics.length;a++){

            ascPics[a] = new File(monsterFolder,"asc"+ a + ".jpg");

        }}

        try{

            FileOutputStream out = new FileOutputStream(monster);

            PrintWriter writer  = new PrintWriter(out);

            writer.println("num\n" + m.num);
            writer.println("name\n" + m.name);
            writer.println("class\n" + m.monClass);
            writer.println("health");
            writer.println(m.maxHealth);
            writer.println(m.plusHealth);
            writer.println("attack");
            writer.println(m.maxAttack);
            writer.println(m.plusAttack);
            writer.println("speed");
            writer.println(m.maxSpeed);
            writer.println(m.plusSpeed);
            writer.println("impact\n" + m.impact);
            writer.println("ability\n" + m.ability);
            writer.println("type\n" + m.type);
            writer.println("strikename\n" + m.strikeName);
            writer.println("strikeinfo\n" + m.strikeInfo);
            writer.println("cooldown\n" + m.cooldown);
            writer.println("bcname\n" + m.bcName);
            writer.println("bcinfo\n" + m.bcInfo);
            writer.println("bcpower\n" + m.bcPower);
            writer.println(m.evoMat);
            writer.print(m.ascMat);

            writer.close();

            Log.i("Favorite","Saving images");


            out = new FileOutputStream(monsterImage);

            Bitmap image  = BitmapFactory.decodeFile(m.bitmap);

             image.compress(Bitmap.CompressFormat.PNG, 100, out);

            out = new FileOutputStream(monsterThumbnail);

            image = BitmapFactory.decodeFile(m.thumb);

            image.compress(Bitmap.CompressFormat.JPEG,100,out);

            if(ascPics != null)
            for(int b = 0;b<ascPics.length;b++){
                out = new FileOutputStream(ascPics[b]);
                image = BitmapFactory.decodeFile(links[b]);
                image.compress(Bitmap.CompressFormat.JPEG,100,out);


            }

          out.close();

        }catch(Exception e){

            e.printStackTrace();
        }



    }

    public static void removeFavorite(String number,Context c){

        for(int m = 0;m<favs.size();m++){
            if(favs.get(m).num.equals(number)){
                favs.remove(m);
                break;
            }

        }

        File dir = c.getDir("Monsters",Context.MODE_PRIVATE);

        File monDir = new File(dir+File.separator+number);

        String[] files  = monDir.list();



        try {

            Log.i("Favorite", "Deleting " + files.length+ " files");

            for(String s : files){

                new File(monDir,s).delete();
            }
            monDir.delete();



      }catch(Exception e){

          e.printStackTrace();
      }



    }
  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
