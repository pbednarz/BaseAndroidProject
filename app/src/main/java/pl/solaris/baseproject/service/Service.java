package pl.solaris.baseproject.service;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import pl.solaris.baseproject.util.PicassoCacheHelper;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;

/**
 * PGS-Software
 * Created by pbednarz on 2015-01-16.
 */
public class Service {
    public static final String SERVER_ADDRESS = "http://solaris.comeze.com/albumAPI/";
    private static Service instance;
    private static OkHttpClient okHttpClient;
    private static Picasso picassoInstance;

    private Service(Context context) {
        okHttpClient = new OkHttpClient();
        okHttpClient.setCache(PicassoCacheHelper.createCache(context));
        final Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttpDownloader(okHttpClient));
        picassoInstance = builder.build();
        picassoInstance.setIndicatorsEnabled(true);
        picassoInstance.setLoggingEnabled(true);
    }

    public static Service getInstance(Context context) {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new Service(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public API getNewClientInstance() {
        return getNewClientInstance(new OkClient(okHttpClient));
    }

    private API getNewClientInstance(Client client) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(SERVER_ADDRESS)
                .setRequestInterceptor(request -> request.addHeader("Authorization", "user:password"))
                .build();
        return restAdapter.create(API.class);
    }

    public Picasso getPicasso() {
        return picassoInstance;
    }
}
