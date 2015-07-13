package com.nectar.timeby.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nectar.timeby.R;
import com.nectar.timeby.service.NotifyService;
import com.nectar.timeby.service.ScreenOnReceiver;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent theIntent = new Intent(this, NotifyService.class);
        theIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(theIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i(TAG,"activity is invisible, tell ScreenOnReceiver to work");

        //当APP不可见时，发送广播，使ScreenReceiver生效
        Intent intent = new Intent();
        intent.setAction(ScreenOnReceiver.ENABLE_ACTION);
        sendBroadcast(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
