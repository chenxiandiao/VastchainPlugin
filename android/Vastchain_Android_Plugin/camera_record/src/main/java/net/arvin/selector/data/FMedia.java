package net.arvin.selector.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Arrays;

/**
 * Created by arvinljw on 2020/7/16 16:13
 * Function：
 * Desc：
 */
public class FMedia implements Parcelable, Cloneable {
    private long id;
    private String mimeType;
    private long bucketId;
    private String bucketName;
    private long size;
    private long duration;
    private int width;
    private int height;
    private long dateTaken;
    private String path;
    public String picKey;

    private int choseNum;

    public static FMedia createMedia(long id, String mimeType, long bucketId, String bucketName, long size,
                                     long duration, int width, int height, long dateTaken, String path) {
        FMedia media = new FMedia();
        media.id = id;
        media.mimeType = mimeType;
        media.bucketId = bucketId;
        media.bucketName = bucketName;
        media.size = size;
        media.duration = duration;
        media.width = width;
        media.height = height;
        media.dateTaken = dateTaken;
        media.path = path;
        return media;
    }

//    public Uri getUri() {
//        return MediaManager.getContentUri(id, mimeType);
//    }

    ///////////////////////////////////////////////////////////////////////////
    // setter\getter
    ///////////////////////////////////////////////////////////////////////////

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getBucketId() {
        return bucketId;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getChoseNum() {
        return choseNum;
    }

    public void setChoseNum(int choseNum) {
        this.choseNum = choseNum;
    }

    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", mimeType='" + mimeType + '\'' +
                ", bucketId=" + bucketId +
                ", bucketName='" + bucketName + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                ", width=" + width +
                ", height=" + height +
                ", dateTaken=" + dateTaken +
                ", path='" + path + '\'' +
                ", picKey='" + picKey + '\'' +
                ", choseNum=" + choseNum +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FMedia media = (FMedia) o;
        return id == media.id;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{id, path});
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.mimeType);
        dest.writeLong(this.bucketId);
        dest.writeString(this.bucketName);
        dest.writeLong(this.size);
        dest.writeLong(this.duration);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeLong(this.dateTaken);
        dest.writeString(this.picKey);
    }

    public FMedia() {
    }

    protected FMedia(Parcel in) {
        this.id = in.readLong();
        this.mimeType = in.readString();
        this.bucketId = in.readLong();
        this.bucketName = in.readString();
        this.size = in.readLong();
        this.duration = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.dateTaken = in.readLong();
        this.picKey = in.readString();
    }

    public static final Creator<FMedia> CREATOR = new Creator<FMedia>() {
        @Override
        public FMedia createFromParcel(Parcel source) {
            return new FMedia(source);
        }

        @Override
        public FMedia[] newArray(int size) {
            return new FMedia[size];
        }
    };

    @NonNull
    @Override
    public FMedia clone() {
        FMedia media = null;
        try {
            media = (FMedia) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if (media == null) {
            media = new FMedia();
        }
        media.id = id;
        media.mimeType = mimeType;
        media.bucketId = bucketId;
        media.bucketName = bucketName;
        media.size = size;
        media.duration = duration;
        media.width = width;
        media.height = height;
        media.dateTaken = dateTaken;
        media.picKey = picKey;
        return media;
    }
}
