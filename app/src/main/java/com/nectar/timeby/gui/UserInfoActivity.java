package com.nectar.timeby.gui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nectar.timeby.R;
import com.nectar.timeby.util.ImgUtil;
import com.nectar.timeby.util.PrefsUtil;

import java.util.Map;

/**
 * Created by finalize on 7/18/15.
 */
public class UserInfoActivity extends Activity {

    /* 临时头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;


    private ImageButton mReturnButton;
    private ImageButton mEditButton;
    private ImageView mHeadImage;

    private TextView mNickNameText;
    private TextView mYearText;
    private TextView mShellText;
    private TextView mHammerText;
    private ImageView mGenderImage;

    public UserInfoActivity() {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mReturnButton = (ImageButton) findViewById(R.id.imageButton_user_return);
        mEditButton = (ImageButton) findViewById(R.id.imageButton_user_edit);
        mHeadImage = (ImageView) findViewById(R.id.imageView_user_photo);
        mGenderImage = (ImageView) findViewById(R.id.imageView_user_gender);
        mNickNameText = (TextView) findViewById(R.id.textView_user_nickname);
        mYearText = (TextView) findViewById(R.id.textView_user_year);
        mShellText = (TextView) findViewById(R.id.textView_user_shell);
        mHammerText = (TextView) findViewById(R.id.textView_user_hammer);

        initInfo();

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, UserInfoEditActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initInfo() {
        Map<String, String> infoMap = PrefsUtil.readUserInfo(this);
        mNickNameText.setText(infoMap.get(PrefsUtil.PREFS_KEY_USER_INFO_NICKNAME));
        mYearText.setText("年龄 "+infoMap.get(PrefsUtil.PREFS_KEY_USER_INFO_YEAR));
        mShellText.setText(infoMap.get(PrefsUtil.PREFS_KEY_USER_INFO_SHELL));
        mHammerText.setText(infoMap.get(PrefsUtil.PREFS_KEY_USER_INFO_HAMMER));

        String gender = infoMap.get(PrefsUtil.PREFS_KEY_USER_INFO_GENDER);
        if (gender.equalsIgnoreCase("male")) {
            mGenderImage.setImageDrawable(getResources().getDrawable(R.drawable.icn_user_gender_male));
        } else {
            mGenderImage.setImageDrawable(getResources().getDrawable(R.drawable.icn_user_gender_female));
        }

        String headImgName = infoMap.get(PrefsUtil.PREFS_KEY_USER_INFO_PHOTO);
        Bitmap bitmap = ImgUtil.readBitmap(this, headImgName);
        Drawable img = new BitmapDrawable(getResources(), bitmap);
        if (bitmap != null)
            mHeadImage.setImageDrawable(img);
    }
}
