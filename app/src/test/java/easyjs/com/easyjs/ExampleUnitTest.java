package easyjs.com.easyjs;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import easyjs.com.easyjs.application.model.Question;
import easyjs.com.easyjs.application.question.SearchEngine;

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

    @Test
    public void testSearch() {
        List<String> list = new ArrayList<>();
        list.add("甲醛");
        list.add("甲醇");
        list.add("苯");
        SearchEngine.baiduSearch("新装修的房子通常哪种化学物质含量会比较高", list);
    }


    @Test
    public void testSqlite() {
        File file = new File("/Users/faith/AndroidStudioProjects/easyJs/app/src/main/assets/data/data.txt");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:/Users/faith/AndroidStudioProjects/easyJs/app/src/main/assets/data/data.db")) {

            try (FileInputStream inputStream = new FileInputStream(file)) {
                String content = IOUtils.toString(inputStream);
                // do something with everything string
                if (!StringUtils.isEmpty(content)) {

                    for (String line : content.split("@")) {
                        String[] quiz = line.split("\\$");
                        String question = quiz[0];
                        String ans = quiz[1];
                        ans = JSON.parseObject(ans).getString("a");
                        Boolean isNeedInsert = true;
                        try (PreparedStatement preparedStatement = connection.prepareStatement("select answer from questions where quiz like ?")) {
                            preparedStatement.setString(1, question);
                            ResultSet resultSet = preparedStatement.executeQuery();
                            while (resultSet.next()) {
                                isNeedInsert = false;
                                String answer = resultSet.getString("answer");
//                                if (!ans.equals(answer))
//                                    System.out.println(question + "--" + answer + " --" + ans);
                            }
                            resultSet.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (isNeedInsert) {
                            try (PreparedStatement insert = connection.prepareStatement("insert into questions(quiz, answer, school, type, options) values(?,?, '1', '2', '3')")) {
                                insert.setString(1, question);
                                insert.setString(2, ans);
                                System.out.println(insert.executeUpdate() + " " + question + " --" + ans);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}