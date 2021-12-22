package net.arvin.selector.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import net.arvin.selector.data.FMedia;

import java.io.File;
import java.text.SimpleDateFormat;


/**
 * @author：luck
 * @date：2019-10-21 17:10
 * @describe：资源处理工具类
 */
public class FMediaUtils {


    private static final String RELATIVE_PATH = "relative_path";

    /**
     * 根据时间戳创建文件名
     *
     * @param prefix 前缀名
     * @return
     */
    private static String getCreateFileName(String prefix) {
        long millis = System.currentTimeMillis();
        return prefix + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(millis);
    }

    private static String getDCIMCameraPath() {
        String absolutePath;
        try {
            absolutePath = Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera";
            File folder = new File(absolutePath);
            if (!folder.exists()) {
                boolean mkdirs = folder.mkdirs();
                Log.d("Log_for_App", "getDCIMCameraPath: " + mkdirs);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return absolutePath;
    }

    /**
     * 创建一条图片地址uri,用于保存拍照后的照片
     *
     * @param ctx
     * @return 图片的uri
     */
    public static Uri createImageUri(final Context ctx) {
        Context context = ctx.getApplicationContext();
        Uri[] imageFilePath = {null};
        String status = Environment.getExternalStorageState();

        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(5);

        String displayName = getCreateFileName("IMG_");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);

        if (Build.VERSION.SDK_INT >= 29) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, String.valueOf(System.currentTimeMillis()));
        }

        values.put(MediaStore.Files.FileColumns.DATA, getDCIMCameraPath() + "/" + displayName + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            if (Build.VERSION.SDK_INT >= 29) {
                values.put(RELATIVE_PATH, "DCIM/Camera");
            }
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Images.Media.getContentUri("external"), values);
        } else {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Images.Media.getContentUri("internal"), values);
        }
        return imageFilePath[0];
    }


    /**
     * 创建一条视频地址uri,用于保存录制的视频
     *
     * @param ctx
     * @return 视频的uri
     */
    public static Uri createVideoUri(final Context ctx) {
        Context context = ctx.getApplicationContext();
        Uri[] imageFilePath = {null};
        String status = Environment.getExternalStorageState();
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(5);

        String displayName = getCreateFileName("IMG_");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);

        if (Build.VERSION.SDK_INT >= 29) {
            values.put(MediaStore.Video.Media.DATE_TAKEN, time);
        }

        values.put(MediaStore.Files.FileColumns.DATA, getDCIMCameraPath() + "/" + displayName + ".mp4");
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/*");
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            if (Build.VERSION.SDK_INT >= 29) {
                values.put(RELATIVE_PATH, "DCIM/Camera");
            }
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Video.Media.getContentUri("external"), values);
        } else {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Video.Media.getContentUri("internal"), values);
        }
        return imageFilePath[0];
    }

    /**
     * 获取DCIM文件下最新一条拍照记录
     *
     * @return
     */
    public static FMedia getDCIMLastImageId(Context context) {
        Cursor data = null;
        FMedia media = null;
        try {
            String selection = MediaStore.Images.Media.DATA + " like ?";
            //定义selectionArgs：
            String[] selectionArgs = {"%*%"};
            if (Build.VERSION.SDK_INT >= 30) {
                Bundle queryArgs = FMediaUtils.createQueryArgsBundle(selection, selectionArgs, 1, 0);
                data = context.getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, queryArgs, null);
            } else {
                String orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0";
                data = context.getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, selection, selectionArgs, orderBy);
            }
            if (data != null && data.getCount() > 0 && data.moveToFirst()) {
                long id = data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                String mimeType = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
                long bucketId = data.getLong(data.getColumnIndexOrThrow("bucket_id"));
                String bucketName = data.getString(data.getColumnIndexOrThrow("bucket_display_name"));
                long size = data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
                int width = data.getInt(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH));
                int height = data.getInt(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT));
                long dateTaken = data.getLong(data.getColumnIndexOrThrow("datetaken"));
                String path = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                long during = 0;
                media = FMedia.createMedia(id, mimeType, bucketId, bucketName,
                        size, during, width, height, dateTaken, path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                data.close();
            }
        }
        return media;
    }

    /**
     * 获取Camera文件下最新一条拍照记录
     *
     * @return
     */
    public static FMedia getCameraFirstBucketId(Context context) {
        Cursor data = null;
        FMedia media = null;
        try {
            //selection: 指定查询条件
            String selection = MediaStore.Files.FileColumns.DATA + " like ?";
            //定义selectionArgs：
            String[] selectionArgs = {"%%"};
            if (Build.VERSION.SDK_INT >= 30) {
                Bundle queryArgs = FMediaUtils.createQueryArgsBundle(selection, selectionArgs, 1, 0);
                data = context.getApplicationContext().getContentResolver().query(MediaStore.Files.getContentUri("external"), null, queryArgs, null);
            } else {
                String orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0";
                data = context.getApplicationContext().getContentResolver().query(MediaStore.Files.getContentUri("external"), null, selection, selectionArgs, orderBy);
            }
            if (data != null && data.getCount() > 0 && data.moveToFirst()) {
                long id = data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                String mimeType = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
                long bucketId = data.getLong(data.getColumnIndexOrThrow("bucket_id"));
                String bucketName = data.getString(data.getColumnIndexOrThrow("bucket_display_name"));
                long size = data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
                int width = data.getInt(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH));
                int height = data.getInt(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT));
                long dateTaken = data.getLong(data.getColumnIndexOrThrow("datetaken"));
                String path = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                long during = data.getLong(data.getColumnIndexOrThrow("duration"));
//                try {
//                    if (during == 0) {
//                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象
//                        mmr.setDataSource(path);
//                        during = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                media = FMedia.createMedia(id, mimeType, bucketId, bucketName,
                        size, during, width, height, dateTaken, path);
                Log.e("cxd", media.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                data.close();
            }
        }
        return media;
    }


    /**
     * 获取刚录取的音频文件
     *
     * @param uri
     * @return
     */
    @Nullable
    public static String getAudioFilePathFromUri(Context context, Uri uri) {
        String path = "";
        try (Cursor cursor = context.getApplicationContext().getContentResolver()
                .query(uri, null, null, null, null)) {
            if (cursor != null) {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
                path = cursor.getString(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }


    /**
     * R  createQueryArgsBundle
     *
     * @param selection
     * @param selectionArgs
     * @param limitCount
     * @param offset
     * @return
     */
    public static Bundle createQueryArgsBundle(String selection, String[] selectionArgs, int limitCount, int offset) {
        Bundle queryArgs = new Bundle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs);
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, MediaStore.Files.FileColumns._ID + " DESC");
            if (Build.VERSION.SDK_INT >= 30) {
                queryArgs.putString("android:query-arg-sql-limit", limitCount + " offset " + offset);
            }
        }
        return queryArgs;
    }

}
