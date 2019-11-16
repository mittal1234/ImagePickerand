package com.example.imagepickersendemail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;

public class ImagePickerActivity extends AppCompatActivity {

    Bitmap bitmap;
    View alertView;
    private static final int PERMISSION_REQUEST_CAMERA = 100;
    private static final int MULTIPLE_PERMISSIONS = 10;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    AlertDialog.Builder alertDialogBuilder;
    private int isImg;
    AlertDialog alertDialog;
    AppCompatImageView imgHelpUs;
    AppCompatImageView imghelp;
    AppCompatTextView tvSendPicture;
    AppCompatButton imgSendEmail;
    LinearLayout linearLayoutimg;
    public Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker);

        imgHelpUs = findViewById(R.id.img_help_us);
        imghelp = findViewById(R.id.img_help);
        tvSendPicture = findViewById(R.id.tvSendPicture);
        linearLayoutimg = findViewById(R.id.ll_add_image);
        imgSendEmail = findViewById(R.id.btnSubmit);

        alertForImage();
        setUIListener();


    }

    public void setUIListener() {


        linearLayoutimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if ((ImagePickerActivity.this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) && (ImagePickerActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                        ImagePickerActivity.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                        ImagePickerActivity.this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA);
                    } else if (ImagePickerActivity.this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ImagePickerActivity.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                    } else if (ImagePickerActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ImagePickerActivity.this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA);
                    } else {
                        alertDialog.show();
                    }
                }


                imgSendEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        emailIntent.setType("application/image");
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{});
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test Subject");
                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From My App");
                        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(String.valueOf(imageUri)));
                        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    }
                });

            }
        });


    }


    public void alertForImage() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        alertView = layoutInflater.inflate(R.layout.imagedialogbox, null);
        alertView.setBackgroundColor(Color.TRANSPARENT);

        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(alertView);

        AppCompatButton btnCam = alertView.findViewById(R.id.btn_camera);
        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.hide();
                ImagePickerActivity.this.cameraIntent();
            }
        });
        AppCompatButton btnGall = alertView.findViewById(R.id.btn_gallery);
        btnGall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.hide();
                ImagePickerActivity.this.galleryIntent();
            }
        });
        AppCompatButton btnCancel = alertView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        //  alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            imageUri = data.getData();

            System.out.println(imageUri+"hello");

            if (isImg == 1) {

                if (requestCode == SELECT_FILE) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        imgHelpUs.setImageBitmap(bitmap);
                        imghelp.setVisibility(View.GONE);
                        tvSendPicture.setVisibility(View.GONE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == REQUEST_CAMERA) {
                    Bundle bundle = data.getExtras();
                    bitmap = (Bitmap) bundle.get("data");

                    imgHelpUs.setImageBitmap(bitmap);
                    imghelp.setVisibility(View.GONE);
                    tvSendPicture.setVisibility(View.GONE);

                }
            }


        }
        isImg = 0;
    }


    private void galleryIntent() {
        isImg = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);
        }
    }

    private void cameraIntent() {
        isImg = 1;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                alertDialog.show();
            } else {

            }
        }
    }
}
