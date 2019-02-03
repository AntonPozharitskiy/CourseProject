package com.example.asus.videocontent;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;


import java.io.File;

public class CurrentVideoActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.current_video_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        videoView = findViewById(R.id.videoview);
        MediaPlayer mediaPlayer = new MediaPlayer();
        MediaController mc = new MediaController(this);
        mc.setAnchorView(videoView);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if(b!= null){
                String path = (String) b.get("pathKey");
                videoView.setVideoURI(Uri.parse(path));
                videoView.setMediaController(mc);
                videoView.start();
        }
    }
}
