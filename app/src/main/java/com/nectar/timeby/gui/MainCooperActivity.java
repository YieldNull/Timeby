package com.nectar.timeby.gui;


import android.os.Bundle;
import android.widget.ListView;

import com.nectar.timeby.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MainCooperActivity extends MainCountDownActivity {
    ListView mResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cooper);

        super.init();

        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> temp = new HashMap<String, Object>();
        temp.put("FIRST_COLUMN", "萌萌哒贝壳");
        temp.put("SECOND_COLUMN", "100");
        temp.put("THIRD_COLUMN", R.drawable.icn_user_shell);

        list.add(temp);

        HashMap<String, Object> temp1 = new HashMap<String, Object>();
        temp1.put("FIRST_COLUMN", "Diaries");
        temp1.put("SECOND_COLUMN", "200");
        temp1.put("THIRD_COLUMN", R.drawable.icn_user_shell);

        list.add(temp1);
        mResultList = (ListView) findViewById(R.id.listView_main_cooper);
        mResultList.setAdapter(new CountdownListViewAdapter(this, list));
    }
}
