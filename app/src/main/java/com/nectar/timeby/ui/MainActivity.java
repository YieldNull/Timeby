package com.nectar.timeby.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v4.widget.ViewDragHelper;
import android.util.Log;
import android.view.Gravity;

import com.nectar.timeby.R;
import com.nectar.timeby.service.NotifyService;
import com.nectar.timeby.ui.fragment.DrawerFragment;
import com.nectar.timeby.ui.fragment.MainFragment;

import java.lang.reflect.Field;

/**
 * 2015.7.13 by finalize
 */
public class MainActivity extends Activity
        implements DrawerFragment.OnDrawerItemSelectedListener,
        MainFragment.OnToggleClickListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //开启Service
        Intent theIntent = new Intent(this, NotifyService.class);
        theIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(theIntent);

        //设置Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);


        // 主界面能滑动拉开抽屉
        try {
            Field mDragger = mDrawerLayout.getClass().getDeclaredField(
                    "mLeftDragger");// mRightDragger or mLeftDragger based on
            // Drawer Gravity
            mDragger.setAccessible(true);
            ViewDragHelper draggerObj = (ViewDragHelper) mDragger
                    .get(mDrawerLayout);

            Field mEdgeSize = draggerObj.getClass().getDeclaredField(
                    "mEdgeSize");
            mEdgeSize.setAccessible(true);
            int edge = mEdgeSize.getInt(draggerObj);

            mEdgeSize.setInt(draggerObj, edge * 4);
        } catch (Exception e) {
        }

    }


    @Override
    public void onDrawerListItemSelected(int position) {
        Log.i(TAG, "DrawerListItemSelected:" + position);

        // 替换fragment，更新main content
        // 实现的接口，与抽屉通信,抽屉中的导航条被选中时触发
        if (mDrawerLayout != null)
            mDrawerLayout.closeDrawer(Gravity.RIGHT);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment, new MainFragment())
                .commit();
    }

    @Override
    public void onToggleClick() {

    }
}
