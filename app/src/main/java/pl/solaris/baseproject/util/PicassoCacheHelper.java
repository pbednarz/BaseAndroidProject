package pl.solaris.baseproject.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.StatFs;

import com.squareup.okhttp.Cache;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttpDownloader;

import java.io.File;
import java.io.IOException;

/**
 * Created by pbednarz on 2014-10-08.
 */
public class PicassoCacheHelper {

    private static final String BIG_CACHE_PATH = "picasso-cache";
    private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_DISK_CACHE_SIZE = 10 * 1024 * 1024; // 10MB

    private static final float MAX_AVAILABLE_SPACE_USE_FRACTION = 0.9f;
    private static final float MAX_TOTAL_SPACE_USE_FRACTION = 0.25f;

    static Downloader createDownloader(Context ctx) {
        File cacheDir = createDefaultCacheDir(ctx, BIG_CACHE_PATH);
        long cacheSize = calculateDiskCacheSize(cacheDir);

        return new OkHttpDownloader(cacheDir, cacheSize);
    }

    public static Cache createCache(Context ctx) {
        File cacheDir = createDefaultCacheDir(ctx, BIG_CACHE_PATH);
        long cacheSize = calculateDiskCacheSize(cacheDir);
        Cache cache = null;
        try {
            cache = new Cache(cacheDir, cacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cache;
    }

    static File createDefaultCacheDir(Context context, String path) {
        File cacheDir = context.getApplicationContext().getExternalCacheDir();
        if (cacheDir == null)
            cacheDir = context.getApplicationContext().getCacheDir();
        File cache = new File(cacheDir, path);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }

    /**
     * Calculates bonded min max cache size. Min value is {@link #MIN_DISK_CACHE_SIZE}
     *
     * @param dir cache dir
     * @return disk space in bytes
     */

    static long calculateDiskCacheSize(File dir) {
        long size = Math.min(calculateAvailableCacheSize(dir), MAX_DISK_CACHE_SIZE);
        return Math.max(size, MIN_DISK_CACHE_SIZE);
    }

    /**
     * Calculates minimum of available or total fraction of disk space
     *
     * @param dir
     * @return space in bytes
     */
    @SuppressLint("NewApi")
    static long calculateAvailableCacheSize(File dir) {
        long size = 0;
        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            int sdkInt = Build.VERSION.SDK_INT;
            long totalBytes;
            long availableBytes;
            if (sdkInt < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                int blockSize = statFs.getBlockSize();
                availableBytes = ((long) statFs.getAvailableBlocks()) * blockSize;
                totalBytes = ((long) statFs.getBlockCount()) * blockSize;
            } else {
                availableBytes = statFs.getAvailableBytes();
                totalBytes = statFs.getTotalBytes();
            }
            size = (long) Math.min(availableBytes * MAX_AVAILABLE_SPACE_USE_FRACTION, totalBytes * MAX_TOTAL_SPACE_USE_FRACTION);
        } catch (IllegalArgumentException ignored) {
        }
        return size;
    }

}