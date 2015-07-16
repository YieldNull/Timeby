package com.nectar.timeby.gui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.nectar.timeby.R;
import com.nectar.timeby.gui.fragment.DrawerFragment;
import com.nectar.timeby.gui.fragment.LoginFragment;
import com.nectar.timeby.service.NotifyService;
import com.nectar.timeby.gui.fragment.MainFragment;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.smssdk.SMSSDK;

/**
 * 2015.7.13 by finalize
 */
//DrawerFragment.OnDrawerItemSelectedListener,
//MainFragment.OnToggleClickListener
public class MainActivity extends AppCompatActivity
        implements MainFragment.OnToggleClickListener,
        DrawerFragment.OnDrawerItemSelectedListener {

    private static final String TAG = "MainActivity";
    private FragmentManager mFragmentManager;
    private DialogFragment mMenuDialogFragment;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //开启Service
        Intent theIntent = new Intent(this, NotifyService.class);
        theIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(theIntent);

        //初始化toolbar、menuFragment
        // initActionbar();
        //initMenuFragment();
        initDrawer();

        //初始时使用主Fragment
        // addFragment(new MainFragment(), true, R.id.main_fragment);

        SMSSDK.initSDK(this, getResources().getString(R.string.smssdk_app_key),
                getResources().getString(R.string.smssdk_app_secret));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        //设置主菜单
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //点击菜单时，显示下拉菜单
        switch (item.getItemId()) {
            case R.id.context_menu:
                mMenuDialogFragment.show(mFragmentManager, "ContextMenuDialogFragment");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onBackPressed() {
//        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
//            mMenuDialogFragment.dismiss();
//        } else {
//            finish();
//        }
//    }

    /**
     * 初始化toolbar
     */
    private void initActionbar() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#88F696A1")));
    }

    private void initDrawer() {
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


    /**
     * 初始化弹出菜单
     */
    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(true);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
    }


    /**
     * 设置弹出菜单的图片啊什么的
     *
     * @return
     */
    private List<MenuObject> getMenuObjects() {
        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);

        MenuObject send = new MenuObject("Send message");
        send.setResource(R.drawable.icn_1);

        MenuObject like = new MenuObject("Like profile");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.icn_2);
        like.setBitmap(b);

        MenuObject addFr = new MenuObject("Add to friends");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.icn_3));
        addFr.setDrawable(bd);

        MenuObject addFav = new MenuObject("Add to favorites");
        addFav.setResource(R.drawable.icn_4);

        MenuObject block = new MenuObject("Block user");
        block.setResource(R.drawable.icn_5);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        menuObjects.add(block);
        return menuObjects;
    }

    /**
     * 在主container中添加fragment
     *
     * @param fragment
     * @param addToBackStack 是否放入栈中
     * @param containerId    container
     */
    protected void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStackName = fragment.getClass().getName();
        mFragmentManager = getSupportFragmentManager();
        boolean fragmentPopped = mFragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.add(containerId, fragment, backStackName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();

        }
    }

    /**
     * 以下是实现的接口
     */

    @Override
    public void onDrawerListItemSelected(int position) {
        Log.i(TAG, "DrawerListItemSelected:" + position);

        // 替换fragment，更新main content
        // 实现的接口，与抽屉通信,抽屉中的导航条被选中时触发
        if (mDrawerLayout != null)
            mDrawerLayout.closeDrawer(Gravity.RIGHT);

        if (position == -1)
            addFragment(new MainFragment(), true, R.id.main_fragment);
        else if (position == 0)
            addFragment(new LoginFragment(), true, R.id.main_fragment);
    }

    @Override
    public void onToggleClick(Fragment fragment, boolean addToBackStack) {
        addFragment(fragment, addToBackStack, R.id.main_fragment);
    }
}
