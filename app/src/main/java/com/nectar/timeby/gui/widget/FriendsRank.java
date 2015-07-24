package com.nectar.timeby.gui.widget;

/**
 * Created by fangdongliang on 15/7/23.
 */
public class FriendsRank {
    private  int rank;
    private String friend_name;
    private int shell_num;
    private int imageId;

    public FriendsRank(int rank, String friend_name, int shell_num, int imageId)
    {
        this.rank = rank;
        this.friend_name = friend_name;
        this.shell_num = shell_num;
        this.imageId = imageId;
    }

    public int getRank()
    {
        return rank;
    }
    public String getFriendName()
    {
        return friend_name;
    }
    public int getShellNum()
    {
        return shell_num;
    }
    public int getImageId()
    {
        return imageId;
    }

}
