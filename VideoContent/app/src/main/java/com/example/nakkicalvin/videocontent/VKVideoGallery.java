package com.example.asus.videocontent;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;
import java.util.List;

public class VKVideoGallery extends AppCompatActivity {

    private ListView VKVideoList;
    private String[] fields = new String[]{"id","title","duration", "photo_800", "player"};
    private ArrayList<Object> VKVideoDetails;
    private ImageView curVidImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vkvideo_gallery);
        VKVideoList = (ListView)findViewById(R.id.vk_videos);
        VKVideoDetails = new ArrayList<>();
        GetVideosFromVk();
    }

    private void GetVideosFromVk() {
        VKRequest getAllVideosRequest = VKApi.video().get(VKParameters.from(VKApiConst.FIELDS, fields));
        getAllVideosRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                final VKList videoList = (VKList) response.parsedModel;
                for(Object video: videoList){
                    VKApiVideo currentVideo = (VKApiVideo) video;
                    curVidImg = new ImageView(VKVideoGallery.this);
                    curVidImg.setImageURI(Uri.parse(currentVideo.photo_320));
                    VKVideoDetails.add(currentVideo.title + "\n \uD83C\uDFA5 \n" + convertTime(currentVideo.duration));
                }
                final ArrayAdapter<Object> videoAdapter = new ArrayAdapter<Object>(VKVideoGallery.this,
                        android.R.layout.simple_expandable_list_item_1,VKVideoDetails);
                VKVideoList.setAdapter(videoAdapter);
                VKVideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            VKApiVideo clickedVideo = (VKApiVideo) videoList.get(position);
                            String videoPath = clickedVideo.player;
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoPath));
                            startActivity(browserIntent);
                    }
                });
            }
        });
    }

    private String convertTime(int seconds) {
        int rem, mins;
        String normalTimeLabel;
        if(seconds < 10) {
            normalTimeLabel = "00:" + "0"+seconds;
        }
        else if(seconds >= 10 && seconds < 60)
        {
            normalTimeLabel = "00:" + seconds;
        }
        else if(seconds >= 60 && seconds < 600)
        {
            rem = seconds%60;
            mins = (seconds - rem)%60;
            normalTimeLabel = "0"+mins+":"+rem;
        }
        else {
            rem = seconds%60;
            mins = (seconds - rem)%60;
            normalTimeLabel = "0"+mins+":"+rem;
        }
        return normalTimeLabel;
    }
}
