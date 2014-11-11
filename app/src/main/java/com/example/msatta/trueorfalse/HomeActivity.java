package com.example.msatta.trueorfalse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.msatta.trueorfalse.models.Question;
import com.example.msatta.trueorfalse.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends Activity {

    public static final String EXTRA_USER = "EXTRA_USER";
    public static final String EXTRA_TOP_TEN = "EXTRA_TOP_TEN";
    public static final String EXTRA_QUESTIONS = "EXTRA_QUESTIONS";

    private Button mStartGameButton;
    private Button mRankingButton;

    private User user;
    private ArrayList<Question> mQuestions;
    private ArrayList<User> mTopTen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        user = getIntent().getParcelableExtra(EXTRA_USER);

        mStartGameButton = (Button) findViewById(R.id.buttonStartGameHome);
        mStartGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mStartGameButton.setEnabled(false);

                String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.url_question) + String.valueOf(user.getUid());
                AsyncHttpClient client = new AsyncHttpClient();
                Log.i("test", "call");
                client.get(url, null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            if (statusCode == 200) {
                                if(response.length() > 0){
                                    mQuestions = new ArrayList<Question>();
                                    for(int i = 0 ; i < response.length(); i++){
                                        JSONObject responseObject = response.getJSONObject(i);
                                        Question question = new Question();
                                        question.setText(responseObject.getString("text"));
                                        question.setAnswer(responseObject.getBoolean("answer"));
                                        mQuestions.add(question);
                                    }
                                    Intent intent = new Intent(getBaseContext(), QuestionActivity.class);
                                    intent.putExtra(EXTRA_USER, user);
                                    intent.putExtra(EXTRA_QUESTIONS, mQuestions);
                                    startActivity(intent);
                                    mStartGameButton.setEnabled(true);

                                }else{
                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                    intent.putExtra(EXTRA_USER, user);
                                    startActivity(intent);
                                    finish();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                                mStartGameButton.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                            mStartGameButton.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                        mStartGameButton.setEnabled(true);
                    }
                });
            }
        });

        mRankingButton = (Button) findViewById(R.id.buttonRankingHome);
        mRankingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mRankingButton.setEnabled(false);

                String url = getResources().getString(R.string.base_url)
                        + getResources().getString(R.string.url_score)
                        + user.getUid() + "/" + user.getBestScore();
                AsyncHttpClient client = new AsyncHttpClient();
                Log.i("test", "call");
                client.get(url, null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            if (statusCode == 200) {
                                if(response.length() > 0){
                                    mTopTen = new ArrayList<User>();
                                    for(int i = 0 ; i < response.length(); i++){
                                        JSONObject responseObject = response.getJSONObject(i);
                                        User user = new User();
                                        user.setName(responseObject.getString("name"));
                                        user.setBestScore(responseObject.getInt("bestScore"));
                                        mTopTen.add(user);
                                    }
                                    Intent intent = new Intent(getBaseContext(), RankingActivity.class);
                                    intent.putExtra(EXTRA_USER, user);
                                    intent.putExtra(EXTRA_TOP_TEN, mTopTen);
                                    startActivity(intent);
                                    mRankingButton.setEnabled(true);

                                }else{
                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                    intent.putExtra(EXTRA_USER, user);
                                    startActivity(intent);
                                    finish();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                                mRankingButton.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                            mRankingButton.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                        mStartGameButton.setEnabled(true);
                    }
                });
            }
        });
    }
}
