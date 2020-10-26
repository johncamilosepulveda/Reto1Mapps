package com.example.Reto1.comm;

import com.example.Reto1.activity.MapsActivity;
import com.google.gson.Gson;

public class LocationWorker extends Thread{

    private MapsActivity ref;
    private boolean isAlive;

    public LocationWorker(MapsActivity ref){
        this.ref = ref;
        isAlive = true;
    }

    @Override
    public void run() {

        HTTPSWebUtilDomi utilDomi = new HTTPSWebUtilDomi();
        Gson gson = new Gson();
        while (isAlive){

            delay(10000);
            //hacer el put de nuestra posición
            if(ref.getCurrentPosition() != null){
                utilDomi.PUTrequest("https://appmoviles2020-b4608.firebaseio.com/user/"+ref.getUser()+"/location.json", gson.toJson(ref.getCurrentPosition()));
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

    public void finish() {
        this.isAlive = false;
    }
}
