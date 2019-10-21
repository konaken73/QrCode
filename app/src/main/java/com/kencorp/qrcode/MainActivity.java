package com.kencorp.qrcode;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

/**
 * By Kenneth Ze Ondoua
 * **/



public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);

        setContentView(scannerView);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        if(checkPermission()){

            Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show();
        }else{
            requestPermission();
        }
    }

    private boolean checkPermission(){

        return (ContextCompat.checkSelfPermission(MainActivity.this,
                CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,new String[]{CAMERA},REQUEST_CAMERA);

    }

    public void onRequestPermissionsResult(int requestCode,String permission[] ,int grantedResults[])
    {
        switch (requestCode)
        {
            case REQUEST_CAMERA :
                if(grantedResults.length >0)
                {
                    boolean cameraAccepted = grantedResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted)
                    {
                        Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if(shouldShowRequestPermissionRationale(CAMERA)){

                                displayAlertMessage("You need to allow access for both permissions",new DialogInterface.OnClickListener()
                                {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[]{CAMERA},REQUEST_CAMERA);
                                    }
                                });

                                return;
                            }
                        }
                    }
                }
                break;
        }

    }

   @Override
   public void onResume()
   {
       super.onResume();

       if(checkPermission()){

           if(scannerView == null)
           {
               scannerView = new ZXingScannerView(this);
               setContentView(scannerView);
           }

           scannerView.setResultHandler(this);
           scannerView.startCamera();

       }else{
         requestPermission();
       }

   }


   @Override
   public void onDestroy(){
        super.onDestroy();
        scannerView.stopCamera();
   }

   public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(message)
                .setPositiveButton("ok",listener)
                .setNegativeButton("Cancel",null)
                .create()
                 .show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void handleResult(final Result rawResult) {

        final String scanResult = rawResult.getText();


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Scan Result")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        scannerView.resumeCameraPreview(MainActivity.this);
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
                        startActivity(intent);
                    }
                })
                .setMessage(scanResult)
                .create()
                .show();

    }
}
