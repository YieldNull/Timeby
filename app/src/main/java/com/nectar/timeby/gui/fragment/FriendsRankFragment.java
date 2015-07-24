package com.nectar.timeby.gui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nectar.timeby.R;

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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FriendsRank rank = getItem(position);
            int color = position % colors.length;

            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder.rank = (TextView) view.findViewById(R.id.rank);
                viewHolder.friendName = (TextView) view.findViewById(R.id.friend_name);
                viewHolder.shellNum = (TextView) view.findViewById(R.id.achievement_num);
                viewHolder.shellImage = (ImageView) view.findViewById(R.id.imageView_friends_rank_img);
                view.setTag(viewHolder);

            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.friendName.setText(rank.getFriendName());

            viewHolder.shellNum.setText(rank.getShellNum() + "");
            viewHolder.shellImage.setImageResource(rank.getImageId());
            viewHolder.rank.setText(rank.getRank() + "");
            view.setBackgroundColor(colors[color]);

            return view;

        }

        class ViewHolder {
            TextView rank;
            TextView friendName;
            TextView shellNum;
            ImageView shellImage;
        }
    }

    public class FriendsRank {
        private int rank;
        private String friend_name;
        private int shell_num;
        private int imageId;

        public FriendsRank(int rank, String friend_name, int shell_num, int imageId) {
            this.rank = rank;
            this.friend_name = friend_name;
            this.shell_num = shell_num;
            this.imageId = imageId;
        }

        public int getRank() {
            return rank;
        }

        public String getFriendName() {
            return friend_name;
        }

        public int getShellNum() {
            return shell_num;
        }

        public int getImageId() {
            return imageId;
        }

    }

}