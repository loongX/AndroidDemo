package leadfair.observer;

import android.os.Parcelable;

/**
 * Created by TZT on 2017/8/21.
 */

public interface Puller<T> extends Parcelable{
   <S extends T> S pull();
}
