package com.nectar.timeby.gui;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.nectar.timeby.R;


public class ViewFlipperActivity extends Activity implements OnGestureListener {

    private int flag = 1; //用于判断当前处于哪一个页面，如果到第四个页面再往下翻则跳转
    private ViewFlipper viewFlipper = null;
    private GestureDetector gestureDetector = null;
    private int[] imageID = {R.drawable.guide1, R.drawable.guide2, R.drawable.guide3, R.drawable.guide4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_flipper);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
        //生成GestureDetector对象,用于检测手势
        gestureDetector = new GestureDetector(this, this);


        //然后是添加切换的引导界面
        for (int i = 0; i < imageID.length; i++) {
            //定义一个ImageView对象
            ImageView image = new ImageView(this);
            image.setImageResource(imageID[i]);
            //充满父控件
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(image, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.gestureDetector.onTouchEvent(event);
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //如果横向滑动超过120像素，则做切换动作


        if ((e2.getX() - e1.getX()) > 120) {
            if (flag > 1) {
                //添加动画
                this.viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
                this.viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
                this.viewFlipper.showPrevious();
                flag--;
                return true;
            } else {
                return true;
            }

        } else if ((e1.getX() - e2.getX()) > 120) {

            if (flag < 4) {
                //添加动画
                this.viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
                this.viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
                this.viewFlipper.showNext();
                flag++;
                return true;
            } else {
                //这里就用来处理引导界面之后的跳转了。
                Intent intent = new Intent(ViewFlipperActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);//进入主界面后将之前的Activity栈清空
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                return true;
            }
        }
        return true;
    }
}
