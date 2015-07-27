package com.nectar.timeby.gui;

import android.app.Activity;
import android.app.Fragment;
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
import com.nectar.timeby.gui.fragment.MainFragment;
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
    private int mTaskType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/youyuan.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_message);

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
                setFragment(fragment);
            }
        });
        btnUserMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUserMsg.setBackgroundColor(getResources().getColor(R.color.message_bottom));
                btnUserMsg.setTextColor(getResources().getColor(R.color.msg_select));

                btnSysMsg.setBackgroundColor(getResources().getColor(R.color.message_top));
                btnSysMsg.setTextColor(Color.WHITE);

                UserMsgFragment fragment = UserMsgFragment.newInstance(mTaskType);
                setFragment(fragment);

            }
        });

        mReturnButton = (ImageButton) findViewById(R.id.imageButton_user_return);
        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTaskType = getIntent().getIntExtra(MessageReceiver.INTENT_EXTRA_TASK_TYPE, -1);
        int msgType = getIntent().getIntExtra(MessageReceiver.INTENT_EXTRA_MESG_TYPE, -1);
        choseFragment(msgType);
    }

    /**
     * 选fragment
     *
     * @param msgType
     */
    private void choseFragment(int msgType) {
        if (msgType != Message.MSG_TYPE_SYSTEM) {
            btnUserMsg.performClick();
        } else {
            btnSysMsg.performClick();
        }
    }


    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
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
