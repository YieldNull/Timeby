package com.nectar.timeby.gui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import com.nectar.timeby.R;
import com.nectar.timeby.gui.widget.FriendsRank;

import java.util.List;

/**
 * Created by fangdongliang on 15/7/22.
 */
public class FriendsRankFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_rank, container, false);
        return view;
    }


    public class RankListAdapter extends ArrayAdapter<FriendsRank> {
        private int[] colors = new int[]{0xFFFFFFE3, 0xFFECEDC4}; //记录listView的背景颜色
        private int resourceId;

        public RankListAdapter(Context context, int resource, List<FriendsRank> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            F_FailedTaskRank rank = getItem(position);
//            int color = position % colors.length;
//
//            View view;
//            ViewHolder viewHolder;
//            if (convertView == null) {
//                viewHolder = new ViewHolder();
//                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
//                viewHolder.failRank = (TextView) view.findViewById(R.id.failRank);
//                viewHolder.friendName = (TextView) view.findViewById(R.id.friendName);
//                viewHolder.failCount = (TextView) view.findViewById(R.id.failCount);
//                viewHolder.hammerImage = (ImageView) view.findViewById(R.id.hammerImage);
//                view.setTag(viewHolder);
//
//            } else {
//                view = convertView;
//                viewHolder = (ViewHolder) view.getTag();
//            }
//
//            viewHolder.friendName.setText(rank.getFriendName());
//
//            viewHolder.failRank.setText(rank.getRank() + "");
//            viewHolder.hammerImage.setImageResource(rank.getImageId());
//            viewHolder.failCount.setText(rank.getHammerNum() + "");
//            view.setBackgroundColor(colors[color]);
//
//            return view;
//
//        }
//
//        class ViewHolder {
//            TextView failRank;
//            TextView friendName;
//            TextView failCount;
//            ImageView hammerImage;
//        }
    }
}