package com.example.drawknot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    int defaultColor;
    int eraserColor;
    int defaultsize;
    SignatureView signatureView;
    ImageButton pencil,eraser,info,color,clear,save;
    SeekBar size;
    TextView sizetxt;

    private static String filename;
    File path=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Drawknot");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signatureView=findViewById(R.id.signature_view);
        pencil=findViewById(R.id.pencil);
        eraser=findViewById(R.id.eraser);
        info=findViewById(R.id.info);
        color=findViewById(R.id.colorpick);
        clear=findViewById(R.id.clear);
        save=findViewById(R.id.save);
        size=findViewById(R.id.size);
        sizetxt=findViewById(R.id.sizetxt);

        askPermission();

        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String date=format.format(new Date());
        filename= path +"/"+"Drawknot"+date+".png";

        if(!path.exists()){
            path.mkdirs();


        }


        defaultColor= ContextCompat.getColor(MainActivity.this,R.color.black);
        eraserColor= ContextCompat.getColor(MainActivity.this,R.color.white);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MainActivity2.class));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(!signatureView.isBitmapEmpty()){
                  try {
                      saveImage();
                  } catch (IOException e) {
                      e.printStackTrace();
                      Toast.makeText(MainActivity.this, "Couldn't Save", Toast.LENGTH_SHORT).show();
                  }

              }  else{

                  Toast.makeText(MainActivity.this, "Can't save, Your canvas is empty", Toast.LENGTH_SHORT).show();
              }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signatureView.clearCanvas();
            }
        });

        pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signatureView.setPenColor(defaultColor);
                signatureView.setPenSize(defaultsize);
                pencil.setImageResource(R.drawable.longpencil);
                eraser.setImageResource(R.drawable.shorteraser);


            }
        });
        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signatureView.setPenColor(eraserColor);
                signatureView.setPenSize(70);
                pencil.setImageResource(R.drawable.shortpencil);
                eraser.setImageResource(R.drawable.longeraser);
            }
        });

        size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sizetxt.setText(i+"dp");
                signatureView.setPenSize(i);
                defaultsize=i;
                size.setMax(100);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker();
            }
        });

    }

    private void saveImage() throws IOException {
        File file=new File(filename);
        Bitmap bitmap=signatureView.getSignatureBitmap();
        ByteArrayOutputStream bos =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,0,bos);
        byte[] bitmapData=bos.toByteArray();

        FileOutputStream fos=new FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
        Toast.makeText(this, "Painting Saved!", Toast.LENGTH_SHORT).show();



    }

    private void openColorPicker() {

        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
               defaultColor=color;
               signatureView.setPenColor(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // cancel was selected by the user
            }


        });
        dialog.show();
    }

    private void askPermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();

            }
        }).check();

    }
}