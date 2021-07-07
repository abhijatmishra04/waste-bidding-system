package io.com.didingapp.createbid;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.com.didingapp.R;
import io.com.didingapp.Utility;
import io.com.didingapp.Volley.Singleton;
import io.com.didingapp.Volley.VolleyApi;
import io.com.didingapp.category.Selectcategory;

public class createBiding extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener, VolleyApi.ResponseListener, TimePickerDialog.OnTimeSetListener {
    TextView category, start_date, end_date;

    EditText min_bid, describtion, title;
    Button Publish, draft, stop, takeimg;


    String time, date, commondate;
     String isEdit="000";
    int flag = 0;
    int api = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_biding);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);

        getSupportActionBar().setTitle("Create bid");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        findviewbyid();

        if (getIntent().getStringExtra("isEdit") != null) {
            isEdit = getIntent().getStringExtra("isEdit");


            if (isEdit.equalsIgnoreCase("100")) {

                title.setText(Singleton.historyModel.getTitle());
                describtion.setText(Singleton.historyModel.getDescription());
                min_bid.setText(Singleton.historyModel.getMin_bids());
                start_date.setText(Singleton.historyModel.getStart_bid_time());
                end_date.setText(Singleton.historyModel.getEnd_bid_time());
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

                draft.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);



            }else if(isEdit.equalsIgnoreCase("200")){
                title.setText(Singleton.historyModel.getTitle());
                describtion.setText(Singleton.historyModel.getDescription());
                min_bid.setText(Singleton.historyModel.getMin_bids());
                start_date.setText(Singleton.historyModel.getStart_bid_time());
                end_date.setText(Singleton.historyModel.getEnd_bid_time());
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

                draft.setVisibility(View.GONE);
                Publish.setVisibility(View.VISIBLE);
                stop.setVisibility(View.VISIBLE);

            }

        }

    }


    public void findviewbyid() {
        describtion = findViewById(R.id.describtion);
        title = findViewById(R.id.title);
        Publish = findViewById(R.id.Publish);

        Publish.setOnClickListener(this);
        draft = findViewById(R.id.draft);
        draft.setOnClickListener(this);
        stop = findViewById(R.id.stop);
        stop.setOnClickListener(this);
        min_bid = findViewById(R.id.minmum_bid);

        category = findViewById(R.id.category);
        category.setOnClickListener(this);
        start_date = findViewById(R.id.start_date);
        start_date.setOnClickListener(this);
        end_date = findViewById(R.id.end_date);
        end_date.setOnClickListener(this);

//        takeimg = findViewById(R.id.takeimg);
//        takeimg.setOnClickListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return true;
    }


    private void saving(String status) {

        if (checkValidation1()) {
            Singleton.status = status;
            Singleton.title = title.getText().toString().trim();
            Singleton.description = describtion.getText().toString().trim();
            Singleton.min_bids = min_bid.getText().toString().trim();
//            int k=Integer.parseInt(Singleton.balance)-Integer.parseInt("1");
//            api=2;
//            VolleyApi.getInstance().balance(createBiding.this,this,String.valueOf(k));

            api = 0;


                if (isEdit.equalsIgnoreCase("100")|| isEdit.equalsIgnoreCase("200")) {

                    VolleyApi.getInstance().updateBiding(this, this,Singleton.historyModel.getAuc_id());

                }



            else {

                VolleyApi.getInstance().actioncreation(this, this);

            }



        }


    }

    boolean checkValidation1() {
        boolean ret = true;
        if (!Utility.hasText(title)) ret = false;
        if (!Utility.hasText(describtion)) ret = false;
        if (!Utility.hasText(min_bid)) ret = false;
//        if (Integer.parseInt(min_bid.getText().toString()) > Integer.parseInt(Singleton.balance)) {
//            ret = false;
//            Toast.makeText(this, "Not sufficient! Please add balance", Toast.LENGTH_LONG).show();
//        }
        if (!Utility.hasText_Textview(category)) ret = false;
        if (!Utility.hasText_Textview(start_date)) ret = false;
        if (!Utility.hasText_Textview(end_date)) ret = false;
        return ret;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = "0" + (view.getMonth() + 1)
                + "-" + view.getDayOfMonth()
                + "-" + view.getYear();
        Utility.timePickerDialog(this, this);
//

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        category.setText(Singleton.category);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.category:
                flag = 3;

                this.startActivityForResult(new Intent(this, Selectcategory.class), 100);

                break;
            case R.id.start_date:
                flag = 1;
                Utility.datePickerDialog(this, this);
                break;


            case R.id.end_date:
                if (Singleton.startDate != null) {
                    flag = 2;
                    Utility.datePickerChekOutDialog(this, this, Singleton.startDate);
                } else {
                    start_date.setError("please choose date");
                }


                break;
            case R.id.Publish:
                saving("2");

                break;
            case R.id.draft:
                saving("1");

                break;
            case R.id.stop:
                saving("3");
                break;
//
//            case R.id.takeimg:
//                startActivity(new Intent(createBiding.this, imgAuction.class));
//                break;

        }
    }

    @Override
    public void _onResponseError(Throwable e) {

    }

    @Override
    public void _onNext(String obj) {

        if (api == 0) {
            try {

                JSONObject obj1 = new JSONObject(obj);
                JSONArray jArray = obj1.getJSONArray("msg");
                JSONObject obj2 = jArray.getJSONObject(0);


                if (obj2.getString("status").equalsIgnoreCase("200")) {

                    api = 1;

                    VolleyApi.getInstance().gethistory(createBiding.this, createBiding.this, Singleton.id,Singleton.status);


                    Toast.makeText(this, "Successesfully insert", Toast.LENGTH_SHORT).show();


//                    startActivity(new Intent(createBiding.this, imgAuction.class));
                }


//            Toast.makeText(this, obj2.getString("status"), Toast.LENGTH_SHORT).show();


            } catch (JSONException e) {
                e.printStackTrace();

            }
        } else if (api == 1) {
            try {

                JSONObject obj1 = new JSONObject(obj);
                JSONArray jArray = obj1.getJSONArray("data");
                //int len = jArray.length();
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject json_data = jArray.getJSONObject(i);


                    Singleton.auc_id = json_data.getString("id");


//                        Utility.addPreferences(this, "email", json_data.getString("email"));
//                        Utility.addPreferences(this, "mobile", json_data.getString("mobile"));
//                        Utility.addPreferences(this, "first name", json_data.getString("first_name"));
//                        Utility.addPreferences(this, "last name", json_data.getString("last_name"));
//                        Utility.addPreferences(this, "img", json_data.getString("photo"));
//                        Utility.addPreferences(this, "password", json_data.getString("password"));
//                        Utility.addPreferences(this, "id", json_data.getString("id"));
//


                    System.out.println("lahkjfahjkf :" + Singleton.auc_id);


                }

                    if(isEdit.equalsIgnoreCase("200")) {
                        finish();
                    }
                else {
                    startActivity(new Intent(createBiding.this, imgAuction.class));
                    finish();
                }
            } catch (
                    JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();

            }
        }
