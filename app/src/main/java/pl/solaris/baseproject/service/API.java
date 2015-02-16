package pl.solaris.baseproject.service;

import com.example.db.Album;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * PGS-Software
 * Created by pbednarz on 2015-01-16.
 */
public interface API {
    public static final String FOOD_ALBUM = "food";
    public static final String SPORT_ALBUM = "sport";

    @GET("/{name}.json")
    Observable<Album> getAlbum(@Path("name") String albumName);
}
