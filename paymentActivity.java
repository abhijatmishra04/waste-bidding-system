package io.com.didingapp;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.com.didingapp.Volley.Singleton;
import io.com.didingapp.Volley.VolleyApi;

public class paymentActivity extends AppCompatActivity  implements VolleyApi.ResponseListener{

    Button buy;
    AlertDialog.Builder alertBuilder;
    CardView fifty,hun,two,thre;
    ImageView rt1,rt2,rt3,rt4;
    EditText balance,cardNumber,cvv,pin,exp_date;
    int flag=0;
    int i,j,k;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        getSupportActionBar().setTitle("Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);


        findviewbyid();


    }

    private void updatebalance() {
        flag=2;
        VolleyApi.getInstance().balance(paymentActivity.this,this,String.valueOf(k));
    }


    private void fetching() {
        flag=1;

        VolleyApi.getInstance().getcard(paymentActivity.this,this,cardNumber.getText().toString().replace(" ",""),cvv.getText().toString(),pin.getText().toString(),exp_date.getText().toString());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        return true;
    }

    @Override
    public void _onResponseError(Throwable e) {

    }

    @Override
    public void _onNext(String obj) {

        if (flag==2){
            try {

                JSONObject obj1 = new JSONObject(obj);
                JSONArray jArray = obj1.getJSONArray("msg");
                JSONObject obj2 =jArray.getJSONObject(0);


                if(obj2.getString("status").equalsIgnoreCase("200")){

                    Utility.addPreferences(this,"balance",Singleton.balance);

                    Toast.makeText(this, "Successesfully updated", Toast.LENGTH_SHORT).show();
                    finish();
                }




            } catch (JSONException e) {
                e.printStackTrace();

            }
        }else if (flag==1){
            try {

                JSONObject obj1 = new JSONObject(obj);
                JSONArray jArray = obj1.getJSONArray("data");
                JSONObject obj2 =jArray.getJSONObject(0);

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    json_data.getString("number");


                }

                updatebalance();




            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this,"Please insert corect details",Toast.LENGTH_LONG).show();

            }
        }

    }

    public void onClick(View view) {

        switch (view.getId()){

            case R.id.fifty:
                balance.setText("0");
                rt1.setImageResource(R.drawable.radio_chek);
                rt2.setImageResource(R.drawable.radio_unchek);
                rt3.setImageResource(R.drawable.radio_unchek);
                rt4.setImageResource(R.drawable.radio_unchek);

                balance.setText("50");

                break;
            case R.id.hun:
                balance.setText("0");
                rt1.setImageResource(R.drawable.radio_unchek);
                rt2.setImageResource(R.drawable.radio_chek);
                rt3.setImageResource(R.drawable.radio_unchek);
                rt4.setImageResource(R.drawable.radio_unchek);
                balance.setText("100");
                break;
            case R.id.twohun:

                rt1.setImageResource(R.drawable.radio_unchek);
                rt2.setImageResource(R.drawable.radio_unchek);
                rt3.setImageResource(R.drawable.radio_chek);
                rt4.setImageResource(R.drawable.radio_unchek);
                balance.setText("200");
                break;
            case R.id.threehun:
                rt1.setImageResource(R.drawable.radio_unchek);
                rt2.setImageResource(R.drawable.radio_unchek);
                rt3.setImageResource(R.drawable.radio_unchek);
                rt4.setImageResource(R.drawable.radio_chek);
                balance.setText("300");
                break;
            case R.id.btnBuy:


                if(checkValidation1()){
                    i=Integer.parseInt(Singleton.balance);
                    System.out.println("ajfa  :"+Singleton.balance);

                    j=Integer.parseInt(balance.getText().toString());
                    System.out.println("ajfa  :"+j);

                    k=i+j;
                    System.out.println("ajfa   k  :"+k);

                    fetching();
                }else {
                    Toast.makeText(this,"please add money",Toast.LENGTH_LONG).show();
                }



                break;

        }
    }

    boolean checkValidation1() {
        boolean ret = true;
        if (!Utility.hasText(balance)) ret = false;
        if (!Utility.hasText(cardNumber)) ret = false;
        if (!Utility.hasText(cvv)) ret = false;
        if (!Utility.hasText(pin)) ret = false;
        if (!Utility.hasText(exp_date)) ret = false;

        return ret;
    }

    public void findviewbyid(){
        buy = findViewById(R.id.btnBuy);

        fifty = findViewById(R.id.fifty);
        hun = findViewById(R.id.hun);
        two = findViewById(R.id.twohun);
        thre = findViewById(R.id.threehun);

        rt1 = findViewById(R.id.rt1);
        rt2 = findViewById(R.id.rt2);
        rt3 = findViewById(R.id.rt3);
        rt4 = findViewById(R.id.rt4);

        balance = findViewById(R.id.balanceed);


        cardNumber = findViewById(R.id.card_number);
        cvv = findViewById(R.id.cvv);
        exp_date = findViewById(R.id.exp_date);
        pin = findViewById(R.id.pin);



        cardNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                int len=s.toString().length();

                if (before == 0 && (len == 4 || len == 9 || len == 14 ))
                    cardNumber.append(" ");

                System.out.println("jkahfkjjakf :"+cardNumber.getText().toString());
            }

        });
        exp_date.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                int len=s.toString().length();

                if (before == 0 && (len == 2))
                    exp_date.append("-");

                System.out.println("jkahfkjjakf :"+cardNumber.getText().toString());

            }

        });

        balance.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){

                    System.out.println("jshafhhajkhf" + Singleton.balance);


                    System.out.println("jajkfklal" + Singleton.balance);


                }
            }
        });


    }

}