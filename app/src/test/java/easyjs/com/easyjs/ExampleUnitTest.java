package easyjs.com.easyjs;

import com.alibaba.fastjson.JSON;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import easyjs.com.easyjs.application.model.Question;

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
    public void testMap() {
        Question question = new Question();
        question.setAnswer("1");
        List<String> list = new ArrayList<>();
        list.add("asdasd");
        list.add("123123");
        question.setOptions(list);
        Map<String, String> map = question.getMap();
        System.out.println(map);
    }

    @Test
    public void testJson() {
        String json = "{\"data\":{\"quiz\":\"下列选项中哪个城市被称为「雾都」？\",\"options\":[\"柏林\",\"伦敦\",\"巴拉圭\",\"巴黎\"],\"num\":1,\"school\":\"生活\",\"type\":\"世界\",\"contributor\":\"张映涛\",\"endTime\":1516436234,\"curTime\":1516436219},\"errcode\":0}";
        String data = JSON.parseObject(json).getString("data");
        Question question = JSON.parseObject(data, Question.class);
        System.out.println(data);
        System.out.println(question);
        System.out.println(question.getOptions());
//        JSON.parseArray()
//        data.get("options")
    }
}