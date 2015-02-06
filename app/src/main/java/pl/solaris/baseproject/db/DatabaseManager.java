package pl.solaris.baseproject.db;

import android.content.Context;

import com.example.db.Album;
import com.example.db.Photo;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;

import rx.Observable;

/**
 * PGS-Software
 * Created by pbednarz on 2014-12-22.
 */
public final class DatabaseManager {
    private static DatabaseManager instance;
    Dao<Album, Long> albumsDao;
    Dao<Photo, Long> picturesDao;
    private DatabaseHelper databaseHelper;

    private DatabaseManager(Context context) {
        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    public static DatabaseManager getInstance(Context ctx) {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager(ctx.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public Dao<Photo, Long> getPicturesDao() {
        if (picturesDao == null) {
            try {
                picturesDao = databaseHelper.getDao(Photo.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return picturesDao;
    }

    public Dao<Album, Long> getAlbumsDao() {
        if (albumsDao == null) {
            try {
                albumsDao = databaseHelper.getDao(Album.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return albumsDao;
    }

    public void saveAlbum(Album album) {
        try {
            Album albumExist = getAlbumFromTitle(album.title);
            if (albumExist != null) {
                album.id = albumExist.id;
            }
            if (instance.getAlbumsDao().createOrUpdate(album).isUpdated()) {
                DeleteBuilder<Photo, Long> deleteBuilder = instance.getPicturesDao().deleteBuilder();
                deleteBuilder.where().eq(Photo.ALBUM_ID, album);
                deleteBuilder.delete();
            }
            for (Photo photo : album.getPhotos()) {
                photo.album = album;
                instance.getPicturesDao().createOrUpdate(photo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Album getAlbumFromTitle(String albumName) {
        Album album;
        try {
            album = instance.getAlbumsDao().queryBuilder().where().eq(Album.TITLE, albumName).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            album = null;
        }
        return album;
    }

    public Observable<Album> getAlbumObservable(String albumName) {
        return Observable.defer(() -> Observable.just(getAlbumFromTitle(albumName)));
    }
}
