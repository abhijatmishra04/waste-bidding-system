package io.com.didingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.com.didingapp.Volley.Singleton;
import io.com.didingapp.Volley.VolleyApi;
import io.com.didingapp.Volley.VolleySingleton;

import static io.com.didingapp.Volley.VolleyApi.BASE_URL;

public class signupActivity extends AppCompatActivity implements VolleyApi.ResponseListener {

    String img = "";
    int flag = 0;
    TextView Login;
    Button signIn;
    ImageView addphoto;
    RelativeLayout relativeLayout;
    String isEdit = "0", id, isActive;
    EditText firstnm, lastnm, email, phone, password;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        findviewbyid();

        if (getIntent().getStringExtra("isEdit") != null) {
            isEdit = getIntent().getStringExtra("isEdit");
//
            if (isEdit.equalsIgnoreCase("2")) {
                relativeLayout.setVisibility(View.GONE);
                Login.setVisibility(View.GONE);
                signIn.setText("Update");
                firstnm.setText(Singleton.firstName);
                lastnm.setText(Singleton.lastname);
                email.setText(Singleton.email);
                password.setText(Singleton.password);
                Utility.setImage(addphoto, Singleton.img);

            }
//            System.out.println("is edit    ;"+isEdit+"jdjkd");
////            setData();
//        }else {
//            isEdit="0";

        }
    }

    public void findviewbyid() {

        relativeLayout = findViewById(R.id.relativeLayout6);

        Login = findViewById(R.id.Log_in);
        signIn = findViewById(R.id.button_signin);

        addphoto = findViewById(R.id.add_photo);
        firstnm = findViewById(R.id.first_name);
        lastnm = findViewById(R.id.last_name);
        email = findViewById(R.id.email_id);
        phone = findViewById(R.id.mob_num);
        password = findViewById(R.id.password_reg);

    }


    public void OnClick(View view) {

        switch (view.getId()) {

            case R.id.Log_in:
                startActivity(new Intent(signupActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                break;
            case R.id.button_signin:
//                startActivity(new Intent(signupActivity.this, otpVerification.class));


                saving();

                break;
            case R.id.add_photo:
                boolean rs = Utility.checkCameraPermission(signupActivity.this);
                System.out.println("::::::::::::::::: :: " + rs);
                if (rs) {
                    chooseProfilePic();
                }

        }
    }

    private void saving() {
        if (checkValidation1()) {
            Singleton.firstName = firstnm.getText().toString().trim();
            Singleton.lastname = lastnm.getText().toString().trim();
            Singleton.email = email.getText().toString().trim();
            Singleton.password = password.getText().toString().trim();


            if (isEdit.equalsIgnoreCase("2")) {


                flag = 3;
                VolleyApi.getInstance().updateprofile(this, this);


            } else {

                flag = 1;
                VolleyApi.getInstance().loginCheck(this, this, Singleton.mobile);
                Singleton.mobile = phone.getText().toString().trim();


            }


        }
    }



    boolean checkValidation1() {
        boolean ret = true;
        if (!Utility.hasText(firstnm)) ret = false;
        if (!Utility.hasText(lastnm)) ret = false;
        if (!Utility.hasText(password)) ret = false;

        if (!isEdit.equalsIgnoreCase("2")) {
            if (!Utility.isPhoneNumber(phone, true)) ret = false;

        }
        if (!Utility.isEmailAddress(email, true)) ret = false;
        return ret;
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

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        img = getStringImage(bm);
        setPhoto(bm);
        Singleton.img = img;

    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }


    private void setPhoto(Bitmap bitmap) {
        addphoto.setImageBitmap(bitmap);
        System.out.println("imageee :" + img);

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

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
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
        setPhoto(thumbnail);
        Singleton.img = img;

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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);

    }

    @Override
    public void _onResponseError(Throwable e) {


    }

    @Override
    public void _onNext(String obj) {
        switch (flag) {
            case 1:
                try {

                    JSONObject obj1 = new JSONObject(obj);
                    JSONArray jArray = obj1.getJSONArray("login");
                    //int len = jArray.length();

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonObject = jArray.getJSONObject(i);
                        id = jsonObject.getString("id");
                        isActive = jsonObject.getString("isActive");

                    }


                    Toast.makeText(this, "Number Already Exist", Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    e.printStackTrace();


                    flag = 2;
                    VolleyApi.getInstance().registration(this, this);


                }
                break;
            case 2:

                try {
                    JSONObject obj1 = new JSONObject(obj);
                    JSONArray jArray = obj1.getJSONArray("msg");


                    startActivity(new Intent(signupActivity.this, otpVerification.class));


                } catch (JSONException e) {
                    e.printStackTrace();


                }
                break;
            case 3:

                try {
                    JSONObject obj1 = new JSONObject(obj);
                    JSONArray jArray = obj1.getJSONArray("msg");
                    activeUser(this);



                } catch (JSONException e) {
                    e.printStackTrace();


                }
                break;


        }

    }

    public void activeUser(final Activity activity) {
        final ProgressDialog mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + "Login/activeuser",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response", response);
                            mProgressDialog.dismiss();

                            Toast.makeText(signupActivity.this, "Profile updated", Toast.LENGTH_LONG).show();
                            onBackPressed();
                            finish();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error", error.toString());
                            mProgressDialog.dismiss();

                            //username.setError(getString(R.string.error_incorrect_username));

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("mobile", Singleton.mobile);
                    params.put("isActive", "1");

                    return params;
                }


            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            VolleySingleton.getInstance(activity).addToRequestQueue(stringRequest);
        } catch (Exception e) {
            mProgressDialog.dismiss();
            e.printStackTrace();
        }
    }
}
