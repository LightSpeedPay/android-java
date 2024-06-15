package com.in.lightspeedpay.pay.ui;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PaymentOperator {


    String API_KEY = "";
    String API_SECRET = "";

    String TransactionId = "";
    String Status = "";
    String PaymentScreenUrl = "";

    public void  setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }
    public void  setAPI_SECRET(String API_SECRET) {
        this.API_SECRET = API_SECRET;
    }

    public String getAPI_KEY() {
        return API_KEY;
    }
    public String getAPI_SECRET() {
        return API_SECRET;
    }

    public String getTransactionId() {
        return TransactionId;
    }
    public String getStatus() {
        return Status;
    }
    public String getPaymentScreenUrl() {
        return PaymentScreenUrl;
    }


    // Over all declaration
    OkHttpClient client = new OkHttpClient();



    // inittate payment
    public void initiatePayment(
            Context context,
            String name,
            String description,
            String billId,
            int amount
    ) throws JSONException, IOException {
        Executors.newSingleThreadExecutor().submit(() -> {


        String liveUrl = "https://api.lightspeedpay.in/api/v1/transaction/initiate-transaction";
        JSONObject postData = new JSONObject();

        try {
            postData.put("customerName", name);
            postData.put("status", "initiate");
            postData.put("method", "unknown");
            postData.put("description", description);
            postData.put("amount", amount);
            postData.put("billId", billId);
            postData.put("apiKey", this.API_KEY);
            postData.put("apiSecret", this.API_SECRET);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // send post request
        String response = null;
        try {
            response = Objects.requireNonNull(client.newCall(
                    new Request.Builder()
                            .url(liveUrl)
                            .post(RequestBody.create(postData.toString(), MediaType.parse("application/json")))
                            .build()
            ).execute().body()).string();


           JSONObject jsonObject = new JSONObject(response);

           Log.d("response", jsonObject.getString("status"));
           if (jsonObject.getString("status").equals("success")) {
               Log.d("response", jsonObject.getString("data"));
               // this.TransactionId = jsonObject.getString("data");
               this.Status = "Initiated";
               this.PaymentScreenUrl = jsonObject.getString("paymentLink");
               JSONObject data = jsonObject.getJSONObject("data");
               this.TransactionId = data.getString("_id");
               Log.d("id", this.TransactionId);
               Log.d("url", this.getPaymentScreenUrl());

               this.openProcessingScreen(context);

           }

           else if (jsonObject.getString("status").equals("fail")) {
               //Log.d("response", jsonObject.getString("data"));
               this.Status = "Failed";

           }

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

            Log.d("status", this.Status);

            // return this.Status.equals("Initiated");

        });

       // return true;

    }

    // open processing screen
    public void openProcessingScreen(Context context) {
        // open processing screen
        Log.d("url", this.getPaymentScreenUrl());

        Intent intent = new Intent(context, PaymentScreenActivity.class);
        intent.putExtra("pageUrl", this.PaymentScreenUrl);
        //intent.putExtra("context", (CharSequence) context);

        startActivity(context, intent, null);
    }



    // keep fetching status

    // get payment status
    public String getPaymentStatus() {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {

                String liveUrl = "https://api.lightspeedpay.in/api/v1/transaction/" + this.TransactionId + "?key=" + this.API_KEY + "&seecret=" + this.API_SECRET;
                JSONObject postData = new JSONObject();
                postData.put("apiKey", this.API_KEY);
                postData.put("apiSecret", this.API_SECRET);
                String response = Objects.requireNonNull(client.newCall(
                        new Request.Builder()
                                .url(liveUrl)
                                .post(RequestBody.create(postData.toString(), MediaType.parse("application/json")))
                                .build()
                ).execute().body()).string();
            }
            catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
            return this.Status;

        });


        return null;
    }

}
