package com.nectar.timeby.db;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by finalize on 7/26/15.
 */
public class Message {
    //long time, String title, String content, int type, int disposed

    private int type;
    private int disposed;
    private long time;
    private String title;
    private String content;
    private String phoneNum;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Message() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDisposed() {
        return disposed;
    }

    public void setDisposed(int disposed) {
        this.disposed = disposed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeStr() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));

        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        StringBuilder str = new StringBuilder();
        str.append(getTimeStr(month));
        str.append("月");
        str.append(getTimeStr(day));
        str.append("日     ");
        str.append(getTimeStr(hour));
        str.append(":");
        str.append(getTimeStr(min));
        return str.toString();
    }

    /**
     * 将int型的格式化为字符串
     *
     * @param time
     * @return
     */
    private CharSequence getTimeStr(int time) {
        return time > 9 ? "" + time : "0" + time;
    }
}
