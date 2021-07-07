package io.com.didingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.com.didingapp.Volley.Singleton;
import io.com.didingapp.Volley.VolleyApi;
import io.com.didingapp.main.view.dashBoard;

public class LoginActivity extends AppCompatActivity implements VolleyApi.ResponseListener {
    TextView signUp;
    Button Loging;
    EditText mobilenumber, Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);

            findviewbyid();



    }

    public void findviewbyid() {

        signUp = findViewById(R.id.sign_up);
        Loging = findViewById(R.id.button_Login);
        mobilenumber = findViewById(R.id.number);
        Password = findViewById(R.id.password);

    }


    public void OnClick(View view) {

        switch (view.getId()) {

            case R.id.sign_up:
                startActivity(new Intent(LoginActivity.this, signupActivity.class));
                break;
            case R.id.button_Login:
                if (checkValidation1()) {
                    System.out.println("mobile :" + Singleton.mobile);
                    Singleton.mobile = mobilenumber.getText().toString().trim();
                    Singleton.password = Password.getText().toString().trim();
                    fetching();
                }
//                dialog cdd = new dialog(LoginActivity.this);
//                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                cdd.show();
                break;
        }
    }

    private void fetching() {
        VolleyApi.getInstance().login(LoginActivity.this, this, "" + Singleton.mobile, "" + Singleton.password);
    }


    boolean checkValidation1() {
        boolean ret = true;
        if (!Utility.hasText(mobilenumber)) ret = false;
        if (!Utility.hasText(Password)) ret = false;
        return ret;
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

                JSONObject json_data = jArray.getJSONObject(i);


                Utility.addPreferences(this, "email", json_data.getString("email"));
                Utility.addPreferences(this, "mobile", json_data.getString("mobile"));
                Utility.addPreferences(this, "first name", json_data.getString("first_name"));
                Utility.addPreferences(this, "last name", json_data.getString("last_name"));
                Utility.addPreferences(this, "img", json_data.getString("photo"));
                Utility.addPreferences(this, "password", json_data.getString("password"));
//                Utility.addPreferences(this,Singleton.rating1,json_data.getString("rating"));
                Utility.addPreferences(this, "id", json_data.getString("id"));

//                if (json_data.getString("balance") != null) {
//                    Utility.addPreferences(this, "balance", json_data.getString("balance"));
//                } else {
//                    Utility.addPreferences(this, "balance", "0");
//
//                }
                Utility.addPreferences(LoginActivity.this, "login", true);


//                Singleton.firstName=Utility.getPreferences(this,"first name");
//                Singleton.lastname=Utility.getPreferences(this,"last name");
//                Singleton.email=Utility.getPreferences(this,"email");
//                Singleton.mobile=Utility.getPreferences(this,"mobile");
//                Singleton.img=Utility.getPreferences(this,"mobile");
//                Singleton.password=Utility.getPreferences(this,"password");
//                Singleton.id=Utility.getPreferences(this,"id");
//                Singleton.balance=Utility.getPreferences(this,"balance");
//


                System.out.println("lahkjfahjkf :" + Singleton.id);


            }
            startActivity(new Intent(LoginActivity.this, dashBoard.class));
            finish();

        } catch (
                JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Wrong Mobile No. or Password", Toast.LENGTH_SHORT).show();

        }


    }



}
