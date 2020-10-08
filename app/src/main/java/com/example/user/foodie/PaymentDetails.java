package com.example.user.foodie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetails extends AppCompatActivity {

    private TextView morder_id;
    private TextView morder_amount;
    private TextView morder_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        morder_id = findViewById(R.id.order_id);
        morder_amount = findViewById(R.id.order_amount);
        morder_status = findViewById(R.id.order_status);

        Intent intent = getIntent();
        try {
            JSONObject obj = new JSONObject(intent.getStringExtra("payment_details"));
            showDetails(obj.getJSONObject("response"), intent.getStringExtra("amount"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showDetails(JSONObject response, String amount) {
        try {

          morder_id.setText(response.getString("id"));
          morder_status.setText(response.getString("state"));
          morder_amount.setText(response.getString(String.format("$%s",amount)));
        } catch (JSONException e)
        {
            e.printStackTrace();

        }
    }
}
