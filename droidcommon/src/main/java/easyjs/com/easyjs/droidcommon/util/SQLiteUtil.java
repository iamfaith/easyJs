package easyjs.com.easyjs.droidcommon.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by faith on 2018/1/20.
 */

public class SQLiteUtil {

    private SQLiteDatabase db;

    public static SQLiteUtil openDataBase(String path) {
        return new SQLiteUtil(path);
    }

    private SQLiteUtil(String path) {
        db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void close() {
        if (db != null)
            db.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Map<String, String> query(String sql, String[] selectionArgs, String[] qryFields) {
        Map<String, String> result = new HashMap<>();
        try (Cursor cursor = db.rawQuery(sql, selectionArgs)) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Optional<String[]> optional = Optional.ofNullable(qryFields);
                optional.ifPresent(fields -> {
                    for (String field : fields) {
                        String value = cursor.getString(cursor.getColumnIndex(field));
                        result.put(field, value);
                    }
                });
            }
        } catch (Exception error) {
            String err = error.getMessage();
            Log.e("SQLiteUtil", "query error" + err, error);
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void insert(String table, Map<String, String> keyVal) {
        ContentValues values = new ContentValues();

        // b. 向该对象中插入键值对
        keyVal.forEach((key, val) -> {
            values.put(key, val);
        });
        //注：ContentValues内部实现 = HashMap，区别在于：ContenValues Key只能是String类型，Value可存储基本类型数据 & String类型
        db.insert(table, null, values);
    }

}
