package robot;

import model.DataCallback;
import model.HttpProxy;
import model.Task;
import util.Log;
import util.ThreadPool;
import util.Util;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class Robot {

    interface Listener {
        void onProcess(QuestionTypeEnum type,Object data);
    }
    private static final ThreadLocal<Object> threadLocal = new ThreadLocal();
    private ConcurrentHashMap<Integer,String> resultMap;
    private AtomicInteger couter = new AtomicInteger(0);
    private Listener listener;


    /********************************************************************************************************************************/
    private static class LazyHolder {
        private static final Robot INSTANCE = new Robot();
    }
    private Robot (){
        resultMap = new ConcurrentHashMap<>();
    }
    public static final Robot getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void searchAnswer(List<String> wordList, DataCallback callback){
        QuestionTypeEnum type = getType(wordList.get(0));
        searchByType(type,wordList,callback);
    }

    private void searchByType(QuestionTypeEnum type,List<String> wordList, DataCallback callback) {
        ThreadPool.getInstance().getPool().execute(new Task() {
            @Override
            public void run() {
                try {
                    threadLocal.set(new Object[]{type,wordList,callback});
                    HttpProxy.searchAnswer(index, wordList.get(0), new DataCallback<String>() {
                        @Override
                        public void onCallback(boolean bResult, String o, Object tagData) {
                            parseData(o);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    if(callback!=null){
                        callback.onCallback(false,e.toString(),"");
                    }
                }
            }
        });
    }

    private void parseData(String o) {
        Object[] objects = (Object[]) threadLocal.get();
        QuestionTypeEnum type = (QuestionTypeEnum) objects[0];
        List<String> wordList = (List<String>) objects[1];
        DataCallback callback = (DataCallback) objects[2];

        switch (type){
            case QUESTION_TYPE_NEXT_ONE:
            {
                String summary = BaiduHtmlParser.getSummary(o);
                for(int i=1;i<wordList.size();++i){
                    if(wordList.get(i).contains(summary)){
                        callback.onCallback(true,i,"");
                        Log.e("摘要匹配成功！");
                        return;
                    }
                }
                break;
            }
            case QUESTION_TYPE_WHEN:
            {
                try {
                    String whenKeyword = BaiduHtmlParser.getWhenKeyword(o);
                    for(int i=1;i<wordList.size();++i){
                        if(wordList.get(i).contains(whenKeyword)){
                            callback.onCallback(true,i,"");
                            Log.e("时期keyword匹配成功！");
                            return;
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    callback.onCallback(false,e.toString(),"");
                }
                break;
            }
            case QUESTION_TYPE_WHERE:

                break;
            case QUESTION_TYPE_MATH:
                break;
            case QUESTION_TYPE_OTHER:
                handleOther(wordList,callback);
                break;
            default:
                break;
        }

//        //首先匹配摘要和keyword
//        String summary = BaiduHtmlParser.getSummary(o);
//        String keyword = BaiduHtmlParser.getKeyword(o);
//        for(int i=1;i<wordList.size();++i){
//            if(wordList.get(i).contains(summary)){
//                /*有选项匹配了摘要，直接返回*/
//                callback.onCallback(true,i,"");
//                Log.e("摘要匹配成功！");
//                return;
//            }
//            if(wordList.get(i).contains(keyword)){
//                /*有选项匹配了keyword，直接返回*/
//                callback.onCallback(true,i,"");
//                Log.e("keyword匹配成功！");
//                return;
//            }
//        }
//        //使用通用方法处理
//        handleOther(wordList,callback);
    }

    private void handleOther(List<String> wordList, DataCallback callback) {
        final String q = wordList.get(0);
        for(int i=1;i<wordList.size();++i){
            ThreadPool.getInstance().getPool().execute(new Task(i) {
                @Override
                public void run() {
                    try {
                        threadLocal.set(new Object[]{wordList,callback});
                        HttpProxy.searchAnswer(getIndex(),q+" "+wordList.get(getIndex()).substring(2),searchAnswerCb);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if(callback!=null){
                            callback.onCallback(false,e.toString(),"");
                        }
                    }
                }
            });
        }
    }
    DataCallback searchAnswerCb = new DataCallback<String>() {
        @Override
        public void onCallback(boolean bResult, String o, Object tagData) {
            int index = (int) tagData;
            String num = BaiduHtmlParser.getNum(o);
            Object[] objects = (Object[]) threadLocal.get();
            List<String> wordList = (List<String>) objects[0];
            DataCallback callback = (DataCallback) objects[1];

            resultMap.put(index,num);
            int os = wordList.size()-1;
            if(couter.addAndGet(1) == os){
                int rightIndex = doAnalysis(wordList);
                if(callback!=null){
                    callback.onCallback(true,rightIndex,"");
                }
            }
        }
    };

    private int doAnalysis(List<String> wordList) {
        boolean isNormal = true;
        String question = wordList.get(0);
        if(question.contains("不是")
                ||question.contains("不能")
                ){
            isNormal = false;
        }
        String tmp = "";
        int targetIndex = 0;
        if(isNormal){
            for (int key : resultMap.keySet()) {
                String value = resultMap.get(key);
                if (Util.moreThan(value, tmp)) {
                    tmp = value;
                    targetIndex = key;
                }
            }
        }else{
            tmp = resultMap.get(1);
            targetIndex = 1;
            for (int key : resultMap.keySet()) {
                String value = resultMap.get(key);
                if (Util.lessThan(value, tmp)) {
                    tmp = value;
                    targetIndex = key;
                }
            }
        }
        return targetIndex;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }


    private QuestionTypeEnum getType(String q) {
        if(q.endsWith("是")
                ||q.endsWith("是什么")
                ){
            return QuestionTypeEnum.QUESTION_TYPE_IS;
        }else if(q.contains("哪个时期")
                ||q.contains("什么时期")
                ||q.contains("哪个时候")
                ||q.contains("什么时候")
                ||q.contains("哪个朝代")
                ||q.contains("什么朝代")
                ){
            return QuestionTypeEnum.QUESTION_TYPE_WHEN;
        }else if(q.contains("哪个国家")
                ||q.contains("什么国家")
                ||q.contains("哪个地区")
                ||q.contains("什么地区")
                ){
            return QuestionTypeEnum.QUESTION_TYPE_WHERE;
        }else if(q.contains("log")
                ){
            return QuestionTypeEnum.QUESTION_TYPE_MATH;
        }else{
            return QuestionTypeEnum.QUESTION_TYPE_OTHER;
        }
    }

}