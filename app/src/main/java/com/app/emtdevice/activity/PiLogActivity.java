package com.app.emtdevice.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.emtdevice.Constants;
import com.app.emtdevice.R;

public class PiLogActivity extends AppCompatActivity {
    private TextView textViewPiLogs;
    private FloatingActionButton floatingActionButtonShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi_log);

        textViewPiLogs = findViewById(R.id.textViewPiLogs);
        floatingActionButtonShare = findViewById(R.id.floatingActionButtonShare);

        floatingActionButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        textViewPiLogs.getText().toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });


        textViewPiLogs.setText(Constants.piLog);
        NestedScrollView scrollViewLog = findViewById(R.id.scrollViewLog);


        scrollViewLog.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    floatingActionButtonShare.hide();
                } else {
                    floatingActionButtonShare.show();
                }
            }
        });
    }
}
