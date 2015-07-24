package com.nectar.timeby.gui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.nectar.timeby.R;
import com.nectar.timeby.gui.fragment.FriendsListFragment;
import com.nectar.timeby.gui.fragment.FriendsRankFragment;


public class FriendsActivity extends Activity {
    private Button mListButton;
    private Button mWinnerButton;
    private Button mFailureButton;
    private ImageView mReturnButton;
    private ImageView mAddButton;

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
                Intent intent = new Intent(FriendsActivity.this, AddFriendActivity.class);
                FriendsActivity.this.startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        startActivity(intent);
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
                    replaceFragment(new FriendsListFragment());

                    break;
                case R.id.button_friends_winner:
                    replaceButtonColor(false, true, false);
                    replaceFragment(new FriendsRankFragment());

                    break;
                case R.id.button_friends_failure:
                    replaceButtonColor(false, false, true);
                    replaceFragment(new FriendsRankFragment());

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
            mListButton.setBackgroundColor(getResources().getColor(R.color.friends_select));
        } else {
            mListButton.setBackgroundColor(getResources().getColor(R.color.friends_bgd));
            mAddButton.setEnabled(false);
            mAddButton.setVisibility(View.INVISIBLE);
        }

        if (isWinnerSelected) {
            mWinnerButton.setBackgroundColor(getResources().getColor(R.color.friends_select));
        } else {
            mWinnerButton.setBackgroundColor(getResources().getColor(R.color.friends_bgd));
        }

        if (isFailureSelected) {
            mFailureButton.setBackgroundColor(getResources().getColor(R.color.friends_select));
        } else {
            mFailureButton.setBackgroundColor(getResources().getColor(R.color.friends_bgd));
        }
    }

    /**
     * 替换Fragment
     *
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_friends_holder, fragment);
        transaction.commit();
    }
}
