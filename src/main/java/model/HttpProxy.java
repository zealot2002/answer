package model;


import constant.HttpConstants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;

public class HttpProxy {
    public static void searchAnswer(final int tag,String keyWord,DataCallback callback)throws Exception{
//        String result = BaiduCrawler.craw(keyWord);
        String urlNameString = HttpConstants.SEARCH_URL_START +
                URLEncoder.encode(keyWord,"utf-8")
                 +HttpConstants.SEARCH_URL_END;
        String result="";
        try {
            HttpGet request = new HttpGet(urlNameString);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result= EntityUtils.toString(response.getEntity(),"utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(callback!=null){
            callback.onCallback(true,result,tag);
        }
    }

    public static void searchAnswer(String keyWord,DataCallback callback)throws Exception{
//        String result = BaiduCrawler.craw(keyWord);
        String urlNameString = HttpConstants.SEARCH_URL_START +
                URLEncoder.encode(keyWord,"utf-8")
                +HttpConstants.SEARCH_URL_END;
        String result="";
        try {
            HttpGet request = new HttpGet(urlNameString);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result= EntityUtils.toString(response.getEntity(),"utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(callback!=null){
            callback.onCallback(true,result,"");
        }
    }
}
