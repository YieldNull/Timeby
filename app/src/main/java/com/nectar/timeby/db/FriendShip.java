package com.nectar.timeby.db;

/**
 * Created by wsw on 2015/7/20.
 */
public class FriendShip {
    private String phoneNumberB;
    private String remark;
    private String phoneNumberA;

    public String getPhoneNumberA() {
        return phoneNumberA;
    }

    public void setPhoneNumberA(String phoneNumberA) {
        this.phoneNumberA = phoneNumberA;
    }



    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPhoneNumberB() {
        return phoneNumberB;
    }

    public void setPhoneNumberB(String phoneNumber) {
        this.phoneNumberB = phoneNumber;
    }

}
