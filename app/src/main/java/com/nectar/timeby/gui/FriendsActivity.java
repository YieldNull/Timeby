package com.nectar.timeby.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.nectar.timeby.R;
import com.nectar.timeby.gui.fragment.FriendsListFragment;
import com.nectar.timeby.gui.fragment.FriendsRankFragment;
import com.nectar.timeby.gui.fragment.MainFragment;
import com.nectar.timeby.gui.interfaces.OnSubmitTaskListener;
import com.nectar.timeby.gui.widget.TopNotification;
import com.nectar.timeby.util.PrefsUtil;


public class FriendsActivity extends Activity implements FriendsListFragment.OnFinishListener {
    private Button mListButton;
    private Button mWinnerButton;
    private Button mFailureButton;
    private ImageView mReturnButton;
    private ImageView mAddButton;

    private Fragment mFragment;

    private int mTaskType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mListButton = (Button) findViewById(R.id.button_friends_list);
        mWinnerButton = (Button) findViewById(R.id.button_friends_winner);
        mFailureButton = (Button) findViewById(R.id.button_friends_failure);
        mReturnButton = (ImageView) findViewById(R.id.imageView_friends_return);
        mAddButton = (ImageView) findViewById(R.id.imageView_friends_add);

        mListButton.setOnClickListener(new ButtonClickListener());
        mWinnerButton.setOnClickListener(new ButtonClickListener());
        mFailureButton.setOnClickListener(new ButtonClickListener());

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTaskType != -1) {
                    ((OnSubmitTaskListener) mFragment).onSubmitTask();
                } else {
                    Intent intent = new Intent(FriendsActivity.this, AddFriendActivity.class);
                    FriendsActivity.this.startActivity(intent);
                }
            }
        });

        //有好友任务时，提示选择好友
        mTaskType = getIntent().getIntExtra(MainFragment.INTENT_TASK_TYPE, -1);
        if (mTaskType != -1) {
            new TopNotification(this, "请选择好友以共同完成任务", 3 * 1000).show();

            mAddButton.setImageDrawable(getResources()
                    .getDrawable(R.drawable.btn_friends_request_submit));
        }

        mListButton.performClick();
    }


    @Override
    public void onBackPressed() {

        if (mTaskType != -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
            builder.setTitle("确认离开")
                    .setMessage("离开后会取消当前任务").setPositiveButton("离开", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    MainFragment.cancelAlarm(FriendsActivity.this, mTaskType);
                    PrefsUtil.cancelTask(FriendsActivity.this);

                    Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    startActivity(intent);
                    finish();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
        } else {
            super.onBackPressed();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onFinish() {
        Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.putExtra(MainActivity.EXRAL_NOTIFY_CONTENT, "已发送申请，请等待处理结果");
        startActivity(intent);
        finish();
    }

    /**
     * 按下按钮，切换Fragment
     */
    private class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.button_friends_list:
                    replaceButtonColor(true, false, false);
                    replaceFragment(FriendsListFragment.newInstance(mTaskType));

                    break;
                case R.id.button_friends_winner:
                    if (mTaskType != -1) {
                        Toast.makeText(FriendsActivity.this,
                                "请先选择好友以共同完成任务", Toast.LENGTH_SHORT).show();
                    } else {
                        replaceButtonColor(false, true, false);
                        replaceFragment(new FriendsRankFragment());
                    }
                    break;
                case R.id.button_friends_failure:
                    if (mTaskType != -1) {
                        Toast.makeText(FriendsActivity.this,
                                "请先选择好友以共同完成任务", Toast.LENGTH_SHORT).show();
                    } else {
                        replaceButtonColor(false, false, true);
                        replaceFragment(new FriendsRankFragment());
                    }

                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 设置按钮颜色
     *
     * @param isListSelected
     * @param isWinnerSelected
     * @param isFailureSelected
     */
    private void replaceButtonColor(boolean isListSelected,
                                    boolean isWinnerSelected, boolean isFailureSelected) {
        if (isListSelected) {
            mAddButton.setEnabled(true);
            mAddButton.setVisibility(View.VISIBLE);
            mListButton.setBackgroundResource(R.drawable.btn_friends_select);
        } else {
            mListButton.setBackgroundResource(R.drawable.btn_friends_bgd);
            mAddButton.setEnabled(false);
            mAddButton.setVisibility(View.INVISIBLE);
        }

        if (isWinnerSelected) {
            mWinnerButton.setBackgroundResource(R.drawable.btn_friends_select);
        } else {
            mWinnerButton.setBackgroundResource(R.drawable.btn_friends_bgd);
        }

        if (isFailureSelected) {
            mFailureButton.setBackgroundResource(R.drawable.btn_friends_select);
        } else {
            mFailureButton.setBackgroundResource(R.drawable.btn_friends_bgd);
        }
    }

    /**
     * 替换Fragment
     *
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        mFragment = fragment;

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_friends_holder, fragment);
        transaction.commit();
    }
}
