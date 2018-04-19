package leadfair.observer;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by TZT on 2017/8/21.
 */

public class PullMessage<T> extends Message<T> implements Parcelable {
    private String groupName;
    private String observerUniqueId;
    private boolean isPull = false;

    public boolean isPull() {
        return isPull;
    }

    public void setPull(boolean pull) {
        isPull = pull;
    }

    public PullMessage(String observerUniqueId) {
        this.observerUniqueId = observerUniqueId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getObserverUniqueId() {
        return observerUniqueId;
    }

    public void setObserverUniqueId(String observerUniqueId) {
        this.observerUniqueId = observerUniqueId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public final void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.groupName);
        dest.writeString(this.observerUniqueId);
        dest.writeByte((byte) (this.isPull ? 1 : 0));
        if (null == data) {
            dest.writeByte((byte) 0);
            return;
        }
        if (data instanceof Parcelable) {
            dest.writeByte((byte) 1);
            dest.writeString(data.getClass().getName());
            dest.writeParcelable((Parcelable) this.data, flags);
        } else if (data instanceof Serializable) {
            dest.writeByte((byte) 2);
            dest.writeSerializable((Serializable) this.data);
        } else {
            throw new IllegalArgumentException("data 不能序列化 :" + data.getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    protected PullMessage(Parcel in) {
        this.groupName = in.readString();
        this.observerUniqueId = in.readString();
        this.isPull = in.readByte() == 1;
        byte b = in.readByte();
        if (b == 0) return;
        try {
            if (b == 1) {
                String dataName = in.readString();
                this.data = in.readParcelable(Class.forName(dataName).getClassLoader());
            } else if (b == 2) {
                this.data = (T) in.readSerializable();
            } else {
                throw new IllegalArgumentException("data 不能反序列化");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<PullMessage> CREATOR = new Creator<PullMessage>() {
        @Override
        public PullMessage createFromParcel(Parcel source) {
            return new PullMessage(source);
        }

        @Override
        public PullMessage[] newArray(int size) {
            return new PullMessage[size];
        }
    };
}
