package com.nectar.timeby.gui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.nectar.timeby.gui.interfaces.DigitalClockConstants;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dean on 15/7/14.
 */
public class CountDownView extends SurfaceView implements Runnable, Callback, DigitalClockConstants {
    private static final String TAG = "CountDownView";
    private SurfaceHolder mHolder;    //用于控制SurfaceView
    private Canvas mCanvas;    //声明画布
    private Paint mPaint;    //声明画笔
    private int cColor;    //声明画布颜色

    private Thread mThread;    //声明一个线程
    private int RADIUS;
    private int MARGIN_TOP;
    private int MARGIN_LEFT;
    //    private static final int RADIUS=1; //声明小球半径
//    private static final int MARGIN_TOP = 30;
//    private static final int MARGIN_LEFT = 40;
    private ArrayList<int[][]> list = new ArrayList<int[][]>();

    //    private static final String END="2015-07-19 00:00:00";
    private String END = "2015-07-19 00:00:00";
    private final String START;
    private Date endDate = null;
    private Date startDate = null;

    private Date curTime = new Date();

    private long curShowTimeSeconds = 0;
    private long ret;

    private long timeSub;
    private long setTime;


    public CountDownView(Context context, int DPI, int penColor, int canColor, long time, int sHour, int sMin, int eHour, int eMin, String sAPM, String eAPM) {

        super(context);
        setDisplayScreen(DPI, canColor);
        setTime = time;
        Log.i(TAG + "setTime:", "" + time);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        Log.i(TAG + "year", "" + year);
        int month = cal.get(Calendar.MONTH) + 1;
        Log.i(TAG + "month", "" + month);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Log.i(TAG + "day:", "" + day);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        Log.i(TAG + " hour:", "" + hour);

        Log.i("sHour", "" + sHour);
        Log.i("sMin", "" + sMin);
        Log.i("eHour", "" + eHour);
        Log.i("eMin", "" + eMin);
        if (sAPM.equals("AM") && eAPM.equals("AM")) {
            if (hour >= 12) {
//                sHour += 24;
//                eHour += 24;
                day += 1;
            }
        } else if (sAPM.equals("AM") && eAPM.equals("PM")) {
            if (hour >= 12) {
//                sHour += 24;
//                eHour += 36;
                day += 1;
                eHour += 12;
            } else {
                eHour += 12;
            }
        } else if (sAPM.equals("PM") && eAPM.equals("AM")) {
            if (hour >= 12) {
//                sHour += 36;
//                eHour += 48;
                day += 1;
                sHour += 12;
                eHour += 24;
            } else {
                sHour += 12;
                eHour += 24;
            }
        } else if (sAPM.equals("PM") && eAPM.equals("PM")) {
            if (hour >= 12) {
                if ((hour - 12) > sHour) {
                    day += 1;
                    sHour += 12;
                    eHour += 12;
                } else {
                    sHour += 12;
                    eHour += 12;
                }
            } else {
                sHour += 12;
                eHour += 12;
            }
        }
        Log.i(TAG + "day1:", "" + day);
        Log.i("sHour1", "" + sHour);
        Log.i("sMin1", "" + sMin);
        Log.i("eHour1", "" + eHour);
        Log.i("eMin1", "" + eMin);

        END = "" + year + "-" + month + "-" + day + " " + eHour + ":" + eMin + ":00";
        START = "" + year + "-" + month + "-" + day + " " + sHour + ":" + sMin + ":00";
//        START = "2015-07-18 00:00:00";


        mHolder = this.getHolder();       //获得SurfaceHolder对象
        mHolder.addCallback(this);        //添加状态监听
        mPaint = new Paint();             //创建一个画笔对象
        mPaint.setColor(penColor);      //设置画笔的颜色

    }

    public int setDisplayScreen(int mDPI, int mcanColor) {
        RADIUS = mDPI / 90;
        MARGIN_TOP = mDPI / 8;
        MARGIN_LEFT = mDPI / 11;
        cColor = mcanColor;
        return 0;
    }


