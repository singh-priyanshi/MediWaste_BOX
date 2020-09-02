package org.tensorflow.lite.examples.detection;

import android.app.AlertDialog;
import android.net.NetworkInfo.DetailedState;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public String text;
    TextView textView;
    public EditText editText;
    public Button img_but,real, btn1;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);


        img_but = (Button) findViewById(R.id.img_but);
        img_but.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();

            }
        });

        real = (Button) findViewById(R.id.real);
        real.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toy = new Intent(MainActivity.this, DetectorActivity.class);
                startActivity(toy);
            }
        });

        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                finish();
                System.exit(0);
            }
        });


    }

    private void selectImage() {
        CharSequence[] items = {
                "Take Photo", "Choose from Library",
                "Cancel"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                // requestStoragePermission(true);
                Intent toy1 = new Intent(MainActivity.this, ChooseModel.class);
                startActivity(toy1);
            } else if (items[item].equals("Choose from Library")) {
                Intent toy1 = new Intent(MainActivity.this, GALLERYActivity.class);
                startActivity(toy1);
                // requestStoragePermission(false);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()){
            case R.id.menuAbout:
                Intent i = new Intent(MainActivity.this, ABOUT.class);
                startActivity(i);
                break;

            case R.id.menuSettings:
                Toast.makeText(this, "You clicked settings", Toast.LENGTH_SHORT).show();
                break;
        }

        //noinspection SimplifiableIfStatement
      /*  if (id == R.id.action_settings) {
            return true;
        } */

        return super.onOptionsItemSelected(item);
    }

}