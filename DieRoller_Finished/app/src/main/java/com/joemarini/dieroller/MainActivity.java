package com.joemarini.dieroller;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int HIGH_ROLLER_COUNT = 25;

    private TextView tvD1, tvD2, tvD3, tvD4, tvD5, tvD6, tvDC, tvDS;
    private SeekBar sbDieSize, sbDieCount;

    private int mDieSize = 6;
    private int mDieCount = 2;
    private int[] mDieValues = new int[6];

    // TODO: Declare Analytics object variable
    private FirebaseAnalytics mFBAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up user interface
        tvD1 = (TextView)findViewById(R.id.tvDie1);
        tvD2 = (TextView)findViewById(R.id.tvDie2);
        tvD3 = (TextView)findViewById(R.id.tvDie3);
        tvD4 = (TextView)findViewById(R.id.tvDie4);
        tvD5 = (TextView)findViewById(R.id.tvDie5);
        tvD6 = (TextView)findViewById(R.id.tvDie6);
        tvDS = (TextView)findViewById(R.id.tvDS);
        tvDC = (TextView)findViewById(R.id.tvDC);
        sbDieSize = (SeekBar)findViewById(R.id.seekBar);
        sbDieCount = (SeekBar)findViewById(R.id.seekBar2);

        // TODO: Initialize the Analytics Package
        mFBAnalytics = FirebaseAnalytics.getInstance(this);

        // TODO: Change the default user session thresholds
        mFBAnalytics.setMinimumSessionDuration(5000);   // 5 sec
        mFBAnalytics.setSessionTimeoutDuration(300000); // 5 min

        sbDieSize.setProgress(mDieSize);
        sbDieCount.setProgress(mDieCount);

        tvDS.setText(String.format("%d", mDieSize));
        tvDC.setText(String.format("%d", mDieCount));

        updateDiceVisibility();

        sbDieSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mDieSize = i;
                tvDS.setText(String.format("%d", mDieSize));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sbDieCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mDieCount = i;
                tvDC.setText(String.format("%d", mDieCount));
                updateDiceVisibility();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        ((Button)findViewById(R.id.btnRoll)).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: Record predefined "Select Content" event
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, item.getTitle().toString());
        params.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(item.getItemId()));
        mFBAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);

        switch (item.getItemId()) {
            case R.id.mID1:
                setPredefinedGame(5, 6);
                return true;
            case R.id.mID2:
                setPredefinedGame(1, 6);
                return true;
            case R.id.mID3:
                setPredefinedGame(2, 6);
                return true;
            case R.id.mID4:
                setPredefinedGame(2, 10);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnRoll) {
            rollTheDice();
            updateDiceValues();
        }
    }

    private void rollTheDice() {
        for (int i=0; i < mDieCount; i++) {
            mDieValues[i] = new Random().nextInt(mDieSize) + 1;
        }
        incrementRollCount();

        // TODO: Record the custom roll event
        Bundle params = new Bundle();
        params.putInt("DieSize", mDieSize);
        String evt = String.format("Roll_%d", mDieCount);
        mFBAnalytics.logEvent(evt, params);
    }

    private void setPredefinedGame(int dieCount, int dieSize) {
        mDieCount = dieCount;
        mDieSize = dieSize;
        sbDieCount.setProgress(mDieCount, true);
        sbDieSize.setProgress(mDieSize, true);
        updateDiceVisibility();
    }

    private void updateDiceVisibility() {
        tvD2.setVisibility(mDieCount >= 2 ? View.VISIBLE : View.INVISIBLE);
        tvD3.setVisibility(mDieCount >= 3 ? View.VISIBLE : View.INVISIBLE);
        tvD4.setVisibility(mDieCount >= 4 ? View.VISIBLE : View.INVISIBLE);
        tvD5.setVisibility(mDieCount >= 5 ? View.VISIBLE : View.INVISIBLE);
        tvD6.setVisibility(mDieCount >= 6 ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateDiceValues() {
        tvD1.setText(String.format("%d", mDieValues[0]));
        if (mDieCount >= 2)
            tvD2.setText(String.format("%d", mDieValues[1]));
        if (mDieCount >= 3)
            tvD3.setText(String.format("%d", mDieValues[2]));
        if (mDieCount >= 4)
            tvD4.setText(String.format("%d", mDieValues[3]));
        if (mDieCount >= 5)
            tvD5.setText(String.format("%d", mDieValues[4]));
        if (mDieCount >= 6)
            tvD6.setText(String.format("%d", mDieValues[5]));
    }

    private void incrementRollCount() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int rollCount = prefs.getInt("RollCount", 0);
        rollCount++;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("RollCount", rollCount);

        // Record players that meet the High Roller criteria - when they roll
        // a certain number of times we flag them as a High Roller
        if (rollCount > HIGH_ROLLER_COUNT) {
            Boolean isHR = prefs.getBoolean("IS_HIGH_ROLLER", false);
            if (!isHR) {
                editor.putBoolean("IS_HIGH_ROLLER", true);

                // TODO: Create a User Property for High Roller users
                mFBAnalytics.setUserProperty("is_high_roller", "is_high_roller");
            }
        }
        editor.apply();
    }
}
