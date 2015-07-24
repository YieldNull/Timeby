package com.nectar.timeby.gui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
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
        btnSysMsg = (Button)findViewById(R.id.btn_sys_msg);
        btnUserMsg = (Button)findViewById(R.id.btn_user_msg);
        btnSysMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSysMsg.setBackgroundResource(R.drawable.btn_sys_yellow);
                btnUserMsg.setBackgroundResource(R.drawable.btn_ivt_green);
                SysMsgFragment fragment = new SysMsgFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.msg_layout,fragment);
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
                transaction.replace(R.id.msg_layout,fragment);
                transaction.commit();
            }
        });
    }

    private void setDefaultFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        SysMsgFragment fragment = new SysMsgFragment();
        transaction.replace(R.id.msg_layout, fragment);
        transaction.commit();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
