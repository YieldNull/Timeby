package com.nectar.timeby.gui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.nectar.timeby.R;
import java.util.ArrayList;
import java.util.List;

public class DrawerFragment extends Fragment {
    // 继承了回调接口的实例，也就是那个与之关联的Activity
    private OnDrawerItemSelectedListener mCallbacks;

    private ListView mDrawerListView;
    private ArrayAdapter<DrawerListItem> mArrayAdapter;

    // 记录当前选定、按下的Position
    private int mCurrentSelectedPosition = -1;

    private final String TAG = "DrawerFragment";


    /**
     * 回调接口，与这个fragment关联的Activity必须要实现这个接口<br>
     * 当抽屉中的项目被选中时,实现的接口会被触发
     */
    public interface OnDrawerItemSelectedListener {
        void onDrawerListItemSelected(int position);
    }

    /**
     * 以下是按生命周期顺序重载的回调函数
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // 关联activity，用于以后回调
        // 卧槽，真叼，实现接口就能传递数据了。
        // 要是我自己写估计就给activity定义一个函数了
        try {
            mCallbacks = (OnDrawerItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDrawerItemSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "CreateView");

        // 为fragment关联layout
        View drawerFragment = inflater.inflate(R.layout.fragment_drawer,
                container, false);
        mDrawerListView = (ListView) drawerFragment
                .findViewById(R.id.listView_drawer);
        mArrayAdapter = new DrawerListAdapter(getActivity(),
                R.layout.drawer_list_item, initListView());
        mDrawerListView.setAdapter(mArrayAdapter);
        mDrawerListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 打开app时，选中抽屉被选初始值或则最近一次选中的值
        selectItem(mCurrentSelectedPosition);
        return drawerFragment;
    }


    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mCallbacks != null)
            mCallbacks.onDrawerListItemSelected(position);
    }


    public List<DrawerListItem> initListView() {
        List<DrawerListItem> itemList = new ArrayList<DrawerListItem>();
        DrawerListItem item1 = new DrawerListItem(R.drawable.icn_drawer_user, "");
        DrawerListItem item2 = new DrawerListItem(R.drawable.icn_drawer_freinds, "");
        DrawerListItem item3 = new DrawerListItem(R.drawable.icn_drawer_report, "");
        DrawerListItem item4 = new DrawerListItem(R.drawable.icn_drawer_setting, "");
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);
        itemList.add(item4);
        return itemList;
    }

    private class DrawerListItem {
        private int imageId;
        private String text;

        public DrawerListItem(int imageId, String text) {
            this.text = text;
            this.imageId = imageId;
        }

        public int getImageId() {
            return imageId;
        }

        public String getText() {
            return text;
        }
    }

    private class DrawerListAdapter extends ArrayAdapter<DrawerListItem> {
        private int mResourceId;


        public DrawerListAdapter(Context context, int resourceId,
                                 List<DrawerListItem> objects) {
            super(context, resourceId, objects);
            mResourceId = resourceId;
        }

        @Override
        public View getView(final int postion, View convertView, ViewGroup parent) {
            DrawerListItem drawerListItem = getItem(postion);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(mResourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.listViewItemImage = (ImageView) view
                        .findViewById(R.id.drawer_list_item_image);
                viewHolder.listViewItemImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectItem(postion);
                    }
                });
                viewHolder.listViewItemText = (TextView) view
                        .findViewById(R.id.drawer_list_item_text);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.listViewItemImage.setImageResource(drawerListItem.getImageId());
            viewHolder.listViewItemText.setText(drawerListItem.getText());
            return view;
        }

        class ViewHolder {
            ImageView listViewItemImage;
            TextView listViewItemText;
        }

    }
}
