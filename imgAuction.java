package io.com.didingapp.createbid;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import io.com.didingapp.R;
import io.com.didingapp.Utility;
import io.com.didingapp.Volley.VolleyApi;
import io.com.didingapp.main.view.dashBoard;

public class imgAuction extends AppCompatActivity  implements VolleyApi.ResponseListener{
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
//    String [] img ;
    String img = "";
    int j = 0;
    int i=0;
    ProgressBar imgpro;
    Button uploadimg;



    // Log tag that is used to distinguish log info.
    private final static String TAG_BROWSE_PICTURE = "BROWSE_PICTURE";

    // Used when request action Intent.ACTION_GET_CONTENT
    private final static int REQUEST_CODE_BROWSE_PICTURE = 1;

    // Used when request read external storage permission.
    private final static int REQUEST_PERMISSION_READ_EXTERNAL = 2;

    // The image view that used to display user selected image.
    private ImageView selectedPictureImageView;

    // Save user selected image uri list.
    private ArrayList<String> userSelectedImageUriList = null;

    // Currently displayed user selected image index in userSelectedImageUriList.
    private int currentDisplayedUserSelectImageIndex = 0;
    Bitmap bm,thumbnail;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auction_img);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);

        selectedPictureImageView = (ImageView) findViewById(R.id.selected_picture_imageview);
        uploadimg  = findViewById(R.id.upload);

        Button choosePictureButton = (Button) findViewById(R.id.choose_picture_button);
        Button showSelectedPictureButton = (Button) findViewById(R.id.show_selected_picture_button);
        imgpro = findViewById(R.id.progrimg);

        userSelectedImageUriList = new ArrayList<>();


//        if(userSelectedImageUriList.size()==0){
//            uploadimg.setText("skip");
//        }





        uploadimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgpro.setVisibility(View.VISIBLE);
                if(userSelectedImageUriList.size()>0){
                    for (i=0;i<userSelectedImageUriList.size();i++){


                        System.out.println("afasda: "+img);
                        VolleyApi.getInstance().insertAuctionImg(imgAuction.this, imgAuction.this,userSelectedImageUriList.get(i));
                    }

                }else {
                    Toast.makeText(getBaseContext(),"please add atleast one",Toast.LENGTH_LONG).show();
                    imgpro.setVisibility(View.GONE);

                }


            }
        });
        choosePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean rs = Utility.checkCameraPermission(imgAuction.this);
                System.out.println("::::::::::::::::: :: " + rs);
                if (rs) {
                    chooseProfilePic();
                }
            }
        });

        showSelectedPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( userSelectedImageUriList.size()!=0 ){

                    Utility.setImage(selectedPictureImageView,userSelectedImageUriList.get(j));
                    j++;
                    if(j==userSelectedImageUriList.size()){
                        j=0;

                    }

                }
                else {
                    Toast.makeText(imgAuction.this,"please add atleast one",Toast.LENGTH_LONG).show();

                }


            }
        });
    }




    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }


    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        img = getStringImage(bm);
        userSelectedImageUriList.add(img);
        Utility.setImage(selectedPictureImageView,img);
        uploadimg.setText("Upload");
    }
    private void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        img = getStringImage(thumbnail);
        userSelectedImageUriList.add(img);


        Utility.setImage(selectedPictureImageView,img);
        uploadimg.setText("Upload");
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }


    private void setPhoto(Bitmap bitmap,ImageView imageView) {

        imageView.setImageBitmap(bitmap);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 100:
               /* if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }*/
                break;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                }
                break;

            default:

                if (resultCode == Activity.RESULT_OK) {
                    if (requestCode == SELECT_FILE)
                        onSelectFromGalleryResult(data);
                    else if (requestCode == REQUEST_CAMERA)
                        onCaptureImageResult(data);

                }

                break;


        }

    }

    public void chooseProfilePic() // replcae method name with chooseProfilePic

    {
        final CharSequence[] items = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Profile Picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                boolean result = Utility.checkPermissions(getApplicationContext());

                if (items[item].equals("Camera")) {
                    userChoosenTask = "Camera";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Gallery")) {
                    userChoosenTask = "Gallery";
                    if (result)
                        galleryIntent();

                }
            }
        });
        builder.show();
    }




    @Override
    public void _onResponseError(Throwable e) {

    }

    @Override
    public void _onNext(String obj) {
        try {
            JSONObject obj1 = new JSONObject(obj);
            JSONArray jArray = obj1.getJSONArray("msg");
            JSONObject obj2 = jArray.getJSONObject(0);


            if (obj2.getString("status").equalsIgnoreCase("200")) {

                for (i=0;i<userSelectedImageUriList.size();i++){

                    Toast.makeText(this, "Successesfully insert Image :" + i, Toast.LENGTH_SHORT).show();

                }
                imgpro.setVisibility(View.GONE);

            }
            else {
                Toast.makeText(this, "Unsuccessesfully", Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(imgAuction.this, dashBoard.class));

    } catch (
    JSONException e) {
        e.printStackTrace();

    }

    }
}
