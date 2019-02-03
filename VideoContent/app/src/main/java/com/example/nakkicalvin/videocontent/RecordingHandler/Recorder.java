package com.example.asus.videocontent.RecordingHandler;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;

import static com.example.asus.videocontent.MainActivity.MEDIA_TYPE_VIDEO;


/**
 * Created by NakkiCalvin on 16.12.2018.
 */

public class Recorder extends AppCompatActivity {

    public static Uri getOutputMediaFileUri(int type, Activity activity){
        //return Uri.fromFile(getOutputMediaFile(type));
        return FileProvider.getUriForFile(activity, "com.mydomain.fileprovider", getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){

        // Check that the SDCard is mounted
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "MyCameraVideo");


        // Create the storage directory(MyCameraVideo) if it does not exist
        if (! mediaStorageDir.exists()){

            if (! mediaStorageDir.mkdirs()){


                Toast.makeText(null, "Failed to create directory MyCameraVideo.",
                        Toast.LENGTH_LONG).show();

                Log.d("MyCameraVideo", "Failed to create directory MyCameraVideo.");
                return null;
            }
        }
        // Create a media file name

        // For unique file name appending current timeStamp with file name
        java.util.Date date= new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(date.getTime());

        File mediaFile;

        if(type == MEDIA_TYPE_VIDEO) {

            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
            //mediaFile = new File(Environment.getExternalStorageDirectory(), "VID_" + timeStamp + ".mp4");

        } else {
            return null;
        }

        return mediaFile;
    }

}
