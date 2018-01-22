package presenter;

import constant.AnswerConstants;
import constant.AnswerConstants.AnswerStateEnum;
import static constant.AnswerConstants.AnswerStateEnum.*;

import contract.MainContract;
import model.DataCallback;
import model.HttpProxy;
import model.Task;
import ocr.OcrProxy;
import robot.Robot;
import util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class MainPresenter implements MainContract.Presenter {
    private final MainContract.View view;
    private List<String> wordList;
    private ConcurrentHashMap<Integer,String> resultMap;
    private AtomicInteger couter = new AtomicInteger(0);
    private long startTime;
/****************************************************************************************************/

    public MainPresenter(MainContract.View view) {
        this.view = view;
        wordList = new ArrayList<>();
        resultMap = new ConcurrentHashMap<>();
    }

    public void start() {
        fsm(ANSWER_STATE_START,"");
    }

    private void fsm(AnswerStateEnum state,Object data){
        try {
            Log.e("fsm:"+state.showingString());
            view.updateUI(state,data);
            switch (state){
                case ANSWER_STATE_START:
                    startTime = System.currentTimeMillis();
                    couter.set(0);
                    fsm(ANSWER_STATE_SCREEN_SHOT,"");
                    break;
                case ANSWER_STATE_SCREEN_SHOT:
                    ThreadPool.getInstance().getPool().execute(() -> {
                        try {
                            Util.screenShot(AnswerConstants.SCREEN_SHOT_FILE_PATH);
                        } catch (IOException e) {
                            e.printStackTrace();
                            fsm(ANSWER_STATE_ERROR,e.toString());
                        }
                        fsm(ANSWER_STATE_CUT_IMAGE,"");
                    });
                    break;
                case ANSWER_STATE_CUT_IMAGE:
                    ThreadPool.getInstance().getPool().execute(() -> {
                        Util.deleteFile(AnswerConstants.SCREEN_SHOT_Q_FILE_PATH);
                        new ImageUtil().cutImage(
                                AnswerConstants.SCREEN_SHOT_FILE_PATH,
                                AnswerConstants.SCREEN_SHOT_Q_FILE_PATH,
                                0,400,1280,820);
                        fsm(ANSWER_STATE_OCR,"");
                    });
                    break;
                case ANSWER_STATE_OCR:
                    ThreadPool.getInstance().getPool().execute(() -> {
                        doOcr();
                    });

                    break;
                case ANSWER_STATE_SEARCH:
                    searchAnswer();
                    break;

                case ANSWER_STATE_ANALYSIS:
//                    int answerIndex = doAnalysis();
//                    long dur = System.currentTimeMillis() - startTime;
//                    fsm(ANSWER_STATE_DONE,new String[]{wordList.get(answerIndex),"耗时："+dur+"ms"});
                    break;

                case ANSWER_STATE_DONE:
                    break;

                case ANSWER_STATE_ERROR:
                case ANSWER_STATE_CACEL:
                case ANSWER_STATE_UNKNOWN:
                default:
                    Log.e("err!");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            fsm(ANSWER_STATE_ERROR,e.toString());
        }
    }

    private void doOcr() {
        OcrProxy.doOcr(AnswerConstants.SCREEN_SHOT_Q_FILE_PATH, (DataCallback<List<String>>) (bResult, strings, tagData) -> {
            wordList = strings;
            if(wordList.isEmpty()){
                fsm(ANSWER_STATE_ERROR,"wordList为空");
            }else{
                fsm(ANSWER_STATE_SEARCH,wordList);
            }
        });
    }
    private void searchAnswer() {
        String q = wordList.get(0);
        Robot.getInstance().searchAnswer(wordList, new DataCallback() {
            @Override
            public void onCallback(boolean bResult, Object o, Object tagData) {
                int answerIndex = (int) o;
                long dur = System.currentTimeMillis() - startTime;
                fsm(ANSWER_STATE_DONE,new String[]{wordList.get(answerIndex),"耗时："+dur+"ms"});
            }
        });
//        Robot.getInstance().addInterupter();
        /*
        * 策略
        * q是问题；wordList的1-3是选项
        * 将"问题 选项"作为搜索keyword，正向情形下：条目最多的作为答案；反向情形：条目最少的作为答案
        * */
//        for(int i=1;i<wordList.size();++i){
//            ThreadPool.getInstance().getPool().execute(new Task(i) {
//                @Override
//                public void run() {
//                    try {
//                        HttpProxy.searchAnswer(index,q+" "+wordList.get(getIndex()).substring(2),searchAnswerCb);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        fsm(ANSWER_STATE_ERROR,e.toString());
//                    }
//                }
//            });
//        }
    }
//    DataCallback searchAnswerCb = new DataCallback<String>() {
//        @Override
//        public void onCallback(boolean bResult, String o, Object tagData) {
//            int index = (int) tagData;
//            String num = BaiduHtmlParser.getNum(o);
//            resultMap.put(index,num);
//            int os = wordList.size()-1;
//            if(couter.addAndGet(1) == os){
//                fsm(ANSWER_STATE_ANALYSIS,resultMap);
//            }
//        }
//    };
//    private int doAnalysis() {
//        boolean isNormal = true;
//        String question = wordList.get(0);
//        if(question.contains("不是")
//                ||question.contains("不能")
//                ){
//            isNormal = false;
//        }
//        String tmp = "";
//        int targetIndex = 0;
//        if(isNormal){
//            for (int key : resultMap.keySet()) {
//                String value = resultMap.get(key);
//                if (Util.moreThan(value, tmp)) {
//                    tmp = value;
//                    targetIndex = key;
//                }
//            }
//        }else{
//            tmp = resultMap.get(1);
//            targetIndex = 1;
//            for (int key : resultMap.keySet()) {
//                String value = resultMap.get(key);
//                if (Util.lessThan(value, tmp)) {
//                    tmp = value;
//                    targetIndex = key;
//                }
//            }
//        }
//        return targetIndex;
//    }

}
