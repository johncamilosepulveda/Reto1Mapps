package com.example.Reto1.activity;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.Reto1.comm.LocationWorker;
import com.example.Reto1.comm.TrackHolesWorker;
import com.example.Reto1.comm.TrackUsersWorker;
import com.example.Reto1.model.Position;
import com.example.Reto1.R;
import com.example.Reto1.comm.HoleLocationWorker;
import com.example.Reto1.model.Hole;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private LocationWorker locationWorker;
    private GoogleMap mMap;
    private String user;
    private LocationManager manager;
    private ArrayList<Marker> points;
    private Position currentPosition;
    private FloatingActionButton addBtn;
    private TrackUsersWorker trackUsersWorker;
    private HoleLocationWorker holeLocationWorker;
    private TrackHolesWorker trackHolesWorker;
    private ArrayList<Marker> pointsHoles;
    private Hole currentHole;
    private TextView distanceTV;
    private FloatingActionButton confirmedBtn;
    private ArrayList<Hole> holes;

    private TextView coordenadasTV;
    private TextView direccionTV;
    private Button okButton;

    private Hole confirmedHole;

    public HoleLocationWorker getHoleLocationWorker() {
        return holeLocationWorker;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        user = getIntent().getExtras().getString("user");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        points = new ArrayList<>();

        pointsHoles = new ArrayList<>();

        holes = new ArrayList<>();

        addBtn = findViewById(R.id.addBtn);
        distanceTV = findViewById(R.id.distanceTV);

        confirmedBtn = findViewById(R.id.confirmedBtn);

        coordenadasTV = findViewById(R.id.coordenadasTV);
        direccionTV = findViewById(R.id.direccionTV);
        okButton = findViewById(R.id.OKbutton);

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 2, this);

        setInitialPos();

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);

        addBtn.setOnClickListener(this);

        locationWorker = new LocationWorker(this);
        locationWorker.start();

        trackUsersWorker = new TrackUsersWorker(this);
        trackUsersWorker.start();

        holeLocationWorker = new HoleLocationWorker(this);

        trackHolesWorker = new TrackHolesWorker(this);
        trackHolesWorker.start();

        new Thread(()->{holes = trackHolesWorker.getHoles();}).start();

    }

    @Override
    protected void onDestroy() {
        locationWorker.finish();
        trackUsersWorker.finish();
        trackHolesWorker.finish();
        super.onDestroy();
    }

    public void setInitialPos(){
        @SuppressLint("MissingPermission") Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            updateMyLocation(location);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        updateMyLocation(location);
    }

    public void updateMyLocation(Location location){
        LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 14));
        currentPosition = new Position(location.getLatitude(), location.getLongitude());

        if(pointsHoles.size() != 0){
            shortestDistance(computeDistances());
        }

        confirmedHole();

    }

    public void confirmedHole(){


                    for(int i=0; i< pointsHoles.size(); i++){
                        Marker marker = pointsHoles.get(i);
                        LatLng markerLoc = marker.getPosition();
                        LatLng meLoc = new LatLng(currentPosition.getLat(), currentPosition.getLng());


                        double meters = SphericalUtil.computeDistanceBetween(markerLoc, meLoc);


                            runOnUiThread(

                                    ()-> {
                                        boolean confirmed = false;
                                        if (meters < 100) {
                                            for(int j=0; j<holes.size() && !confirmed; j++){
                                                if(marker.getPosition().equals(holes.get(j).getPosition())){
                                                    confirmedHole = holes.get(j);
                                                    confirmed = true;
                                                }
                                            }
                                        }

                                        if(confirmedHole.getUserReporter() != user){
                                            confirmedBtn.setVisibility(View.VISIBLE);
                                        }else{
                                            confirmedBtn.setVisibility(View.INVISIBLE);
                                        }
                                    }

                            );


                    }


    }

    private double[] computeDistances() {

        double[] distances = new double[pointsHoles.size()];

        for(int i=0; i< pointsHoles.size(); i++){
            Marker marker = pointsHoles.get(i);
            LatLng markerLoc = marker.getPosition();
            LatLng meLoc = new LatLng(currentPosition.getLat(), currentPosition.getLng());

            double meters = SphericalUtil.computeDistanceBetween(markerLoc, meLoc);
            distances[i] = meters;

        }

        return distances;
    }

    public void shortestDistance(double[] distances){

        double shortest = distances[0];

        for (int i=0; i<distances.length; i++){
            if(distances[i] < shortest){
                shortest = distances[i];
            }
        }

        distanceTV.setText(shortest+" m");

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    public Position getCurrentPosition(){
        return currentPosition;
    }

    public  String getUser(){
        return user;
    }

    public void updateMarkersHoles(ArrayList<Hole> holes){

        runOnUiThread(

                ()->{

                    for(int i=0; i<holes.size(); i++){
                        Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(holes.get(i).getPosition().getLat(), holes.get(i).getPosition()
                                .getLng())));
                        pointsHoles.add(m);
                    }
        });
    }

    public void updateMarkers(ArrayList<Position> positions){

        runOnUiThread(

                ()->{

                    for(int i=0; i<points.size(); i++){
                        Marker m = points.get(i);
                        m.remove();
                    }
                    points.clear();

                    for(int i=0; i<positions.size(); i++){
                        Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(positions.get(i).getLat(), positions.get(i).getLng())));
                        points.add(m);
                    }
                }

        );

    }

    public void getAddress(){
        //Obtener la direccion de la calle a partir de la latitud y la longitud
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        currentHole.getPosition().getLat(), currentHole.getPosition().getLng(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    direccionTV.setText(DirCalle.getAddressLine(0));

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_add_hole, null);
        builder.setView(v);

        /*coordenadasTV.setText(getCurrentHole().getPosition().toString());
        getAddress();**/

        AlertDialog dialog = builder.create();
        dialog.show();

        /*okButton.setOnClickListener(
                (view) -> {
                    dialog.dismiss();
                }
        );**/

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.addBtn:

                currentHole = new Hole(UUID.randomUUID().toString(), currentPosition, false, this.user);
                showDialog();

                new Thread(

                        ()->{
                            holeLocationWorker.postHole();
                        }

                ).start();


                break;

            case R.id.confirmedBtn:

                confirmedHole.setConfirmed(true);

                Gson gson = new Gson();

                break;

        }

    }

    public void setHoleLocationWorker(HoleLocationWorker holeLocationWorker) {
        this.holeLocationWorker = holeLocationWorker;
    }

    public Hole getCurrentHole() {
        return currentHole;
    }

    public void setCurrentHole(Hole currentHole) {
        this.currentHole = currentHole;
    }
}
