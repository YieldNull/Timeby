package com.nectar.timeby.gui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.nectar.timeby.R;
import com.nectar.timeby.gui.fragment.DrawerFragment;
import com.nectar.timeby.gui.fragment.MainFragment;
import com.nectar.timeby.gui.interfaces.OnDrawerStatusChangedListener;
import com.nectar.timeby.gui.interfaces.OnDrawerToggleClickListener;
import com.nectar.timeby.gui.widget.TopNotification;
import com.nectar.timeby.service.MessageReceiver;
import com.nectar.timeby.service.wakeful.AlarmListener;
import com.nectar.timeby.service.countdown.ScreenService;
import com.nectar.timeby.util.PrefsUtil;

import java.lang.reflect.Field;

/**
 * 2015.7.13 by finalize
 */
public class MainActivity extends AppCompatActivity
        implements OnDrawerToggleClickListener,
        DrawerFragment.OnDrawerItemSelectedListener {

    public static final String EXRAL_NOTIFY_CONTENT = "notify_content";
    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private FragmentManager mFragmentManager;
    private OnDrawerStatusChangedListener mDrawerStatusChangedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //各种判断
        if (!PrefsUtil.isLogin(this)) {
            Log.i(TAG, "Not login, entering login activity");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (PrefsUtil.isOnTask(this)) {
            Log.i(TAG, "On task, entering countdown page");
            startActivity(new Intent(this, CountDownActivity.class));
            finish();
            return;
        }

        if (PrefsUtil.isFirstUse(this)) {
            PrefsUtil.setFirstUse(this, false);

            Log.i(TAG, "App first use, starting polling service");
            WakefulIntentService.scheduleAlarms(new AlarmListener(), this, false);


            Log.i(TAG, "Starting ScreenService");
            startService(new Intent(this, ScreenService.class));
        }

        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        initDrawer();

        //显示顶部提示
        String content = getIntent().getStringExtra(EXRAL_NOTIFY_CONTENT);
        if (content != null) {
            new TopNotification(this, content, 3000).show();
        }
    }


    //在主界面时使用优先级较高的Receiver截断广播，不使用Notification，直接在主界面提示
    private BroadcastReceiver onNotify = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String content = intent.getStringExtra(MessageReceiver.INTENT_EXTRA_CONTENT);
            int flag = intent.getIntExtra(MessageReceiver.INTENT_FLAG, -1);

            new TopNotification(MainActivity.this, content, 3 * 1000).show();

            abortBroadcast();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");


        IntentFilter filter = new IntentFilter(MessageReceiver.INTENT_ACTION);
        filter.setPriority(2);
        registerReceiver(onNotify, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(onNotify);
    }

    @Override
    public void onBackPressed() {
        //当MainActivity使用不同的fragment进行替换时使用
        //然而微姐把DrawerLayout当菜单用了。。。。
        //整个就特么只有MainFragment一个，那还换个毛啊
        //将Fragment栈弹栈，到栈底之后结束Activity
        int count = mFragmentManager.getBackStackEntryCount();

        if (count == 1) {
            finish();
            return;
        } else {
            mFragmentManager.popBackStack();
        }

        //获取当前栈顶的Fragment
        String fragmentTag = mFragmentManager.getBackStackEntryAt(
                count - 2).getName();
        try {
            mDrawerStatusChangedListener = (OnDrawerStatusChangedListener) mFragmentManager
                    .findFragmentByTag(fragmentTag);
        } catch (ClassCastException e) {
            mDrawerStatusChangedListener = null;
        }

        //返回时要显示抽屉开关
        if (mDrawerStatusChangedListener != null)
            mDrawerStatusChangedListener.onDrawerClosed();
    }


    /**
     * 当界面中的抽屉开关点击时触发，打开抽屉
     */

    @Override
    public void onDrawerToggleClick() {
        mDrawerLayout.openDrawer(Gravity.RIGHT);
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
                intent = new Intent(this, FriendsActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(this, MessageActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(this, ReportActivity.class);
                startActivity(intent);
                break;
            case 4:
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
        Log.i(TAG, "onDestroy");
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
//                Log.i(TAG, "onDrawerStateChanged" + newState);
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
}
