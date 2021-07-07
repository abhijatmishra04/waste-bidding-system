package io.com.didingapp.main.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.com.didingapp.R;
import io.com.didingapp.Volley.Singleton;
import io.com.didingapp.Volley.VolleyApi;
import io.com.didingapp.main.Adapter.aucImgAdapter;
import io.com.didingapp.main.Adapter.bidingAdabter;
import io.com.didingapp.main.model.aucimgModel;
import io.com.didingapp.main.model.bidingModel;

public class biding_details extends AppCompatActivity implements VolleyApi.ResponseListener {

    TextView title, category, describtion, startdate, enddate, minbid, aucImgtxt, bidhistory;
    String auc_id;
    EditText useramount;
    ArrayList<aucimgModel> foodModels = new ArrayList<>();
    ArrayList<bidingModel> bidingModels = new ArrayList<>();
    LinearLayout layot;
    RecyclerView recyclerView, bidngRecycle;
    int flag, k;
    Button done;
    String history="00";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biding_details);
        getSupportActionBar().setTitle("Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);


        findviewbyid();
        System.out.println("jkhfjaf");
        setdata();

        if (getIntent().getStringExtra("history") != null) {
            history = getIntent().getStringExtra("history");

            layot.setVisibility(View.GONE);

            if (history.equalsIgnoreCase("4")) {

                done.setVisibility(View.GONE);

            } else if (history.equalsIgnoreCase("3")) {
                bidhistory.setVisibility(View.GONE);
                done.setText("Press to Publish");

            } else if (history.equalsIgnoreCase("2")) {
                done.setText("Stop");

            }

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return true;
    }

    public void findviewbyid() {

        layot = findViewById(R.id.ly7);
        title = findViewById(R.id.titledt);
        category = findViewById(R.id.categorydt);
        describtion = findViewById(R.id.discriptiondt);
        startdate = findViewById(R.id.start_date);
        enddate = findViewById(R.id.end_date);
        minbid = findViewById(R.id.amount);
        useramount = findViewById(R.id.minmum_bid);
        done = findViewById(R.id.done);
        bidhistory = findViewById(R.id.bidhistory);

        aucImgtxt = findViewById(R.id.imgtxt);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_img);
        bidngRecycle = (RecyclerView) findViewById(R.id.recycler_bing);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (history.equalsIgnoreCase("2")) {

                    updateStatus("3");

                } else if (history.equalsIgnoreCase("3")) {

                    updateStatus("2");

                } else if (history.equalsIgnoreCase("5")) {
                    finish();
                } else {

                    if (Integer.parseInt("1") > Integer.parseInt(Singleton.balance)) {
                        Toast.makeText(biding_details.this, "Not sufficient! Please add balance", Toast.LENGTH_LONG).show();

                    } else if (Integer.parseInt(useramount.getText().toString()) < Integer.parseInt(minbid.getText().toString())) {
                        Toast.makeText(biding_details.this, "Please bid a higher amount", Toast.LENGTH_LONG).show();
                    } else {
                        k = Integer.parseInt(Singleton.balance) - Integer.parseInt("1");
                        flag = 2;
                        VolleyApi.getInstance().insertBiding(biding_details.this, biding_details.this, Singleton.id, auc_id, useramount.getText().toString());
                    }


                }


            }
        });

        bidhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 0;
                bidingModels.clear();
                getBiding();
                System.out.println("ahkjfaf :" + auc_id);
            }
        });


    }

    private void getBiding() {
        VolleyApi.getInstance().getBiding(biding_details.this, this, auc_id);
    }


    public void updateStatus(String status) {
        flag = 3;
        VolleyApi.getInstance().updateStatus(biding_details.this, this, auc_id, status);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);

    }

    public void setdata() {
        int cat = 0;
        cat = Integer.parseInt(Singleton.historyModel.getCategory());
        switch (cat) {
            case 1:
                category.setText("Electronic");
                break;
            case 2:
                category.setText("Clothes");
                break;
        }


//        foodModels.get(position).getStatus();
        minbid.setText(Singleton.historyModel.getMin_bids());
        startdate.setText(Singleton.historyModel.getStart_bid_time());
        enddate.setText(Singleton.historyModel.getEnd_bid_time());
        title.setText(Singleton.historyModel.getTitle());
        describtion.setText(Singleton.historyModel.getDescription());
        auc_id = Singleton.historyModel.getAuc_id();

        System.out.println("akjflkafkla :" + auc_id);
        flag = 1;
        VolleyApi.getInstance().getAuctionImg(this, this, auc_id);


    }

    @Override
    public void _onResponseError(Throwable e) {

    }

    @Override
    public void _onNext(String obj) {
        if (flag == 1) {
            try {

                JSONObject obj1 = new JSONObject(obj);
                JSONArray jArray = obj1.getJSONArray("data");
                //int len = jArray.length();
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject json_data = jArray.getJSONObject(i);

                    foodModels.add(new aucimgModel(
                            "" + json_data.getString("auction_id"),
                            "" + json_data.getString("auction_photo")
                    ));

                    System.out.println("sfdlkhksdjgkajlg :" + json_data.getString("auction_photo"));

                }

                aucImgAdapter foodAdapter = new aucImgAdapter(this, foodModels, 1);
                recyclerView.setAdapter(foodAdapter);


            } catch (
                    JSONException e) {
                e.printStackTrace();
                aucImgtxt.setVisibility(View.GONE);

            }
        } else if (flag == 2) {
            try {

                JSONObject obj1 = new JSONObject(obj);
                JSONArray jArray = obj1.getJSONArray("msg");

                JSONObject json_data = jArray.getJSONObject(0);
                String msg = json_data.getString("msg");
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                flag = 3;
                VolleyApi.getInstance().balance(biding_details.this, this, String.valueOf(k));


            } catch (
                    JSONException e) {
                e.printStackTrace();

            }

        } else if (flag == 3) {

            try {

                JSONObject obj1 = new JSONObject(obj);
                JSONArray jArray = obj1.getJSONArray("msg");
                JSONObject obj2 = jArray.getJSONObject(0);


                if (obj2.getString("status").equalsIgnoreCase("200")) {


                    onBackPressed();

                    Toast.makeText(this, "Successesfully updated", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();

            }

        } else if (flag == 0) {

            try {

                JSONObject obj1 = new JSONObject(obj);
                JSONArray jArray = obj1.getJSONArray("data");
                bidingModels.clear();
                //int len = jArray.length();
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject json_data = jArray.getJSONObject(i);

                    bidingModels.add(new bidingModel(
                            "" + json_data.getString("auction_id"),
                            "" + json_data.getString("first_name"),
                            "" + json_data.getString("bids")
                    ));

                    bidngRecycle.setVisibility(View.VISIBLE);


                    System.out.println("fgsfgsdf :" + json_data.getString("bids"));

                }
//
                bidingAdabter foodAdapter = new bidingAdabter(this, bidingModels, 1);
                bidngRecycle.setAdapter(foodAdapter);


            } catch (
                    JSONException e) {
                e.printStackTrace();
                Toast.makeText(biding_details.this, "No previous biding found", Toast.LENGTH_LONG).show();

            }

        }

    }
}
