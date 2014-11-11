package com.example.msatta.trueorfalse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class QuestionActivity extends Activity {

    private static final String TAG = "QuestionActivity";

    public static final String EXTRA_FINAL_SCORE = "EXTRA_FINAL_SCORE";

    private int mCount;
    private boolean mCurrentAnswer;

    private ArrayList<Question> mQuestions;
    private ArrayList<User> mTopTen;
    private User mUser;

    private TextView mCountDownTextView;
    private TextView mQuestionText;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mForwardButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        mQuestions = getIntent().getParcelableArrayListExtra(HomeActivity.EXTRA_QUESTIONS);
        mUser = getIntent().getParcelableExtra(HomeActivity.EXTRA_USER);

        mCountDownTextView = (TextView) findViewById(R.id.textCountDownTimer);
        mCountDownTimer.start();

        mQuestionText = (TextView) findViewById(R.id.textQuestion);

        mTrueButton = (Button) findViewById(R.id.trueButton);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "true pressed");
                if(mCurrentAnswer){
                    mCount++;
                    updateQuestion();
                }
                else{
                    gameOver();
                }
            }
        });

        mFalseButton = (Button) findViewById(R.id.falseButton);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "false pressed");
                if(mCurrentAnswer){
                    gameOver();
                }else{
                    mCount++;
                    updateQuestion();
                }

            }
        });

        mForwardButton = (Button) findViewById(R.id.forwardButton);
        mForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mForwardButton.setEnabled(false);

                String url = getResources().getString(R.string.base_url)
                        + getResources().getString(R.string.url_score)
                        + mUser.getUid() + "/" + mCount;
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
                                    intent.putExtra(HomeActivity.EXTRA_USER, mUser);
                                    intent.putExtra(HomeActivity.EXTRA_TOP_TEN, mTopTen);
                                    startActivity(intent);

                                }else{
                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                    intent.putExtra(HomeActivity.EXTRA_USER, mUser);
                                    startActivity(intent);
                                    finish();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                                mForwardButton.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                            mForwardButton.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
                        mForwardButton.setEnabled(true);
                    }
                });

            }
        });

        mCount = 0;
        updateQuestion();
    }

    private CountDownTimer mCountDownTimer = new CountDownTimer(10000, 1000) {

        public void onTick(long millisUntilFinished) {
            mCountDownTextView.setText(String.valueOf(millisUntilFinished / 1000));
        }

        public void onFinish() {
           gameOver();
        }
    };

    private void updateQuestion(){
        if(mCount < mQuestions.size()) {
            Question question = mQuestions.get(mCount);
            String questionText = String.valueOf(mCount + 1) + ". " + question.getText();
            mQuestionText.setText(questionText);
            mCurrentAnswer = question.isAnswer();
            mCountDownTimer.start();
        }
        else{
            wellDone();
        }
    }

    private void gameOver(){
        mCountDownTimer.cancel();
        mCountDownTextView.setText(R.string.text_game_over);
        mCountDownTextView.setTextSize(80);

        mQuestionText.setText("score: " + mCount);

        mTrueButton.setVisibility(View.INVISIBLE);
        mFalseButton.setVisibility(View.INVISIBLE);
        mForwardButton.setVisibility(View.VISIBLE);
    }

    private void wellDone() {
        mCountDownTimer.cancel();
        mCountDownTextView.setText(R.string.text_well_done);
        mCountDownTextView.setTextSize(80);

        mQuestionText.setText(R.string.text_well_done_sub);

        mTrueButton.setVisibility(View.INVISIBLE);
        mFalseButton.setVisibility(View.INVISIBLE);
        mForwardButton.setVisibility(View.VISIBLE);
    }

}
