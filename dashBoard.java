package io.com.didingapp.main.view;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.com.didingapp.LoginActivity;
import io.com.didingapp.R;
import io.com.didingapp.Utility;
import io.com.didingapp.Volley.Singleton;
import io.com.didingapp.Volley.VolleyApi;
import io.com.didingapp.createbid.createBiding;
import io.com.didingapp.history.HistoryTab;
import io.com.didingapp.history.historyAdabter;
import io.com.didingapp.history.historyModel;
import io.com.didingapp.paymentActivity;
import io.com.didingapp.signupActivity;

public class dashBoard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VolleyApi.ResponseListener {
    int flag=0;
    ImageView drw, photo;
    TextView balance, name, email, mobile,no_aution;
    NavigationView navigationView;
    DrawerLayout drawer;
    ArrayList<historyModel> foodModels = new ArrayList<>();
    RecyclerView recyclerView;
    RelativeLayout logout;
    ProgressBar progressBar;
    ValueAnimator animator;
    SearchView upcoming;
    public static historyAdabter foodAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);


        fetching();
        findviewbyid();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(mLayoutManager);

    }

    public  void getData(){
        foodModels.clear();
        flag=1;
        progressBar.setVisibility(View.VISIBLE);
        VolleyApi.getInstance().UpcomingBiding(this,this);
        System.out.println("et dataag");
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.create_bid:
                startActivity(new Intent(dashBoard.this, createBiding.class));

                break;


            case R.id.history:
                startActivity(new Intent(dashBoard.this, HistoryTab.class));
                //finish();

//                Toast.makeText(this, "history ", Toast.LENGTH_LONG).show();

                break;
            case R.id.updateprf:
                startActivity(new Intent(dashBoard.this, signupActivity.class).putExtra("isEdit","2"));

//                Toast.makeText(this, "Update Profile ", Toast.LENGTH_LONG).show();

                break;


        }

//

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void OnClick(View view) {

        switch (view.getId()) {

            case R.id.balance:
                startActivity(new Intent(dashBoard.this, paymentActivity.class));
                break;
            case R.id.drw_menu:
                drawer.openDrawer(GravityCompat.START);


//                dialog cdd = new dialog(LoginActivity.this);
//                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                cdd.show();
                break;
            case R.id.logout:

                Utility.clearPreferenceData(dashBoard.this);
                startActivity(new Intent(dashBoard.this, LoginActivity.class));
                finishAffinity();
                break;
        }
    }

    private void fetching() {
        flag=0;
        System.out.println(" >>>>>>>>>>>> :: "+Utility.getPreferences(this,"mobile"));
        VolleyApi.getInstance().loginCheck(this, this, "" +Utility.getPreferences(this,"mobile"));

    }

    public void findviewbyid() {

        logout = findViewById(R.id.logout);
        no_aution=findViewById(R.id.no_auction);

        upcoming= findViewById(R.id.svmovie);

        drw = findViewById(R.id.drw_menu);
        photo = findViewById(R.id.photo);
        name = findViewById(R.id.full_name);
        mobile = findViewById(R.id.mob_num);
        email = findViewById(R.id.email);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        balance = findViewById(R.id.balance);

        progressBar = findViewById(R.id.mainprogrs);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view1);


    }

    public void valueanim(final TextView view, int text) {
        animator = new ValueAnimator();
        animator.setObjectValues(0, text);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animator.setDuration(3500); // here you set the duration of the anim
        animator.start();
    }

    @Override
    protected void onResume() {

       if(!upcoming.isIconified()) {
           upcoming.setIconified(true);

       }

        fetching();
        super.onResume();

    }

    @Override
    public void _onResponseError(Throwable e) {

    }

    @Override
    protected void onPause() {
         animator.end();

        super.onPause();
    }

    @Override
    public void _onNext(String obj) {
        if(flag ==0){
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
                    Utility.addPreferences(this, "id", json_data.getString("id"));
                    Utility.addPreferences(this, "balance", json_data.getString("balance"));

                    Utility.addPreferences(this, "login", true);


                    Singleton.firstName = Utility.getPreferences(this, "first name");
                    Singleton.lastname = Utility.getPreferences(this, "last name");
                    Singleton.email = Utility.getPreferences(this, "email");
                    Singleton.mobile = Utility.getPreferences(this, "mobile");
                    Singleton.img = Utility.getPreferences(this, "img");
                    Singleton.password = Utility.getPreferences(this, "password");
                    Singleton.id = Utility.getPreferences(this, "id");

                    Singleton.balance = Utility.getPreferences(this,"balance");




                    System.out.println("iaakflaf:"+Singleton.balance);


                     String fullname=Singleton.firstName+" "+Singleton.lastname;

                     name.setText(fullname);
                     email.setText(Singleton.email);
                     mobile.setText(Singleton.mobile);
                    Utility.setImage( photo, Singleton.img);

                    valueanim(balance, Integer.parseInt(Singleton.balance));


//                    photo.setImageBitmap(Singleton.balance);


                    System.out.println("hkjhksjfdgkl :" + Singleton.id);

                    getData();

                }

            } catch (
                    JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Wrong Mobile No. or Password", Toast.LENGTH_SHORT).show();

            }
        }else if(flag==1){

            try {
                JSONObject obj1 = new JSONObject(obj);
                JSONArray jArray = obj1.getJSONArray("data");
                String status="";
                //int len = jArray.length();
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject json_data = jArray.getJSONObject(i);

                    foodModels.add(new historyModel(
                            ""+json_data.getString("title"),
                            ""+json_data.getString("min_bids"),
                            ""+json_data.getString("start_bid_time"),
                            ""+json_data.getString("end_bid_time"),
                            ""+json_data.getString("status"),
                            ""+json_data.getString("cat_id"),
                            ""+json_data.getString("description"),
                            ""+json_data.getString("id")

                    ));




                    System.out.println("sfdlkhksdjgkajlg :"+json_data.getString("title"));


                }
                progressBar.setVisibility(View.GONE);
                no_aution.setVisibility(View.GONE);


                foodAdapter = new historyAdabter(this,foodModels,1);
                recyclerView.setAdapter(foodAdapter);

                upcoming.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        foodAdapter.getFilter().filter(newText);
                        return true;
                    }
                });


            } catch (
                    JSONException e) {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
                no_aution.setVisibility(View.VISIBLE);

            }
        }



    }


}
