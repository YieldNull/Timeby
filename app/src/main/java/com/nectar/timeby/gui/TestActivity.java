package com.nectar.timeby.gui;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.nectar.timeby.R;

/**
 * Created by finalize on 7/19/15.
 */
public class TestActivity extends Activity {

    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.spinner_test);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setAdapter(new MySpinnerAdapter());

        Button button= (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSpinner.performClick();
            }
        });
    }

    private class MySpinnerAdapter extends BaseAdapter {

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
                itemView = getLayoutInflater().inflate(R.layout.spinner, parent, false);
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
