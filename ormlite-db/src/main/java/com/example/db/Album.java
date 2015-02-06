package com.example.db;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

/**
 * PGS-Software
 * Created by pbednarz on 2015-02-03.
 */
@DatabaseTable(tableName = "album")
public class Album extends BaseModel {

    public static final String TITLE = "_title";

    @Expose
    @DatabaseField(generatedId = true, columnName = ID)
    public long id;

    @SerializedName("title")
    @Expose
    @DatabaseField(columnName = TITLE)
    public String title;

    @SerializedName("description")
    @Expose
    @DatabaseField
    public String description;

    @SerializedName("empty")
    @ForeignCollectionField
    ForeignCollection<Photo> albumPhotos;

    @SerializedName("albumPhotos")
    @Expose
    List<Photo> photos;

    public Album() {
    }

    public Album(AlbumBuilder albumBuilder) {
        this.title = albumBuilder.title;
        this.description = albumBuilder.description;
        this.photos = albumBuilder.photos;
    }

    public List<Photo> getPhotos() {
        if (photos == null && albumPhotos != null) {
            photos = new ArrayList<>(albumPhotos);
        }
        return photos;
    }

    public static final class AlbumBuilder {
        private String title;
        private String description;
        private ForeignCollection<Photo> notePhotos;
        private List<Photo> photos;

        public AlbumBuilder(String name) {
            this.title = name;
        }

        public AlbumBuilder setName(String title) {
            this.title = title;
            return this;
        }

        public AlbumBuilder setText(String description) {
            this.description = description;
            return this;
        }

        public AlbumBuilder setNotePhotos(ForeignCollection<Photo> photos) {
            this.notePhotos = photos;
            return this;
        }

        public AlbumBuilder setPhotos(List<Photo> photos) {
            this.photos = photos;
            return this;
        }

        public Album build() {
            return new Album(this);
        }
    }
}
