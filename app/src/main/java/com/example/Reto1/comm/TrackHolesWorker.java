package com.example.Reto1.comm;

import android.util.Log;

import com.example.Reto1.model.Position;
import com.example.Reto1.activity.MapsActivity;
import com.example.Reto1.model.Hole;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class TrackHolesWorker extends Thread {

    private MapsActivity ref;
    private boolean isAlive;
    private ArrayList<Hole> holes;

    public TrackHolesWorker(MapsActivity ref) {
        this.ref = ref;
        this.isAlive = true;
    }

    public TrackHolesWorker() {
    }

    @Override
    public void run() {


        HTTPSWebUtilDomi httpsWebUtilDomi = new HTTPSWebUtilDomi();
        Gson gson = new Gson();

        while (isAlive) {
            delay(3000);

            String json = httpsWebUtilDomi.GETrequest("https://appmoviles2020-b4608.firebaseio.com/hole.json");
            Type type = new TypeToken<HashMap<String, Hole>>() {
            }.getType();

            if (json != null) {
                Log.e(">>>", json);

                HashMap<String, Hole> holeSer = gson.fromJson(json, type);
                holes = new ArrayList<>();

                if (holeSer != null){
                    holeSer.forEach((key, value) -> {

                                Log.e(">>>", key);

                                Hole hole = value;

                                boolean confirmed = hole.isConfirmed();
                                String id = hole.getId();
                                Position pos = hole.getPosition();
                                String user = hole.getUserReporter();

                                holes.add(new Hole(id, pos, confirmed, user));


                            }

                    );

                    ref.updateMarkersHoles(holes);
                }

            }
        }


    }

    public void delay ( long time){

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void finish () {
        this.isAlive = false;
    }

    public ArrayList<Hole> getHoles() {
        return holes;
    }

    public void setHoles(ArrayList<Hole> holes) {
        this.holes = holes;
    }
}