package com.nectar.timeby.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nectar.timeby.R;
import com.nectar.timeby.util.ImgUtil;
import com.nectar.timeby.util.PrefsUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UserInfoEditActivity extends Activity {
    private static final String TAG = "UserInfoActivity";

    private Spinner mGenderSpinner;
    private ImageView mSpinnerToggle;
    private ImageButton mReturnButton;
    private ImageButton mSubmitButton;

    private ImageView mHeadImage;
    private EditText mNickNameText;
    private EditText mYearText;
    private TextView mShellText;
    private TextView mHammerText;

    private static int[] resources = {R.drawable.icn_user_gender_male,
            R.drawable.icn_user_gender_female};


    /* 临时头像文件 */
    private static final String IMAGE_FILE_NAME = "nectar_head_image.jpg";

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_CROP_REQUEST = 0xa2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);

        mHeadImage = (ImageView) findViewById(R.id.imageView_user_edit_photo);
        mReturnButton = (ImageButton) findViewById(R.id.imageButton_user_edit_return);
        mSubmitButton = (ImageButton) findViewById(R.id.imageButton_user_edit_submit);
        mNickNameText = (EditText) findViewById(R.id.editText_user_edit_nickname);
        mYearText = (EditText) findViewById(R.id.editText_user_edit_year);
        mShellText = (TextView) findViewById(R.id.textView_user_edit_shell);
        mHammerText = (TextView) findViewById(R.id.textView_user_edit_hammer);

        initInfo();

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInfo();
                Intent intent = new Intent(UserInfoEditActivity.this, UserInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        mGenderSpinner = (Spinner) findViewById(R.id.spinner_user_gender);
        mGenderSpinner.setAdapter(new GenderSpinnerAdapter());
        mSpinnerToggle = (ImageView) findViewById(R.id.imageView_user_spinner_toggle);
        mSpinnerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGenderSpinner.performClick();
            }
        });

        mHeadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoEditActivity.this);
                builder.setTitle("选择照片位置").setPositiveButton("相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choseHeadImageFromGallery();
                    }
                }).setNegativeButton("相机", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choseHeadImageFromCameraCapture();
                    }
                }).create().show();
            }
        });
    }

    private void initInfo() {
        Map<String, String> infoMap = PrefsUtil.readUserInfo(this);
        mShellText.setText(infoMap.get(PrefsUtil.PREFS_KEY_USER_INFO_SHELL));
        mHammerText.setText(infoMap.get(PrefsUtil.PREFS_KEY_USER_INFO_HAMMER));

        String headImgName = infoMap.get(PrefsUtil.PREFS_KEY_USER_INFO_PHOTO);
        Bitmap bitmap = ImgUtil.readBitmap(this, headImgName);
        Drawable img = new BitmapDrawable(getResources(), bitmap);
        if (bitmap != null)
            mHeadImage.setImageDrawable(img);
    }

    private void saveInfo() {
        HashMap<String, String> infoMap = new HashMap<>();
        infoMap.put(PrefsUtil.PREFS_KEY_USER_INFO_NICKNAME, mNickNameText.getText().toString());
        infoMap.put(PrefsUtil.PREFS_KEY_USER_INFO_YEAR, mYearText.getText().toString());

        long id = mGenderSpinner.getSelectedItemId();
        if (id == resources[0]) {
            infoMap.put(PrefsUtil.PREFS_KEY_USER_INFO_GENDER, "male");
        } else {
            infoMap.put(PrefsUtil.PREFS_KEY_USER_INFO_GENDER, "female");
        }
        PrefsUtil.updateInfo(this, infoMap);
    }

    // 从本地相册选取图片作为头像
    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent();
        // 设置文件类型
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }

    // 启动手机相机拍摄照片作为头像
    private void choseHeadImageFromCameraCapture() {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 判断存储卡是否可用，存储照片文件
        if (ImgUtil.isExternalStorageAvailable()) {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                    .fromFile(new File(Environment
                            .getExternalStorageDirectory(), IMAGE_FILE_NAME)));
        }

        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplication(), "取消操作", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                //切割、存储、显示
                cropRawPhoto(intent.getData());
                break;
            case CODE_CAMERA_REQUEST:
                if (ImgUtil.isExternalStorageAvailable()) {
                    File tempFile = new File(
                            Environment.getExternalStorageDirectory(),
                            IMAGE_FILE_NAME);
                    cropRawPhoto(Uri.fromFile(tempFile));
                } else {
                    Toast.makeText(getApplication(), "没有SDCard!", Toast.LENGTH_LONG)
                            .show();
                }
                break;
            case CODE_CROP_REQUEST:
                if (intent != null) {
                    showSelectedPhoto(intent);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        Log.i(TAG, uri.toString());

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", mHeadImage.getWidth());
        intent.putExtra("outputY", mHeadImage.getHeight());
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CODE_CROP_REQUEST);
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     * 在这里可以将裁剪之后的Bitmap对象之类的进行上传。
     */
    private void showSelectedPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            String fileName = ImgUtil.getHeadImgFileName(PrefsUtil.getUserName(this));
            ImgUtil.saveHeadBitmap(this, fileName, photo);
            mHeadImage.setImageBitmap(photo);
        }
    }


    /**
     * spinner的适配器
     */
    private class GenderSpinnerAdapter extends BaseAdapter {

        private class ViewHolder {
            ImageView mImageView;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            ViewHolder viewHolder;

            if (convertView == null) {
                itemView = getLayoutInflater().inflate(R.layout.user_info_spinner_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mImageView = (ImageView) itemView.findViewById(R.id.imageView);
                itemView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mImageView.setImageDrawable(
                    getResources().getDrawable(resources[position]));
            return itemView;
        }

        @Override
        public int getCount() {
            return resources.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return resources[position];
        }

    }
}
