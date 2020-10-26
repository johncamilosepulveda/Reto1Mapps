package com.example.Reto1.comm;

import com.example.Reto1.activity.MapsActivity;
import com.google.gson.Gson;

public class HoleLocationWorker {

    private MapsActivity ref;

    public HoleLocationWorker(MapsActivity ref){
        this.ref = ref;
    }

    public void postHole() {
        HTTPSWebUtilDomi utilDomi = new HTTPSWebUtilDomi();
        Gson gson = new Gson();
            //hacer el put de nuestra posición
            if(ref.getCurrentHole() != null){
                utilDomi.POSTrequest("https://appmoviles2020-b4608.firebaseio.com/hole.json", gson.toJson(ref.getCurrentHole()));
            }

    }


}
