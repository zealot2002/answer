package util;

public class HtmlParser {
    public static String getNum(String s){
        String KEY_HEAD = "百度为您找到相关结果约";
        String KEY_TAIL = "个</div></div></div>";
        int head = s.indexOf(KEY_HEAD);
        int tail = s.indexOf(KEY_TAIL);
        return s.substring(head+KEY_HEAD.length(),tail);
    }
}
