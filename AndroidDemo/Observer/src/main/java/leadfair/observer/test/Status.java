package leadfair.observer.test;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TZT on 2017/8/21.
 * 序列化状态
 */

public class Status implements Parcelable {

    private int code;
    private String message;

    public Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Status{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.message);
    }

    public Status() {
    }

    protected Status(Parcel in) {
        this.code = in.readInt();
        this.message = in.readString();
    }

    public static final Parcelable.Creator<Status> CREATOR = new Parcelable.Creator<Status>() {
        @Override
        public Status createFromParcel(Parcel source) {
            return new Status(source);
        }

        @Override
        public Status[] newArray(int size) {
            return new Status[size];
        }
    };
}
