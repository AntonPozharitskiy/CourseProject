package com.example.asus.videocontent.Base;

public class VideoModel {

    private String Path;
    private float Size;
    private String Name;

    public VideoModel(String Path, Float Size, String Name) {
        this.Path = Path;
        this.Size = Size;
        this.Name = Name;
    }

    public String getPath() {
        return Path;
    }

    public Float getSize() {
        return Size;
    }

    public String getName() {
        return Name;
    }

}
