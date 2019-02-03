package com.example.asus.videocontent;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.asus.videocontent.Base.DatabaseHandler;
import com.example.asus.videocontent.Base.Querrys;
import com.example.asus.videocontent.Base.VideoModel;
import com.example.asus.videocontent.R;
import com.example.asus.videocontent.RecordingHandler.Recorder;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.example.asus.videocontent.MainActivity.MEDIA_TYPE_VIDEO;

public class VideoGalleryActivity extends AppCompatActivity{

    ListView listView;
    ArrayList<String> videoList;
    private Uri fileUri;
    static HashMap<String, String> videoPathes;
    static HashMap<String, Float> videoSizes;
    static HashMap<String, Float> videoPreview;
    static ArrayList<VideoModel> list;
    static ArrayAdapter adapter;

    private static DatabaseHandler dbOpenHandler;
    private static SQLiteDatabase db;
    private static Querrys querrys;

    private static final String path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS) + "/MyCameraVideo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_gallery_activity);
        initView();
        GetDbHandler();
        UpdateView();
    }

    private void initView(){
        listView=findViewById(R.id.listView);
        fileUri = Recorder.getOutputMediaFileUri(MEDIA_TYPE_VIDEO, this);
        videoList= new ArrayList<>();
        videoPathes = new HashMap<>();
        videoSizes = new HashMap<>();
        adapter= new ArrayAdapter(this,android.R.layout.simple_list_item_1,videoList);
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Options:");
        getMenuInflater().inflate(R.menu.listview_longclick_menu, menu) ;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                String fullVideoName = videoList.get(menuInfo.position).toString();
                String videoName = fullVideoName.substring(0, fullVideoName.indexOf('\n'));
                String videoPathKey = path + "/" + videoName;
                File selectedVideo = new File(videoPathKey);
                selectedVideo.delete();
                querrys.DeleteVideo(videoPathKey);
                adapter.remove(videoList.get(menuInfo.position));
                adapter.notifyDataSetChanged();
                return true;
                default:
                    return super.onContextItemSelected(item);

        }
    }

    private void UpdateView(){
        GetVideoPathes();
        InsertToBase();
        list = querrys.getVideoName();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (VideoModel item: list) {
                    if((adapter.getItem(position)).toString().split("\n")[0].equals(item.getName())){
                        Intent ii = new Intent(VideoGalleryActivity.this, com.example.asus.videocontent.CurrentVideoActivity.class);
                        ii.putExtra("pathKey", item.getPath());
                        startActivity(ii);
                        break;
                    }
                }
            }
        });
    }
    private void InsertToBase(){
        for (String item: videoPathes.keySet()) {

            ContentValues values = new ContentValues();
            values.put("Path", videoPathes.get(item) + item);
            values.put("Size", videoSizes.get(item));
            values.put("Name", item);

            try {
                db.insertWithOnConflict("Media", null, values, SQLiteDatabase.CONFLICT_IGNORE);
            } catch (Exception e) {

            }
        }
    }


    private void GetVideoPathes(){
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            Date dateCreate = new Date(files[i].getAbsoluteFile().lastModified());
            videoPathes.put(files[i].getName() ,directory.getPath() + "/");
            videoSizes.put(files[i].getName(), (float)files[i].getAbsoluteFile().length()/(1024*1024));
            videoList.add(files[i].getName() + "\n\n \uD83C\uDFA5 " + (float)files[i].getAbsoluteFile().length()/(1024*1024) + "MB\n\n" + dateCreate.toString());
        }
    }

    private void GetDbHandler(){
        dbOpenHandler = new DatabaseHandler(this);
        db = dbOpenHandler.getWritableDatabase();
        querrys = new Querrys(db);
    }
}
