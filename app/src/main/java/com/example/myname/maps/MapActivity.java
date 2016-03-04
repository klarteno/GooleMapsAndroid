package com.example.myname.maps;

import android.media.ExifInterface;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MapActivity extends ActionBarActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener,View.OnClickListener {


    private GoogleMap gmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.gmap);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gmap = googleMap;
        gmap.setOnMarkerClickListener(this);

        findImagesWithGeoTag();

        Marker marker_one = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(10, 10))
                .draggable(true)
                .title("New Marker"));
        marker_one.showInfoWindow();

        //  type can be "hybrid","Normal","Satellite","Terrain","None"
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.setMyLocationEnabled(true);

       /* IndoorBuilding building = mMap.getFocusedBuilding();
        if (building == null) {
            return null;
        }
        return building.getLevels().get(building.getActiveLevelIndex());*/

    }

    @Override
    protected void onResume(){
        super.onResume();

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(status== ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Google Play Services Are Available", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Google Play Services Are Not Available", Toast.LENGTH_LONG).show();

        }

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //lunch activity to show image and maybe some metadata like date
        Toast.makeText(this,"ImageName:" +marker.getTitle(),Toast.LENGTH_LONG).show();

        //returns true tells android you handled the event
        return true;
    }

    private void findImagesWithGeoTag() {

        String storageState = Environment.getExternalStorageState();
        if(storageState.equals(Environment.MEDIA_MOUNTED)){

            File pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            pictureDir = new File(pictureDir, "MapFragment");

            if(pictureDir.exists()){
                ArrayList<File> allFiles = new ArrayList<File>();
                File[] files = pictureDir.listFiles();
                for (File file : files) {
                    if(file.getName().endsWith(".jpg")){

                        LatLng pos = getLatLongFromExif(file.getAbsolutePath());

                        if(pos != null) {
                            addGeoTag(pos, file.getName());
                        }
                    }
                }
            }
        }

    }

    private LatLng getLatLongFromExif(String filename) {
        float latlo[] = new float[2];
        LatLng pos = null;
        try {
            ExifInterface exifi = new ExifInterface(filename);
            if(exifi.getLatLong(latlo)){
                pos = new LatLng(latlo[0], latlo[1]);
            }else{
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return pos;
    }

    private void addGeoTag(LatLng pos, String filename) {
        gmap.setMyLocationEnabled(true);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos , 14));
        gmap.addMarker(new MarkerOptions()
                .position(pos))
                .setTitle(filename);
    }

}
