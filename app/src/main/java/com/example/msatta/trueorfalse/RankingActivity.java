package com.example.msatta.trueorfalse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.msatta.trueorfalse.models.User;

import java.util.ArrayList;

public class RankingActivity extends Activity {

    private Button mTryAgainButton;
    private ListView mRankingTopTenListView;

    private User mUser;
    private int mFinalScore;

    private ArrayList<User> mTopTen;

    private ArrayAdapter<String> mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        mUser = getIntent().getParcelableExtra(NameActivity.EXTRA_USER);
        mTopTen = getIntent().getParcelableArrayListExtra(HomeActivity.EXTRA_TOP_TEN);
        Log.i("RankingActivity", ""+mTopTen.size());
        mFinalScore = (Integer) getIntent().getIntExtra(QuestionActivity.EXTRA_FINAL_SCORE,-1);

        if(mFinalScore < 0){
            //update ranking

        }

        mRankingTopTenListView = (ListView) findViewById(R.id.rankingTopTenList);

        fillListView(mTopTen);



        mTryAgainButton = (Button) findViewById(R.id.rankingTryAgainButton);
        mTryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                intent.putExtra(NameActivity.EXTRA_USER, mUser);
                intent.putParcelableArrayListExtra(HomeActivity.EXTRA_TOP_TEN, mTopTen);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fillListView(ArrayList<User> topTen) {
        ArrayList<String> topTenString = new ArrayList<String>();

        int count = 1;

        for(User user : topTen){
            topTenString.add(count + ". " + user.getName() + " - " + user.getBestScore() );
            count++;
        }

        Log.i("RankingActivity",""+topTenString.size());

        mListAdapter = new ArrayAdapter<String>(this, R.layout.simple_row, topTenString);

        mRankingTopTenListView.setAdapter(mListAdapter);
    }
}


