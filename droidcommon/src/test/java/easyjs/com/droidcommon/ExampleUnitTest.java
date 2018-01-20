package easyjs.com.droidcommon;

import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import easyjs.com.easyjs.droidcommon.util.SQLiteUtil;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_dbquery() {
        String path = this.getClass().getClassLoader().getResource("data.db").getPath();
        SQLiteUtil instance = SQLiteUtil.openDataBase(path);
        String sql = "select * from questions where school = ? and type = ? and quiz = ?";
        Map<String, String> result = instance.query(sql, new String[] {"生活", "健康", "「瑜伽」运动起源于哪一地区？"}, new String[] {"options", "answer"});
        Log.d("test", result.toString());
    }
}