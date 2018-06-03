package leadfair.observer.test;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xlpan on 2017/9/1.
 */

public class xxx implements Parcelable{
    String i;
    int y;

    protected xxx(Parcel in) {
        i = in.readString();
        y = in.readInt();
    }

    public static final Creator<xxx> CREATOR = new Creator<xxx>() {
        @Override
        public xxx createFromParcel(Parcel in) {
            return new xxx(in);
        }

        @Override
        public xxx[] newArray(int size) {
            return new xxx[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(i);
        dest.writeInt(y);
    }
}
