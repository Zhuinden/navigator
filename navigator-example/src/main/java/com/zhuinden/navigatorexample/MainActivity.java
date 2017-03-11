package com.zhuinden.navigatorexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.zhuinden.navigator.Navigator;
import com.zhuinden.simplestack.HistoryBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @BindView(R.id.root)
    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Navigator.install(this, root, HistoryBuilder.single(new FirstKey()));
    }

    @Override
    public void onBackPressed() {
        if(!Navigator.onBackPressed(this)) {
            super.onBackPressed();
        }
    }
}

