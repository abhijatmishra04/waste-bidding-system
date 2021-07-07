package io.com.didingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

import java.util.HashMap;
import java.util.Map;

import io.com.didingapp.Volley.Singleton;
import io.com.didingapp.Volley.VolleyApi;
import io.com.didingapp.Volley.VolleySingleton;

import static io.com.didingapp.Volley.VolleyApi.BASE_URL;

public class otpVerification extends AppCompatActivity implements VolleyApi.ResponseListener{
    TextView et1, et2, et3, et4, et5, et6;
    Button verified;
    ProgressBar otpprgrs;
    String otp,Otpdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);

        findviewbyid();

    }

    public void findviewbyid() {
        verified = findViewById(R.id.button_verified);
        otpprgrs = findViewById(R.id.otp_progreshbar);

        et1 = findViewById(R.id.editText1);
        et2 = findViewById(R.id.editText2);
        et3 = findViewById(R.id.editText3);
        et4 = findViewById(R.id.editText4);
        et5 = findViewById(R.id.editText5);
        et6 = findViewById(R.id.editText6);

        et1.addTextChangedListener(new GenericTextWatcher(et1));
        et2.addTextChangedListener(new GenericTextWatcher(et2));
        et3.addTextChangedListener(new GenericTextWatcher(et3));
        et4.addTextChangedListener(new GenericTextWatcher(et4));
        et5.addTextChangedListener(new GenericTextWatcher(et5));
        et6.addTextChangedListener(new GenericTextWatcher(et6));

        VolleyApi.getInstance().loginCheck(this,this, Singleton.mobile);


    }


    public void OnClick(View view) {

        switch (view.getId()) {

            case R.id.button_verified:
                otpprgrs.setVisibility(View.VISIBLE);
                otp = et1.getText().toString() + et2.getText().toString() + et3.getText().toString() + et4.getText().toString() + et5.getText().toString() + et6.getText().toString();

                System.out.println("otppppp     ;" + otp);
                if (otp.length() == 6) {


                    if (Otpdata!=null) {
                        if (otp.equalsIgnoreCase(Otpdata)) {
                            activeUser(otpVerification.this);


                        }else {
                            Toast.makeText(otpVerification.this, "Wrong OTP !!!", Toast.LENGTH_SHORT).show();
                            otpprgrs.setVisibility(View.GONE);

                        }
                    }else {
                        Toast.makeText(otpVerification.this, "something wrong please try after sometime", Toast.LENGTH_SHORT).show();
                        otpprgrs.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "please enter a valid code", Toast.LENGTH_LONG).show();
                    otpprgrs.setVisibility(View.INVISIBLE);

                }

                break;
        }
    }

    @Override
    public void _onResponseError(Throwable e) {

    }

    @Override
    public void _onNext(String obj) {
        try {

            JSONObject obj1 = new JSONObject(obj);
            JSONArray jArray = obj1.getJSONArray("login");
            //int len = jArray.length();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jsonObject = jArray.getJSONObject(i);
                Otpdata = jsonObject.getString("otp");

            }




        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    public class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {

                case R.id.editText1:
                    if (text.length() == 1)
                        et2.requestFocus();
                    break;
                case R.id.editText2:
                    if (text.length() == 1)
                        et3.requestFocus();
                    else if (text.length() == 0)
                        et1.requestFocus();
                    break;
                case R.id.editText3:
                    if (text.length() == 1)
                        et4.requestFocus();
                    else if (text.length() == 0)
                        et2.requestFocus();
                    break;

                case R.id.editText4:
                    if (text.length() == 1)
                        et5.requestFocus();
                    else if (text.length() == 0)
                        et3.requestFocus();
                    break;

                case R.id.editText5:
                    if (text.length() == 1)
                        et6.requestFocus();
                    else if (text.length() == 0)
                        et4.requestFocus();
                    break;

                case R.id.editText6:
                    if (text.length() == 0)
                        et6.requestFocus();
                    break;


            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.right_in, R.anim.right_out);

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
                            otpprgrs.setVisibility(View.GONE);

                            startActivity(new Intent(otpVerification.this, LoginActivity.class));

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
                    params.put("mobile",Singleton.mobile);
                    params.put("isActive","1");

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

