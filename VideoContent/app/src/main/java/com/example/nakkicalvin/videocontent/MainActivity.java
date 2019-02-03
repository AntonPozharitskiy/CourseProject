package com.example.asus.videocontent;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.asus.videocontent.RecordingHandler.CameraUtils;
import com.example.asus.videocontent.RecordingHandler.Recorder;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.util.VKUtil;


import java.io.File;
import java.util.Arrays;

import static com.example.asus.videocontent.PermissionChecker.Validator.READ_GRANTED;
import static com.example.asus.videocontent.PermissionChecker.Validator.REQUEST_CODE_CAM;
import static com.example.asus.videocontent.PermissionChecker.Validator.Requests;
//01.01.2019
public class MainActivity extends AppCompatActivity {

    private Toolbar mTopToolbar;
    private Uri fileUri;
    private final int CAMERA_PIC_REQUEST = 101;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";
    public static final String GALLERY_DIRECTORY_NAME = "MyCameraVideo";
    public static final String VIDEO_EXTENSION = "mp4";

    Button recordButton;

    private String[] scope = new String[]{VKScope.VIDEO};
    private static String imageStoragePath;
    private TextView txtDescription;
    private VideoView videoPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
        System.out.println(Arrays.asList(fingerprints));
        setContentView(R.layout.activity_main);
        initView();
        Requests(this);
        restoreFromBundle(savedInstanceState);
    }


    private void initView(){
        recordButton = findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });
        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        txtDescription = findViewById(R.id.txt_desc);
        videoPreview = findViewById(R.id.videoPreview);
        View someView = findViewById(R.id.mainlayout);

        // Find the root view
        View root = someView.getRootView();

        // Set the color
        root.setBackgroundColor(getResources().getColor(android.R.color.white));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAM:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if(getPackageManager().hasSystemFeature(
                            PackageManager.FEATURE_CAMERA_ANY)){
                        READ_GRANTED = true;
                    }
                }
                recordButton.setEnabled(READ_GRANTED);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.viewfolder:
                viewVideoFolder();
                break;
            case R.id.log_vk:
                if(!VKSdk.isLoggedIn()){
                    VKSdk.login(this, scope);
                }
                else {
                    viewVKVideosFolder();
                }
        }


        return super.onOptionsItemSelected(item);
    }

    private void startRecording()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_VIDEO);
        fileUri = Recorder.getOutputMediaFileUri(MEDIA_TYPE_VIDEO, this);
        if (fileUri != null){
            imageStoragePath = fileUri.getPath();
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }

    private void viewVideoFolder(){
        Intent videoFolder = new Intent(this, VideoGalleryActivity.class);
        startActivity(videoFolder);
    }
    private void restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_IMAGE_STORAGE_PATH)) {
                imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
                if (!TextUtils.isEmpty(imageStoragePath)) {
                    if (imageStoragePath.substring(imageStoragePath.lastIndexOf(".")).equals("." + "mp4")) {
                        previewVideo();
                    }
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putString(KEY_IMAGE_STORAGE_PATH, imageStoragePath);
    }

    /**
     * Restoring image path from saved instance state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
    }
    private void previewVideo() {
        try {
            // hide image preview
            txtDescription.setVisibility(View.GONE);

            videoPreview.setVisibility(View.VISIBLE);
            videoPreview.setVideoURI(fileUri);
            videoPreview.setMediaController(new MediaController(this));
            videoPreview.requestFocus();
            // start playing
            videoPreview.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void viewVKVideosFolder() {
        Intent intent = new Intent(MainActivity.this, VKVideoGallery.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_PIC_REQUEST) {

            if (resultCode == RESULT_OK) {

                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // video successfully recorded
                // preview the recorded video
                previewVideo();
                Toast.makeText(this, "VideoModel saved to: " +
                        data.getData(), Toast.LENGTH_LONG).show();

            } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(this, "VideoModel capture failed.",
                        Toast.LENGTH_LONG).show();
            }
        }
        else if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                viewVKVideosFolder();
            }
            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        }))

            super.onActivityResult(requestCode, resultCode, data);
    }

}
