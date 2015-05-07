package edu.csus.teamname.motrails;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AddWaypoint extends ActionBarActivity {
    public static final int CAMERA_REQUEST = 69;
    public static final int GALLERY_REQUEST = 420;
    public static final String TITLE_STRING = "TITLE";
    public static final String DESCRIPTION_STRING = "DESCRIPTION";
    public static final String PHOTO_STRING = "PHOTO";
    public ImageView waypointPhoto;
    String mCurrentPhotoPath;
    LocationManager lm;
    Location loc;
    float lat;
    float lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_waypoint);

        //loc = (Location) getIntent().getExtras().getSerializable("location");
        //Log.d("location", loc.toString());
        lat = getIntent().getExtras().getFloat("lat");
        lng = getIntent().getExtras().getFloat("lng");

        Log.d("waypointLng", Float.toString(lng));
        Log.d("waypointLat", Float.toString(lat));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_waypoint, menu);
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

    public void takePhoto(View v){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
        //dispatchTakePictureIntent();
    }

    public void addPhoto(View v){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("CameraReturnResultCode", String.valueOf(resultCode));
        Log.d("CameraReturnRequestCode", String.valueOf(requestCode));
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Log.d("WaypointActivity", "Got Successful Camera Return");

            ImageView iv = (ImageView) findViewById(R.id.waypointPhotoImageView);
            iv.setImageBitmap(photo);
        }
        else if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            mCurrentPhotoPath = filePath;

            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
            ImageView imageView = (ImageView) findViewById(R.id.waypointPhotoImageView);
            imageView.setImageBitmap(yourSelectedImage);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    public void finish(){

        Intent returnIntent = new Intent();
        returnIntent.putExtra("title", ((TextView)findViewById(R.id.title)).getText());
        returnIntent.putExtra("description", ((TextView)findViewById(R.id.description)).getText());
        returnIntent.putExtra("photoLocation", mCurrentPhotoPath);
    }
}
