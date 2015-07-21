package com.nectar.timeby.gui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.nectar.timeby.R;

/**
 * Created by finalize on 7/18/15.
 */
public class UserFragment extends Fragment {
    private ImageButton userEdit = null;
    private FragmentManager fm = null;
    private FragmentTransaction ft = null;
    public UserFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_user, container, false);

       //这里设置编辑按钮的回调事件
        userEdit = (ImageButton)rootView.findViewById(R.id.imageButton_user_edit);
        userEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm = getFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.userInfo,new UserInfoEditionFragment());
                ft.commit();
            }
        });

        return rootView;
    }



}
