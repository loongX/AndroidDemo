package leadfair.observer;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by TZT on 2017/8/21.
 */

public class PushMessage<T> extends Message<T> implements Parcelable, Cloneable {

    private int role;
    private String payload;
    private boolean isPull;

    public boolean isPull() {
        return isPull;
    }

    public void setPull(boolean pull) {
        isPull = pull;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final PushMessage<T> clone() throws CloneNotSupportedException {
        try {
            return (PushMessage<T>) super.clone();
        } catch (Exception e) {
            PushMessage<T> pushMessage = new PushMessage<>();
            pushMessage.isPull = isPull;
            pushMessage.payload = payload;
            pushMessage.role = role;
            //浅拷贝
            pushMessage.data = data;
            return pushMessage;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public final void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.role);
        dest.writeString(this.payload);
        dest.writeByte((byte) (this.isPull ? 1 : 0));
        if (data instanceof Parcelable) {
            dest.writeByte((byte) 1);
            dest.writeString(data.getClass().getName());
            dest.writeParcelable((Parcelable) this.data, flags);
        } else if (data instanceof Serializable) {
            dest.writeByte((byte) 2);
            dest.writeSerializable((Serializable) this.data);
        } else {
            throw new IllegalArgumentException("data 不能序列化");
        }
    }

    public PushMessage() {
    }

    @SuppressWarnings("unchecked")
    protected PushMessage(Parcel in) {
        this.role = in.readInt();
        this.payload = in.readString();
        this.isPull = in.readByte() == 1;
        try {
            byte b = in.readByte();
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

    public static final Creator<PushMessage> CREATOR = new Creator<PushMessage>() {
        @Override
        public PushMessage createFromParcel(Parcel source) {
            return new PushMessage(source);
        }

        @Override
        public PushMessage[] newArray(int size) {
            return new PushMessage[size];
        }
    };

}
