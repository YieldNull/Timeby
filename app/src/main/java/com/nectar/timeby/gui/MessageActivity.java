package com.nectar.timeby.gui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.nectar.timeby.R;
import com.nectar.timeby.db.ClientDao;
import com.nectar.timeby.db.Message;
import com.nectar.timeby.gui.fragment.SysMsgFragment;
import com.nectar.timeby.gui.fragment.UserMsgFragment;
import com.nectar.timeby.gui.widget.TopNotification;
import com.nectar.timeby.service.MessageReceiver;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MessageActivity extends Activity {
    private static final String TAG = "MessageActivity";
    private Button btnSysMsg;
    private Button btnUserMsg;

    private ImageButton mReturnButton;

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
                btnSysMsg.setBackgroundColor(getResources().getColor(R.color.message_bottom));
                btnSysMsg.setTextColor(getResources().getColor(R.color.msg_select));

                btnUserMsg.setBackgroundColor(getResources().getColor(R.color.message_top));
                btnUserMsg.setTextColor(Color.WHITE);

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
                btnUserMsg.setBackgroundColor(getResources().getColor(R.color.message_bottom));
                btnUserMsg.setTextColor(getResources().getColor(R.color.msg_select));

                btnSysMsg.setBackgroundColor(getResources().getColor(R.color.message_top));
                btnSysMsg.setTextColor(Color.WHITE);

                UserMsgFragment fragment = new UserMsgFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.msg_layout, fragment);
                transaction.commit();
            }
        });

        mReturnButton = (ImageButton) findViewById(R.id.imageButton_user_return);
        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
