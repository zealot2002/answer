package ocr;

import com.baidu.aip.ocr.AipOcr;
import model.DataCallback;
import org.json.JSONArray;
import org.json.JSONObject;
import util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ocr.OcrConstants.*;

public class OcrProxy {
    public static void doOcr(String imagePath, DataCallback callback){
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 调用接口
        JSONObject res = client.basicGeneral(imagePath, new HashMap<String, String>());
        parseData(res,callback);
    }

    private static void parseData(JSONObject res, DataCallback callback) {
        Log.e("parseData: res:"+res.toString());
        List<String> wordList = new ArrayList<>();
        JSONArray wordArray = res.getJSONArray("words_result");
        for(int i=0;i<wordArray.length();i++){
            JSONObject object = wordArray.getJSONObject(i);
            String s = object.getString("words");

            if(i>0){
                StringBuilder sb = new StringBuilder(wordList.get(0));
                if(!sb.toString().endsWith("?")) {
                    sb.append(s);
                    wordList.set(0, sb.toString());
                }else{
                    wordList.add(s);
                }
            }else{
                wordList.add(s);
            }
        }
        if(callback!=null){
            callback.onCallback(true,wordList,null);
        }
    }
}
