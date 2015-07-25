package com.nectar.timeby.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by wsw on 2015/7/20.
 */
public class ClientDao {
    private ClientSQLiteOpenHelper helper;
    private static final String TAG = "ClientDao";

    //构造初始化helper
    public ClientDao(Context context) {
        helper = new ClientSQLiteOpenHelper(context);
    }

    /*
     *下面就是我根据应用情景设想到的数据库操作了。
     * 注意用户信息表只需要一条记录来记录用户自身的信息
     * 首先是用户注册时在本地添加一条记录，填入用户名，电话号码，密码
     */
    public long addRegisterInfo(String username, String phoneNumber, String password) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("phoneNumber", phoneNumber);
        values.put("password", password);
        long result = db.insert("User", null, values);
        db.close();
        return result;
    }

    /*
     *然后是完善用户信息时，对用户的那条数据进行更新
     * 参数：属性名，属性值
     */
    public int updateUserInfo(String attribute, String value) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (attribute.equals("age")) { //只有年龄是int类型的，所以需要单独判断一下
            values.put(attribute, Integer.parseInt(value));
        } else {
            values.put(attribute, value);
        }
        int result = db.update("User", values, null, null);
        db.close();
        return result;
    }

    /*
     *对贝壳数的修改，参数是增加的贝壳数
     */
    public void addNumberOfShell(int number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("UPDATE User SET numberOfShell = numberOfShell+" + number);
        db.close();
    }

    /*
     *对锤子数的修改，参数是增加的锤子数
     */
    public void addNumberOfHammer(int number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("UPDATE User SET numberOfHammer = numberOfHammer+" + number);
        db.close();
    }

    /*
     *然后是从数据库取用户数据
     * 参数是属性名
     */
    public String findUserInfo(String attribute) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String result = null;
        Cursor cursor = db.query("User", new String[]{attribute}, null, null, null, null, null);
        if (attribute.equals("age")) {
            if (cursor.moveToNext()) {
                result = cursor.getInt(cursor.getColumnIndex(attribute)) + "";
            } else {
                Log.i(TAG, "没有找到该属性内容");
            }
        } else if (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex(attribute));
        } else {
            Log.i(TAG, "没有找到该属性内容");
        }
        db.close();
        return result;
    }

    /*
     *接下来是对好友表的操作
     * 首先是添加好友记录
     * 参数：本机用户注册号码，好友号码，备注（默认是好友昵称）
     */
    public long addFriend(String phoneNumberA, String phoneNumberB, String remark) {//remark指的是用户名，默认是昵称
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phoneNumberA", phoneNumberA); //这个属性是本用户的电话号码
        values.put("phoneNumberB", phoneNumberB); //好友的电话号码
        values.put("remark", remark);
        long result = db.insert("Friendship", null, values);
        db.close();
        return result;
    }

    /*
     *然后是删除好友
     */
    public int deleteFriend(String phoneNumber) { //返回值是int类型的，具体含义，你懂得
        SQLiteDatabase db = helper.getWritableDatabase();
        int number = db.delete("Friendship", "phoneNumberB=?", new String[]{phoneNumber});
        db.close();
        return number;
    }

    /*
     *修改好友备注
     *参数：好友号码，新备注
     */
    public int updateFriendRemark(String phoneNumber, String remark) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("remark", remark);
        int result = db.update("Friendship", values, "phoneNumberB=?", new String[]{phoneNumber});
        db.close();
        return result;
    }

    /*
     *查询好友信息
     * 参数：用户号码
     */
    public ArrayList<FriendShip> findFriendInfo(String phoneNumberA) {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<FriendShip> friendShips = new ArrayList<FriendShip>();
        //好吧，这里好像还有点问题
        Cursor cursor = db.query("Friendship", new String[]{"phoneNumberB", "phoneNumberA", "remark"}, "phoneNumberA=?", new String[]{phoneNumberA}, null, null, null);
        while (cursor.moveToNext()) {
            FriendShip friendShip = new FriendShip();
            friendShip.setPhoneNumberB(cursor.getString(cursor.getColumnIndex("phoneNumberB")));
            friendShip.setPhoneNumberA(cursor.getString(cursor.getColumnIndex("phoneNumberA")));
            friendShip.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
            friendShips.add(friendShip);
        }
        db.close();
        return friendShips;
    }


    /**
     * 任务表操作：
     *
     */

     // 鉴于失败任务不需要填写相关的信息，故新建任务时只需要起止时间及用户信息
     public long addTask(String startTime,String endTime, String phoneNumberA){
         SQLiteDatabase db = helper.getWritableDatabase();
         ContentValues content = new ContentValues();
         content.put("phoneNumberA",phoneNumberA);
         content.put("startTime",startTime);
         content.put("endTime",endTime);
         long result = db.insert("Task", null, content);
         db.close();
         return result;
     }

    /*然后是完善信息部分，就是填写成功与否，还有专注度及效率，，，这里成功与否用Integer值 1和0进行区分
     *鉴于你妹的，需要什么多用户，所以完善信息时需要传主键，也就是用户号码及任务开始时间
     */
    public void updateTask(String phoneNumberA,int startTime,String attribute,String value){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("UPDATE Task SET " + attribute + " = " + value + "where phoneNumberA = " + phoneNumberA + " AND startTime = " + startTime);
    }

    /**
     * 查询任务的内容
     * 参数：用户号码
     */
    public ArrayList<Task> queryTimeOfTask(String phoneNumberA){
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Task> tasks = new ArrayList<Task>();
        Cursor cursor = db.query("Task",new String[]{"startTime","endTime","taskContent","foucusDegree","efficiency","successOrNot"},null,null,null,null,null);
        while (cursor.moveToNext()) {
            Task task = new Task();
            task.setStartTime(cursor.getInt(cursor.getColumnIndex("startTime")));
            task.setEndTime(cursor.getInt(cursor.getColumnIndex("endTime")));
            task.setTaskContent(cursor.getString(cursor.getColumnIndex("taskContent")));
            task.setFoucusDegree(cursor.getInt(cursor.getColumnIndex("foucusDegree")));
            task.setEfficiency(cursor.getInt(cursor.getColumnIndex("efficiency")));
            task.setSuccessOrNot(cursor.getInt(cursor.getColumnIndex("successOrNot")));
        }
        db.close();
        return tasks;
    }





}
