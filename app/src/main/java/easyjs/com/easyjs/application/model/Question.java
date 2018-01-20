package easyjs.com.easyjs.application.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by faith on 2018/1/20.
 */

public class Question {

    public boolean isFound = false;
    public int num;

    protected String quiz;
    protected String school;
    protected String type;
    protected String options;
    protected String answer;

    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                if (field.get(this) instanceof String)
                    map.put(field.getName(), (String) field.get(this));
            } catch (IllegalAccessException e) {
            }
        }
        return map;
    }

    public String getQuiz() {
        return quiz;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Question{" +
                "isFound=" + isFound +
                ", num=" + num +
                ", quiz='" + quiz + '\'' +
                ", school='" + school + '\'' +
                ", type='" + type + '\'' +
                ", options='" + options + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
