package easyjs.com.easyjs.application.model;

/**
 * Created by faith on 2018/1/23.
 */

public class SearchResult {

    public String choice;
    public int weight;

    @Override
    public String toString() {
        return choice + "[" + weight + "]";
    }
}