    private long getCurrentShowTimeSeconds() {


        curTime = new Date();
//            Log.i("curTime",""+curTime);
        timeSub = startDate.getTime() - curTime.getTime();
//          Log.i("time",""+timeSub);
//        ret = endDate.getTime() - startDate.getTime();
        if (timeSub < 0) {
            ret = endDate.getTime() - curTime.getTime();
        } else {
            ret = endDate.getTime() - startDate.getTime();
        }
        ret = Math.round(ret / 1000);
//        Log.i("ret",""+ret);
        return ret >= 0 ? ret : 0;

    }


    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        mThread = new Thread(this);    //创建线程对象

        mThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {


    }

    @SuppressLint("SimpleDateFormat")
    public Date strToDateLong(String strDate) {
        if ("".equals(strDate) || null == strDate) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }


    @Override
    public void run() {
        try {

            list.add(data0);
            list.add(data1);
            list.add(data2);
            list.add(data3);
            list.add(data4);
            list.add(data5);
            list.add(data6);
            list.add(data7);
            list.add(data8);
            list.add(data9);
            list.add(data10);
            endDate = strToDateLong(END);
            startDate = strToDateLong(START);

            Log.i("startDate", "" + startDate);
            Log.i("endDate:", "" + endDate);
            while (true) {
                curShowTimeSeconds = getCurrentShowTimeSeconds();
                mDraw();
                Thread.sleep(50);
            }

        } catch (Exception e) {
            Log.e(TAG, "run error", e);
        }


    }

    /**
     * 自定义绘图方法
     * 2014-12-19 下午2:22:45
     */
    public void mDraw() {
        mCanvas = mHolder.lockCanvas();             //获得画布对象,开始对画布画画
//        mCanvas.drawColor(Color.rgb(188,236,188));             //设置画布颜色为淡绿
        mCanvas.drawColor(cColor);
        canvas(mCanvas);
        mHolder.unlockCanvasAndPost(mCanvas);       //把画布显示在屏幕上
    }

    public void canvas(Canvas mCanvas) {
        //画圆,(x轴,y轴,半径,画笔)

        int hours = (int) curShowTimeSeconds / 3600;
        int minutes = (int) (curShowTimeSeconds - hours * 3600) / 60;
        int seconds = (int) curShowTimeSeconds % 60;
        canvasDigit(MARGIN_LEFT, MARGIN_TOP, hours / 10, mCanvas);
        canvasDigit(MARGIN_LEFT + 15 * (RADIUS + 1), MARGIN_TOP, hours % 10, mCanvas);
        canvasDigit(MARGIN_LEFT + 30 * (RADIUS + 1), MARGIN_TOP, 10, mCanvas);
        canvasDigit(MARGIN_LEFT + 39 * (RADIUS + 1), MARGIN_TOP, minutes / 10, mCanvas);
        canvasDigit(MARGIN_LEFT + 54 * (RADIUS + 1), MARGIN_TOP, minutes % 10, mCanvas);
        canvasDigit(MARGIN_LEFT + 69 * (RADIUS + 1), MARGIN_TOP, 10, mCanvas);
        canvasDigit(MARGIN_LEFT + 78 * (RADIUS + 1), MARGIN_TOP, seconds / 10, mCanvas);
        canvasDigit(MARGIN_LEFT + 93 * (RADIUS + 1), MARGIN_TOP, seconds % 10, mCanvas);
    }


    public void canvasDigit(int x, int y, int num, Canvas mCanvas) {
        int[][] data = list.get(num);
        for (int i = 0; i < data.length; i++) {

            for (int j = 0; j < data[i].length; j++) {

                if (data[i][j] == 1) {

                    mCanvas.drawCircle(x + j * 2 * (RADIUS + 1) + (RADIUS + 1), y + i * 2 * (RADIUS + 1) + (RADIUS + 1), RADIUS, mPaint);

                }

            }

        }
    }
}
