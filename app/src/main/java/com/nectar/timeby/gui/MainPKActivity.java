package com.nectar.timeby.gui;

import android.os.Bundle;

import com.nectar.timeby.R;

public class MainPKActivity extends MainCountDownActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pk);

        super.init();
    }
}
