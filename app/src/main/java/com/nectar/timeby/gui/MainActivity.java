package com.nectar.timeby.gui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.nectar.timeby.R;
import com.nectar.timeby.gui.fragment.DrawerFragment;
import com.nectar.timeby.gui.util.OnDrawerStatusChangedListener;
import com.nectar.timeby.gui.fragment.MainFragment;
import com.nectar.timeby.gui.util.OnDrawerToggleClickListener;
import com.nectar.timeby.gui.util.TopNotification;
import com.nectar.timeby.service.NotifyService;

import java.lang.reflect.Field;

/**
 * 2015.7.13 by finalize
 */
//DrawerFragment.OnDrawerItemSelectedListener,
//MainFragment.OnToggleClickListener
public class MainActivity extends AppCompatActivity
        implements OnDrawerToggleClickListener,
        DrawerFragment.OnDrawerItemSelectedListener {

    private static final String TAG = "MainActivity";
    private FragmentManager mFragmentManager;
    private DrawerLayout mDrawerLayout;
    private OnDrawerStatusChangedListener mDrawerStatusChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG,"onCreate");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Log.i(TAG, "Main Activity Create");
        Log.i(TAG, "trying to start notify service");

        //开启Service
        Intent theIntent = new Intent(this, NotifyService.class);
        theIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(theIntent);

        initDrawer();
        new TopNotification(this, "Hello World!", 1000 * 1).show();
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "finish");
        finish();
//        //将Fragment栈弹栈，到栈底之后结束Activity
//        int count = mFragmentManager.getBackStackEntryCount();
//
//        if (count == 1) {
//            finish();
//            return;
//        } else {
//            mFragmentManager.popBackStack();
//        }
//
//        //获取当前栈顶的Fragment
//        String fragmentTag = mFragmentManager.getBackStackEntryAt(
//                count - 2).getName();
//        try {
//            mDrawerStatusChangedListener = (OnDrawerStatusChangedListener) mFragmentManager
//                    .findFragmentByTag(fragmentTag);
//        } catch (ClassCastException e) {
//            mDrawerStatusChangedListener = null;
//        }
//
//        //返回时要显示抽屉开关
//        if (mDrawerStatusChangedListener != null)
//            mDrawerStatusChangedListener.onDrawerClosed();
    }

    /**
     * 初始化抽屉
     */
    private void initDrawer() {
        //设置Drawer
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            private boolean isClosed = true;//是否处于关闭状态

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isClosed = false;
                Log.i(TAG, "onDrawerOpened");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Log.i(TAG, "onDrawerClosed");
                isClosed = true;
                if (mDrawerStatusChangedListener != null)
                    mDrawerStatusChangedListener.onDrawerClosed();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                Log.i(TAG, "onDrawerStateChanged" + newState);
                if (isClosed & newState == DrawerLayout.STATE_SETTLING) {
                    if (mDrawerStatusChangedListener != null)
                        mDrawerStatusChangedListener.onDrawerOpening();
                }
            }
        });

        // 增大EdgeSize,使主界面能更轻松滑动拉开抽屉
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


    /**
     * 在主container中添加fragment
     *
     * @param fragment
     * @param addToBackStack 是否放入栈中
     */
    protected void addFragment(Fragment fragment, boolean addToBackStack) {
        try {
            mDrawerStatusChangedListener = (OnDrawerStatusChangedListener) fragment;
        } catch (ClassCastException e) {
            mDrawerStatusChangedListener = null;
        }

        String backStackName = fragment.getClass().getName();
        mFragmentManager = getFragmentManager();
        boolean fragmentPopped = mFragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.add(R.id.main_fragment_holder, fragment, backStackName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }


    /**
     * 当抽屉中的元素点击时触发，更换主页面的Fragment
     */
    @Override
    public void onDrawerListItemSelected(int position) {
        Log.i(TAG, "DrawerListItemSelected:" + position);

        // 替换fragment，更新main content
        // 实现的接口，与抽屉通信,抽屉中的导航条被选中时触发
        if (mDrawerLayout != null)
            mDrawerLayout.closeDrawer(Gravity.RIGHT);

        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(this, UserInfoActivity.class);
                startActivity(intent);
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            default:
                addFragment(new MainFragment(), true);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }

    /**
     * 当界面中的抽屉开关点击时触发，打开抽屉
     */

    @Override
    public void onDrawerToggleClick() {
        mDrawerLayout.openDrawer(Gravity.RIGHT);
    }
}
