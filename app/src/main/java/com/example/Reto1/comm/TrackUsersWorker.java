package com.example.Reto1.comm;

import android.util.Log;

import com.example.Reto1.model.Position;
import com.example.Reto1.activity.MapsActivity;
import com.example.Reto1.model.PositionContainer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class TrackUsersWorker extends Thread{

    private MapsActivity ref;
    private boolean isAlive;

    public TrackUsersWorker(MapsActivity ref){
        this.ref = ref;
        this.isAlive = true;
    }

    @Override
    public void run() {

        HTTPSWebUtilDomi httpsWebUtilDomi = new HTTPSWebUtilDomi();
        Gson gson = new Gson();

        while(isAlive){
            delay(3000);
            String json = httpsWebUtilDomi.GETrequest("https://appmoviles2020-b4608.firebaseio.com/user.json");
            ArrayList<Position> positions = new ArrayList<>();

            if(json != null){
                Log.e(">>>", json);
                Type type = new TypeToken<HashMap<String, PositionContainer>>(){}.getType();
                HashMap<String, PositionContainer> users = gson.fromJson(json, type);



                if(users != null){

                    users.forEach((key, value)->{

                                Log.e(">>>", key);

                                PositionContainer positionContainer = value;
                                double lat = positionContainer.getLocation().getLat();
                                double lng = positionContainer.getLocation().getLng();

                                positions.add(new Position(lat, lng));

                            }
                    );


                }

                ref.updateMarkers(positions);
            }


        }

    }


    public void delay(long time){

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void finish(){
        this.isAlive = false;
    }
}
