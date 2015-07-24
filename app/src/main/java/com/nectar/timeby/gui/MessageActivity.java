package com.nectar.timeby.gui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.nectar.timeby.R;
import com.nectar.timeby.gui.fragment.SysMsgFragment;
import com.nectar.timeby.gui.fragment.UserMsgFragment;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MessageActivity extends Activity {
    private Button btnSysMsg;
    private Button btnUserMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/youyuan.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_message);

        setDefaultFragment();
        btnSysMsg = (Button) findViewById(R.id.btn_sys_msg);
        btnUserMsg = (Button) findViewById(R.id.btn_user_msg);
        btnSysMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSysMsg.setBackgroundResource(R.drawable.btn_sys_yellow);
                btnUserMsg.setBackgroundResource(R.drawable.btn_ivt_green);
                SysMsgFragment fragment = new SysMsgFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.msg_layout, fragment);
                transaction.commit();

            }
        });
        btnUserMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUserMsg.setBackgroundResource(R.drawable.btn_ivt_yellow);
                btnSysMsg.setBackgroundResource(R.drawable.btn_sys_green);
                UserMsgFragment fragment = new UserMsgFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.msg_layout, fragment);
                transaction.commit();
            }
        });
    }

    private void setDefaultFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        SysMsgFragment fragment = new SysMsgFragment();
        transaction.replace(R.id.msg_layout, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