//        else if (api==2) {
//            try {
//
//                JSONObject obj1 = new JSONObject(obj);
//                JSONArray jArray = obj1.getJSONArray("msg");
//                JSONObject obj2 = jArray.getJSONObject(0);
//
//
//                if (obj2.getString("status").equalsIgnoreCase("200")) {
//
////                    Utility.addPreferences(this, "balance", Singleton.balance);
//                    api = 0;
//                    VolleyApi.getInstance().actioncreation(this, this);
//
////                    Toast.makeText(this, "Successesfully updated", Toast.LENGTH_SHORT).show();
//                }
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//
//            }
//        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

        time = selectedHour + ":" + selectedMinute;

        switch (flag) {
            case 1:
                commondate = Utility.formatDateForDisplay(date, "MM-dd-yyyy", "MMM dd, yyyy");

//                start_date.setText(Utility.formatDateForDisplay(date, "MM-dd-yyyy", "MMM dd, yyyy"));
                Singleton.startDate = Utility.formatDateForDisplay(date, "MM-dd-yyyy", "YYYY-MM-dd") + " " + time + ":00";

                start_date.setText("Date :" + commondate + " Time :" + time);

                break;

            case 2:
                commondate = Utility.formatDateForDisplay(date, "MM-dd-yyyy", "MMM dd, yyyy");
                Singleton.endDate = Utility.formatDateForDisplay(date, "MM-dd-yyyy", "YYYY-MM-dd") + " " + time + ":00";
//                end_date.setText(Utility.formatDateForDisplay(date, "MM-dd-yyyy", "MMM dd, yyyy"));

                end_date.setText("Date :" + commondate + " Time :" + time);

                break;
        }

        System.out.println("time    :" + time + "sngloton :" + Singleton.startDate + "hajkkfha :" + commondate);
    }

}
