package com.example.myname.maps;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.google.android.gms.maps.model.MarkerOptions.*;


public class MyMainActivity extends ActionBarActivity  {


    public static String RESULT_MAP_ACTIVITY;



    private static final int REQUEST_IMAGE = 100;
    private View.OnClickListener listener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent =
                                new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_IMAGE);
                    } catch (ActivityNotFoundException e) {
                        //Handle if no application exists
                    }
                }
            };
    Button captureButton;
    ImageView imageView;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_main);



        captureButton = (Button)findViewById(R.id.capture);
        captureButton.setOnClickListener(listener);

        imageView = (ImageView)findViewById(R.id.image);

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == REQUEST_IMAGE
                && resultCode == Activity.RESULT_OK) {
            //Process and display the image
            Bitmap userImage =
                    (Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(userImage);
        }
        if((requestCode==1) && (resultCode==RESULT_OK) && (data!=null)){
            String msg = data.getStringExtra(RESULT_MAP_ACTIVITY);

            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }

        if(requestCode == 42 && resultCode == RESULT_OK ){

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize =6;

            Bitmap bm = BitmapFactory.decodeFile(imagePath,opts);
            imageView.setImageBitmap(bm);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_main, menu);
        return true;
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
    @Override
    protected void onResume(){
        super.onResume();


    }

    /*onClick listener for the  button showing the google map*/
    public void lunchGoogleMapView(View view) {

        Intent mapViewIntent = new Intent(this,MapActivity.class);
        // mapViewIntent.putExtra();
        startActivityForResult(mapViewIntent,1);
    }

    public void takePicture(View view) {
        Uri imgUri = createPictureFile();
        imagePath = imgUri.getEncodedPath();

        if(imgUri!=null){

            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);

            if(intentCamera.resolveActivity(getPackageManager())!=null){
                startActivityForResult(intentCamera,42);
            }
        }


    }


    private Uri createPictureFile() {

        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {

            File pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            pictureDir = new File(pictureDir, "MapFragment");

            if (!pictureDir.exists()) {
                if (!pictureDir.mkdirs()) {
                    Log.d("storage error", "failed to create directory");
                    return null;
                }
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filename = pictureDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
            File imageFile = new File(filename);

            return Uri.fromFile(imageFile);
        } else {
            Log.d("media error", "No media mounted");
            return null;
        }
    }


}
