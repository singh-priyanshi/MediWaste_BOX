package org.tensorflow.lite.examples.detection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.widget.Toast;

import org.tensorflow.lite.examples.detection.env.BorderedText;
import org.tensorflow.lite.examples.detection.env.ImageUtils;
import org.tensorflow.lite.examples.detection.env.Logger;
import org.tensorflow.lite.examples.detection.tflite.Classifier;
import org.tensorflow.lite.examples.detection.tflite.TFLiteObjectDetectionAPIModel;
import org.tensorflow.lite.examples.detection.tracking.MultiBoxTracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
//import android.support.v4.util.Pair;


public class ObjectDetectionInference {
    private static final Logger LOGGER = new Logger();

    private static final int TF_OD_API_INPUT_SIZE = 300;
    private static final boolean TF_OD_API_IS_QUANTIZED = false;
    private static final String TF_OD_API_MODEL_FILE = "detect.tflite";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt";
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    private static final boolean MAINTAIN_ASPECT = false;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;

    private Integer sensorOrientation=0;

    private Classifier detector;

    private long lastProcessingTimeMs;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private MultiBoxTracker tracker;
    float textSizePx;
    private BorderedText borderedText;
    private int previewWidth;
    private int previewHeight;
    private int cropSize = TF_OD_API_INPUT_SIZE;
    private HashMap<String,Integer> labelCount ;


    public ObjectDetectionInference(Context context) {

        textSizePx =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,TEXT_SIZE_DIP , context.getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);
        tracker = new MultiBoxTracker(context);


        try {
            detector =
                    TFLiteObjectDetectionAPIModel.create(
                            context.getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_INPUT_SIZE,
                            TF_OD_API_IS_QUANTIZED);
            cropSize = TF_OD_API_INPUT_SIZE;
        }
        catch (final IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing classifier!");
            Toast.makeText(context, "Classifier could not be initialized", Toast.LENGTH_SHORT).show();
//                finish();
        }
    }


    public LableCountBitmap getInference(Context context, Uri imageUri){
        initializeHashMap(context);

        rgbFrameBitmap = decodeSampledBitmapFromResource(context,imageUri,800,600 );
        rgbFrameBitmap = convertToMutable(rgbFrameBitmap);
        previewWidth=rgbFrameBitmap.getWidth();
        previewHeight=rgbFrameBitmap.getHeight();
        Log.i("previewWidth" , previewWidth +"x"+previewHeight);


        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,cropSize, cropSize, sensorOrientation, MAINTAIN_ASPECT
                );
//        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(croppedBitmap);
//        Log.i("check",croppedBitmap.getHeight() +"X" + croppedBitmap.getWidth() +" " + rgbFrameBitmap.getHeight()+"X"+rgbFrameBitmap.getWidth());
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);

        Log.i("frameToCropTransform", String.valueOf(frameToCropTransform));
        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);

        List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
        Log.i("result",results.toString());
        List<Classifier.Recognition> newresults = removeDuplicates(results);
        Log.i("newresult",newresults.toString());
//        Bitmap cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
        final Canvas canvas2 = new Canvas(cropCopyBitmap);
        final Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);

        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
        switch (MODE) {
            case TF_OD_API:
                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                break;
        }

        final List<Classifier.Recognition> mappedRecognitions =
                new LinkedList<Classifier.Recognition>();


        for (final Classifier.Recognition result : newresults) {

            final RectF location = result.getLocation();
            Log.i("Location", location.toString());
            if (location != null && result.getConfidence() >= minimumConfidence) {
                canvas2.drawRect(location, paint);

                Matrix cropToFrameTransform = new Matrix();
                cropToFrameTransform.mapRect(location);
                Log.i("cropToFrameTransform",cropToFrameTransform.toString());
                result.setLocation(location);
                if (labelCount.containsKey(result.getTitle()))
                    labelCount.put(result.getTitle(), labelCount.get(result.getTitle()) + 1);

                Log.i("result", result.toString());
                mappedRecognitions.add(result);
            }
        }

        final Canvas canvas1 = new Canvas(rgbFrameBitmap);
        Log.i("check",croppedBitmap.getHeight() +"X" + croppedBitmap.getWidth() +" " + rgbFrameBitmap.getHeight()+"X"+rgbFrameBitmap.getWidth());

        tracker.processResults(mappedRecognitions);

        tracker.draw(canvas1,1);
        // SystemClock.sleep(1000);
        String fname = imageUri.getPath();
        saveImage(rgbFrameBitmap, fname);
        // SystemClock.sleep(1000);
        saveImage(croppedBitmap, fname+"_detect");
//       Pair<HashMap<Strng, Integer>,Bitmap>  pair =  new Pair<HashMap<Strng, Integer>, Bitmap>();


//        HashMap<HashMap<String, Integer>, Bitmap> hashMapBitmapHashMap = new HashMap<>();
//        hashMapBitmapHashMap.put(labelCount,rgbFrameBitmap);

        LableCountBitmap labelCountBitmap = new LableCountBitmap(labelCount,rgbFrameBitmap);
//            labelCountBitmap


        return labelCountBitmap;
    }

    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }

    //    String fname = Uri.getPath();
    public void saveImage(Bitmap bitmap, String fname) {

        Log.i("saveImage","Save mage");
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String fname = "Shutta_"+ timeStamp +".jpg";

        File file = new File(myDir, fname+".jpg");
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            Log.i("file path",file.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
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

    public Bitmap decodeSampledBitmapFromResource(Context context,Uri imageUri, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream stream = null;
        InputStream stream1 = null;
        Bitmap bm =null;
        try {
            stream = context.getContentResolver().openInputStream(imageUri);
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
                stream1 = context.getContentResolver().openInputStream(imageUri);
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

    public enum DetectorMode {
        TF_OD_API;
    }
    public static  List<Classifier.Recognition> removeDuplicates(List<Classifier.Recognition> list)
    {
        Map<RectF,Classifier.Recognition> map = new HashMap<RectF,Classifier.Recognition>();
        for(Classifier.Recognition obj : list )
        {
            map.put(obj.getLocation(),obj);
        }
        list.clear();
        list.addAll(map.values());
        return list;


//        List<T> newList =  new ArrayList<>();
//        for(T i:t )
//        return list;
    }
    public void initializeHashMap(Context context)
    {
        InputStream labelsInput = null;
        labelCount = new HashMap<>();
        try {
            labelsInput = context.getAssets().open(TF_OD_API_LABELS_FILE.split("file:///android_asset/")[1] );
            BufferedReader br = new BufferedReader(new InputStreamReader(labelsInput));
            String line;
            while ((line = br.readLine()) != null) {
                Log.i("label initailized with ",line + " " + 0);
                if(!line.contains("???"))
                    labelCount.put(line,0);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}