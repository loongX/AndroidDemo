package com.rdm.base.db.pool;

import android.content.ContentValues;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Created on 2015/9/6 by  .
 * 之前bug导致key明明存在还被多次插入，废弃之前数据吧
 */
public class PoolTableV3 extends PoolTableV2 {

    public static ContentValues contentValues4Expire() {
        ContentValues values = new ContentValues();
        values.put(LAST_MODIFY, 0);
        return values;
    }

    public static ContentValues contentValues(String k, Serializable v, int priority) throws IOException {
        ContentValues values = new ContentValues();
        values.put(KEY, k);

        if (v != null) {
            values.put(NAME, v.getClass().getName());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(v);
            byte[] bytes = bos.toByteArray();
            values.put(RAW_DATA, bytes);
            values.put(RAW_DATA_HASH, Arrays.hashCode(bytes));
            values.put(SIZE, bytes.length);
            values.put(EXT_3, priority);
        } else {
            values.put(RAW_DATA_HASH, Integer.MIN_VALUE);
            values.put(SIZE, 0);
        }
        values.put(LAST_MODIFY, System.currentTimeMillis());
        return values;
    }

}
