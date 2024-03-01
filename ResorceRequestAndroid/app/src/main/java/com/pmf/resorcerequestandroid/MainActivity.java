/**
* Copyright 2016 IBM Corp.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.pmf.resorcerequestandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import com.worklight.wlclient.api.*;

public class MainActivity extends AppCompatActivity {
    private TextView first_name = null;
    private TextView middle_name = null;
    private TextView last_name = null;
    private TextView age = null;
    private TextView height = null;
    private TextView birthdate = null;
    private TextView summary = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        first_name = (TextView) findViewById(R.id.first_name);
        middle_name = (TextView) findViewById(R.id.middle_name);
        last_name = (TextView) findViewById(R.id.last_name);
        age = (TextView) findViewById(R.id.age);
        height = (TextView) findViewById(R.id.height);
        birthdate = (TextView) findViewById(R.id.birthdate);
        summary = (TextView) findViewById(R.id.summary);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        Button sendButton = (Button) findViewById(R.id.button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Path Parameters (First Name, Middle Name and Last Name)
                    URI adapterPath = new URI("/adapters/JavaAdapter/users/"
                            + first_name.getText().toString() + "/"
                            + middle_name.getText().toString() + "/"
                            + last_name.getText().toString()
                    );

                    WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.POST);
                    // Query Parameters
                    request.setQueryParameter("age", age.getText().toString());

                    // Header Parameters
                    request.addHeader("birthdate", birthdate.getText().toString());

                    // Form Parameters
                    HashMap<String,String> formParams = new HashMap<>();
                    formParams.put("height", height.getText().toString());

                    // Send
                    request.send(formParams, new WLResponseListener() {
                        public void onSuccess(WLResponse response) {
                            String responseText = response.getResponseText();
                            String resultText = "";

                            try {
                                resultText += "Name = " + response.getResponseJSON().getString("first")
                                        + " " + response.getResponseJSON().getString("middle")
                                        + " " + response.getResponseJSON().getString("last") + "\n";
                                resultText += "Age = " + response.getResponseJSON().getInt("age") + "\n";
                                resultText += "Height = " + response.getResponseJSON().getString("height") + "\n";
                                resultText += "Birthdate = " + response.getResponseJSON().getString("birthdate");
                            } catch (org.json.JSONException e) {
                                updateTextView(e.getMessage());
                            }

                            Log.d("InvokeSuccess", responseText);
                            updateTextView(resultText);
                        }

                        public void onFailure(WLFailResponse response) {
                            //String responseText = response.getResponseText();
                            String errorMsg = response.getErrorMsg();
                            Log.d("InvokeFail", errorMsg);
                            updateTextView("Failed to Invoke Adapter Procedure\n" + errorMsg);
                        }
                    });

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateTextView(final String str){
        Runnable run = new Runnable() {
            public void run() {
                summary.setText(str);
            }
        };
        this.runOnUiThread(run);
    }
}
