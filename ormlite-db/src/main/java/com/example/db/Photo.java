package com.example.db;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * PGS-Software
 * Created by pbednarz on 2015-02-03.
 */
@DatabaseTable(tableName = "photo")
public class Photo extends BaseModel {
    public static final String ALBUM_ID = "album" + ID;

    @DatabaseField(generatedId = true, columnName = ID)
    public long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = ALBUM_ID)
    public Album album;

    @Expose
    @SerializedName("url")
    @DatabaseField
    public String url;

    public Photo() {
    }

    public Photo(PictureBuilder builder) {
        this.url = builder.url;
    }

    public static final class PictureBuilder {
        private String url;
        private Album album;

        public PictureBuilder(String url) {
            this.url = url;
        }

        public PictureBuilder setAlbum(Album album) {
            this.album = album;
            return this;
        }

        public Photo build() {
            return new Photo(this);
        }
    }
}