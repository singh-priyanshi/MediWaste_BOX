package org.tensorflow.lite.examples.detection;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public class GALLERYActivity extends AppCompatActivity {

    final int REQUEST_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.img_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            //Toast.makeText(getApplicationContext(), "getSupportActionBar", Toast.LENGTH_LONG).show();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        launchGalleryIntent();
    }

    public void launchGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_EXTERNAL_STORAGE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    launchGalleryIntent();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == RESULT_OK) {

          //  final ImageView imageView = findViewById(R.id.image_view);
            final List<Bitmap> bitmaps = new ArrayList<>();
            List<Uri> uriList = new ArrayList<Uri>();
            ClipData clipData = data.getClipData();
            //Main2Activity ma =  new Main2Activity();
            //ma.initializeDetecor(this.getApplicationContext());

            if (clipData != null) {
                //multiple images selecetd
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    Log.i("URI", imageUri.toString());
                    uriList.add(imageUri);
//                    try {
//                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
////                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
////                        Bitmap bitmap = decodeSampledBitmapFromResource(imageUri,800,600 );
//                        Bitmap bitmap = ma.getInference(getApplicationContext(),imageUri);
//                        if(bitmap!=null){
//
//                            Log.i("image size",bitmap.getWidth() + "x" + bitmap.getHeight());
//                            bitmaps.add(bitmap);
//                        }
////                        Log.i("image size",bitmap.getWidth() + "x" + bitmap.getHeight());
////                        bitmaps.add(bitmap);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
                }
            } else {
                //single image selected
                Uri imageUri = data.getData();
                uriList.add(imageUri);
            }

            Intent intent = new Intent(GALLERYActivity.this,ExpansionPanelSampleActivityProgrammatically.class);
            intent.putParcelableArrayListExtra("uriList", (ArrayList<? extends Parcelable>) uriList);
            startActivity(intent);
            finish();

//                Log.i("URI", imageUri.toString());
//                try {
//                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
//
////                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
////                    Bitmap bitmap = decodeSampledBitmapFromResource(imageUri,800,600 );
//                    Bitmap bitmap = ma.getInference(getApplicationContext(),imageUri);
//                    if(bitmap!=null){
//
//                        Log.i("image size",bitmap.getWidth() + "x" + bitmap.getHeight());
//                        bitmaps.add(bitmap);
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }

            }

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    for (final Bitmap b : bitmaps) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                imageView.setImageBitmap(b);
//                            }
//                        });
//
//                        try {
//                            Thread.sleep(3000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }).start();
        }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    || (halfWidth / inSampleSize) >= reqWidth) {


                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromResource(Uri imageUri,
                                                  int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream stream = null;
        InputStream stream1 = null;
        Bitmap bm =null;
        try {
            stream = getContentResolver().openInputStream(imageUri);
            bm = BitmapFactory.decodeStream(stream,null,options);
//        Log.i("options",bm.getWidth()+ "x" + bm.getHeight());
//        BitmapFactory.decodeResource(stream,options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            Log.i("options", options.inSampleSize + "  "+options.outWidth + "x" + options.outHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;


            bm = BitmapFactory.decodeStream(stream1,null,options);
            try {
                stream1 = getContentResolver().openInputStream(imageUri);
                bm = BitmapFactory.decodeStream(stream1,null,options);
                Log.i("bm",bm.getWidth() + "X" + bm.getHeight());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bm;

    }
}