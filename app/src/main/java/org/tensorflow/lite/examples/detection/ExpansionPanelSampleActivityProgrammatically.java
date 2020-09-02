package org.tensorflow.lite.examples.detection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import  org.tensorflow.lite.examples.detection.expansionpanel.ExpansionHeader;
import  org.tensorflow.lite.examples.detection.expansionpanel.ExpansionLayout;
import  org.tensorflow.lite.examples.detection.expansionpanel.viewgroup.ExpansionLayoutCollection;

import org.tensorflow.lite.examples.detection.LableCountBitmap;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.tensorflow.lite.examples.detection.Utils.dpToPx;

public class ExpansionPanelSampleActivityProgrammatically extends AppCompatActivity {

    ViewGroup dynamicLayoutContainer;
    //private ProgressBar spinner=(ProgressBar)findViewById(R.id.progressBar1);
    // spinner.setVisibility(View.VISIBLE);
    // spinner.setVisibility(View.GONE);

    private FloatingActionButton button_REPORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expansion_panel_sample_programmatically);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
           // Toast.makeText(getApplicationContext(), "getSupportActionBar", Toast.LENGTH_LONG).show();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //new added
        button_REPORT =findViewById(R.id.button_REPORT);
        button_REPORT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "getSupportActionBar", Toast.LENGTH_LONG).show();
                Intent toy = new Intent(ExpansionPanelSampleActivityProgrammatically.this, ReporttActivity.class);
                startActivity(toy);
            }
        });

        this.dynamicLayoutContainer = findViewById(R.id.dynamicLayoutContainer);
        List<Uri>uriList = getIntent().getParcelableArrayListExtra("uriList");
        List<ExpansionLayout> expansionLayoutList = new ArrayList<ExpansionLayout>();
        final ExpansionLayoutCollection expansionLayoutCollection = new ExpansionLayoutCollection();
        ObjectDetectionInference objectDetectionInference = new ObjectDetectionInference(getApplicationContext());
        for(Uri uri:uriList)
        {
            LableCountBitmap lableCountBitmap= objectDetectionInference.getInference(getApplicationContext(),uri);
            Log.i("hashmap",lableCountBitmap.getLabelCount().toString());
            final ExpansionLayout ex = addDynamicLayout(lableCountBitmap.getBitmap(),lableCountBitmap.getLabelCount());
            expansionLayoutList.add(ex);
        }
        expansionLayoutCollection.addAll(expansionLayoutList);
        expansionLayoutCollection.openOnlyOne(true);
    }

    public ExpansionLayout addDynamicLayout(Bitmap bitmap,HashMap<String,Integer> labelCount) {

        final ExpansionHeader expansionHeader = createExpansionHeader();
        dynamicLayoutContainer.addView(expansionHeader, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        final ExpansionLayout expansionLayout = createExpansionLayout(bitmap,labelCount);
        dynamicLayoutContainer.addView(expansionLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        expansionHeader.setExpansionLayout(expansionLayout);

        return expansionLayout;

    }

    @SuppressLint("ResourceType")
    @NonNull
    private ExpansionLayout createExpansionLayout(Bitmap bitmap,HashMap<String,Integer> labelCount) {

        final ExpansionLayout expansionLayout = new ExpansionLayout(this);


        final RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setBackgroundColor(Color.parseColor("#c6e3b3"));
        expansionLayout.addView(relativeLayout, ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(this, 48)); //equivalent to addView(linearLayout)



        RelativeLayout.LayoutParams textViewParam2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);


        RelativeLayout.LayoutParams imageParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        int id = 3;
        final ImageView imageView = new AppCompatImageView(this);
        imageView.setId(id++);
        imageView.setBackgroundColor(Color.parseColor("#EEEEEE"));
        imageView.setMaxWidth(250);
//        imageParam.addRule(RelativeLayout.CENTER_VERTICAL);
        imageParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//        imageParam.addRule(RelativeLayout.);
        if(bitmap != null )
        {
            imageView.setImageBitmap(bitmap);
        }
        imageView.setLayoutParams(imageParam);
        relativeLayout.addView(imageView, imageParam);

        for(HashMap.Entry<String,Integer> label:labelCount.entrySet()) {
            if (label.getValue() > 0) {

                RelativeLayout.LayoutParams textViewParam = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                final TextView text1 = new TextView(this);
                text1.setLayoutParams(textViewParam);
                text1.setText(label.getKey() + " " + label.getValue());
                text1.setId(id);

                text1.setGravity(Gravity.CENTER);
                text1.setTextColor(Color.parseColor("#3E3E3E"));
                text1.setBackgroundColor(Color.parseColor("#EEEEEE"));
                textViewParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                if(id>4)
                textViewParam.addRule(RelativeLayout.BELOW,id-1);

                text1.setWidth(250);
//                Toast.makeText(getApplicationContext(),"Content " + i ,Toast.LENGTH_SHORT).show();
                relativeLayout.addView(text1, textViewParam);
            }
        }

        return expansionLayout;
    }

    @NonNull
    private ExpansionHeader createExpansionHeader() {
        final ExpansionHeader expansionHeader = new ExpansionHeader(this);
        expansionHeader.setBackgroundColor(Color.WHITE);

        expansionHeader.setPadding(dpToPx(this, 16), dpToPx(this, 8), dpToPx(this, 16), dpToPx(this, 8));

        final RelativeLayout layout = new RelativeLayout(this);
        expansionHeader.addView(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //equivalent to addView(linearLayout)

        //image
        final ImageView expansionIndicator = new AppCompatImageView(this);
        expansionIndicator.setImageResource(R.drawable.ic_expansion_header_indicator_grey_24dp);
        final RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layout.addView(expansionIndicator, imageLayoutParams);

        //label
        final TextView text = new TextView(this);
        text.setText("Image");
        text.setTextColor(Color.parseColor("#3E3E3E"));

        final RelativeLayout.LayoutParams textLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        textLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

        layout.addView(text, textLayoutParams);

        expansionHeader.setExpansionHeaderIndicator(expansionIndicator);
        return expansionHeader;
    }
}
