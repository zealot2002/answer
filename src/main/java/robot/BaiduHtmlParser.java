package robot;

import util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class BaiduHtmlParser {
    /*
    * 返回搜索条目数量
    * */
    public static String getNum(String s){
        String KEY_HEAD = "百度为您找到相关结果约";
        String KEY_TAIL = "个</div></div></div>";
        int head = s.indexOf(KEY_HEAD);
        int tail = s.indexOf(KEY_TAIL);
        return s.substring(head+KEY_HEAD.length(),tail);
    }
    /*
    * 返回摘要
    * */
    public static String getSummary(String s){
        String KEY_1 = "<div class=\"op_exactqa_detail_s_answer\">";
        String KEY_2 = "target=\"_blank\">";
        String KEY_3 = "</a></span>";
        int cur = s.indexOf(KEY_1);
        s=s.substring(cur);
        cur = s.indexOf(KEY_2);
        s=s.substring(cur+KEY_2.length());
        cur = s.indexOf(KEY_3);
        s = s.substring(0,cur);
        Log.e("getSummary: "+s);
        return s;
    }
    /*
    * 返回时期keyword
    * */
    public static String getWhenKeyword(String s) throws UnsupportedEncodingException {

        String KEY_0 = "<div id=\"content_left\">";

        String KEY_1 = "时期";
        String KEY_2 = "时代";
        String KEY_3 = "年间";

        //找到content_left div，并截取content
        int cur = s.indexOf(KEY_0);
        s = s.substring(cur);

        //只要2个汉字
        int len = 6;
        cur = s.indexOf(KEY_1);
        if(cur!=-1){
            return trimStr(s.substring(cur-len,cur));
        }
        cur = s.indexOf(KEY_2);
        if(cur!=-1){
            return trimStr(s.substring(cur-len,cur));
        }
        cur = s.indexOf(KEY_3);
        if(cur!=-1){
            return trimStr(s.substring(cur-len,cur));
        }
        return s;
    }

    public static String trimStr(String s){
        if(s.endsWith("<em>")){
            return s.substring(0,2);
        }else{
            return s.substring(2);
        }
    }
    /*
    * 返回关键词
    * */
    public static String getKeyword(String s){
        String KEY_HEAD = "百度为您找到相关结果约";
        String KEY_TAIL = "个</div></div></div>";
        int head = s.indexOf(KEY_HEAD);
        int tail = s.indexOf(KEY_TAIL);
        return s.substring(head+KEY_HEAD.length(),tail);
    }
}
