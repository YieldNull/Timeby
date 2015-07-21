package com.nectar.timeby.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.nectar.timeby.R;

public class UserInfoEditActivity extends Activity {

    private Spinner mSpinner;
    private ImageView mSpinnerToggle;
    private ImageButton mReturnButton;
    private ImageButton mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);
        mReturnButton = (ImageButton) findViewById(R.id.imageButton_user_edit_return);
        mSubmitButton = (ImageButton) findViewById(R.id.imageButton_user_edit_submit);

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoEditActivity.this, UserInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        mSpinner = (Spinner) findViewById(R.id.spinner_user_gender);
        mSpinner.setAdapter(new GenderSpinnerAdapter());
        mSpinnerToggle = (ImageView) findViewById(R.id.imageView_user_spinner_tolgger);
        mSpinnerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpinner.performClick();
            }
        });
    }


    private class GenderSpinnerAdapter extends BaseAdapter {

        private int[] resources = {R.drawable.icn_user_gender_male,
                R.drawable.icn_user_gender_female};

        private class ViewHolder {
            ImageView mImageView;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            ViewHolder viewHolder;

            if (convertView == null) {
                itemView = getLayoutInflater().inflate(R.layout.user_info_spinner_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mImageView = (ImageView) itemView.findViewById(R.id.imageView);
                itemView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mImageView.setImageDrawable(
                    getResources().getDrawable(resources[position]));
            return itemView;
        }

        @Override
        public int getCount() {
            return resources.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }
}
