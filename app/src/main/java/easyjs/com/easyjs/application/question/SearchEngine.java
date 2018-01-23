package easyjs.com.easyjs.application.question;


import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import easyjs.com.common.HttpUtil;
import easyjs.com.easyjs.application.model.SearchResult;

/**
 * Created by faith on 2018/1/23.
 */

public class SearchEngine {


    private static final String url = "http://www.baidu.com/s";


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String baiduSearch(String question, List<String> choice) {
        List<SearchResult> list = new LinkedList<>();
        for (String s : choice) {
            try {
                Map<String, String> map = new HashMap<>();
                map.put("wd", question + s);
                String ret = HttpUtil.getInstance().get(url, map);
                int pos = ret.indexOf("百度为您找到相关结果约") + 11;
                ret = ret.substring(pos);
                pos = ret.indexOf("个");
                ret = ret.substring(0, pos);
                ret = ret.replaceAll(",", "");

                SearchResult result = new SearchResult();
                result.choice = s;
                result.weight = Integer.parseInt(ret);
                list.add(result);
            } catch (Exception e) {

            }
        }
        String searchRet = "";
        if (list.size() > 0) {
            list.sort((a, b) -> a.weight - b.weight);
            searchRet = list.get(0) + " " + list.get(list.size() - 1);
        }
//        System.out.println(list + "--" + searchRet);
        return searchRet;
    }

}
