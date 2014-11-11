package com.example.msatta.trueorfalse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msatta.trueorfalse.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class NameActivity extends Activity implements TextWatcher{

    public static final String EXTRA_USER = "EXTRA_USER";

    private static final Pattern sPattern
            = Pattern.compile("[0-9a-zA-Z]+");

    private EditText mEditText;
    private Button mOkButton;

    private User user;
    private String mText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        user = (User) getIntent().getParcelableExtra(EXTRA_USER);

        mEditText = (EditText) findViewById(R.id.nameEditText);
        mEditText.addTextChangedListener(this);

        mOkButton = (Button) findViewById(R.id.nameButton);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOkButton.setEnabled(false);

                String currentName = mEditText.getText().toString();
                Log.i("test","click");
                if (currentName.equals("null")) {
                    Toast.makeText(getApplicationContext(), R.string.name_not_available, Toast.LENGTH_LONG).show();
                } else {
                    String url = getResources().getString(R.string.base_url)
                            + getResources().getString(R.string.url_user_registration)
                            + user.getUid() + "/" + currentName;
                    AsyncHttpClient client = new AsyncHttpClient();
                    Log.i("test","call");
                    client.get(url, null, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            Log.i("test",response.toString());
                            try {
                                if (statusCode == 200) {
                                    Log.i("test", ""+statusCode);
                                    User user = new User();
                                    long uid = response.getLong("uid");
                                    user.setUid(uid);
                                    String name = response.getString("name");
                                    Log.i("test",name);

                                    if (!name.equals("null")) {
                                        int bestScore = 0;

                                        user.setName(name);
                                        user.setBestScore(bestScore);

                                        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                                        intent.putExtra(NameActivity.EXTRA_USER, user);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(getApplicationContext(), R.string.name_not_available, Toast.LENGTH_LONG).show();
                                        mOkButton.setEnabled(true);
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                                    mOkButton.setEnabled(true);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                                mOkButton.setEnabled(true);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                            mOkButton.setEnabled(true);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!isValid(s)){
            mEditText.setText(mText);
        }else{
            mText = s.toString();
        }
    }

    private boolean isValid(CharSequence s) {
        return sPattern.matcher(s).matches();
    }
}