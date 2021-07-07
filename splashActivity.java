package io.com.didingapp;




import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import io.com.didingapp.Volley.Singleton;
import io.com.didingapp.main.view.dashBoard;

public class splashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    boolean loginCheck = Utility.getPreferences(splashActivity.this, "login", false);
                    if (loginCheck) {
                        store();
                        startActivity(new Intent(splashActivity.this, dashBoard.class));
                    } else {
                        startActivity(new Intent(splashActivity.this, LoginActivity.class));
                    }


                    finish();
                }
            }
        };

        timerThread.start();

    }
    public void store() {

        Singleton.firstName = Utility.getPreferences(this, "first name");
        Singleton.lastname = Utility.getPreferences(this, "last name");
        Singleton.email = Utility.getPreferences(this, "email");
        Singleton.mobile = Utility.getPreferences(this, "mobile");
        Singleton.img = Utility.getPreferences(this, "mobile");
        Singleton.password = Utility.getPreferences(this, "password");
        Singleton.id = Utility.getPreferences(this, "id");
//        Singleton.balance = Utility.getPreferences(this, "balance");


    }

}

