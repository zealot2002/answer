package robot;

import model.DataCallback;
import util.Log;

import java.util.ArrayList;
import java.util.List;

public class Tester {
    public static void main(String[] args) {
//        testNextOne();
        testWhen();
    }

    private static void testWhen() {
        List<String> wordList = new ArrayList<>();
        wordList.add("荆轲刺秦发生在哪个朝代");
        wordList.add("唐朝");
        wordList.add("战国时期");
        wordList.add("宋朝");

        Robot.getInstance().searchAnswer(wordList, new DataCallback() {
            @Override
            public void onCallback(boolean bResult, Object o, Object tagData) {
                int answerIndex = (int) o;
                Log.e("answerIndex: "+answerIndex);
            }
        });
    }


    private static void testNextOne(){
        List<String> wordList = new ArrayList<>();
        wordList.add("春风又绿江南岸下一句是");
        wordList.add("看了电视剧阿卡丽富家大室卡拉胶");
        wordList.add("明月何时照我还");
        wordList.add("范德萨广大割发代首");

        Robot.getInstance().searchAnswer(wordList, new DataCallback() {
            @Override
            public void onCallback(boolean bResult, Object o, Object tagData) {
                int answerIndex = (int) o;
                Log.e("answerIndex: "+answerIndex);
            }
        });
    }
}
