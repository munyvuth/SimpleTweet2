package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    EditText etNewTweet;
    Button btnCancel;
    Button btnSubmit;
    TwitterClient client;
    public static final int MAX_TWEET_LENGTH = 280;
    public static final String TAG = "ComposeActivity";
    String newTweetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etNewTweet = findViewById(R.id.etNewTweet);
        btnCancel = findViewById(R.id.btnCancel);
        btnSubmit = findViewById(R.id.btnSubmit);
        client = TwitterApplication.getRestClient(this);

        try {
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(openFileInput("draft.txt")));
            String line;
            StringBuffer buffer = new StringBuffer();
            try {
                while ((line = input.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                String text = buffer.toString();
                if(text.isEmpty() == false) {
                    etNewTweet.setText(text);
                }
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        saveDraft("");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String draft = etNewTweet.getText().toString();
                if (draft.isEmpty()) {
                    finish();
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ComposeActivity.this);
                    builder1.setMessage("You have existing text.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Save Draft",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    saveDraft(draft);
                                    dialog.dismiss();
                                    finish();
                                }
                            });

                    builder1.setNegativeButton(
                            "Delete Draft",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newTweetContent = etNewTweet.getText().toString();

                if (newTweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Compose new tweet field is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newTweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Tweet is too long!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Make API call to Twitter
                client.postNewTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess!");
                        //getting new composed tweet
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Intent i = new Intent();
                            i.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, i);
                            finish();
                        } catch (JSONException e) {
                            Log.e(TAG, "Cannot get json object", e);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure!", throwable);
                    }
                }, newTweetContent);
            }
        });

        etNewTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty() || editable.toString().length() > MAX_TWEET_LENGTH) {
                    btnSubmit.setTextColor(Color.parseColor("#D3DBE1"));
                } else {
                    btnSubmit.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }
        });
    }

    private void saveDraft(String draft) {
        try {
            FileOutputStream fos = openFileOutput("draft.txt", MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(draft);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}