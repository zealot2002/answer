package util;

import constant.AnswerConstants;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.*;

public class Util {

    public static void screenShot(String savedFilePath) throws IOException {
        deleteFile(savedFilePath);
        String cmd = "adb shell /system/bin/screencap -p /sdcard/screenshot.png";
        cmdExec(cmd);
        cmd =  "adb pull /sdcard/screenshot.png "+ savedFilePath;
        cmdExec(cmd);
    }
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
    }
    public static void cmdExec(String cmd) throws IOException {
        Runtime run = Runtime.getRuntime();
        // run.exec("cmd /k shutdown -s -t 3600");
        Process process = run.exec("cmd /c" + cmd);
//        //虽然cmd命令可以直接输出，但是通过IO流技术可以保证对数据进行一个缓冲。
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String msg = null;
        while ((msg = br.readLine()) != null) {
//            Log.e(msg);
        }
    }
    public static void appendText(Text tv,String s){
        String old = tv.getText();
        if(old.isEmpty()){
            tv.setText(s);
        }else{
            tv.setText(old+"->"+s);
        }
    }
    public static void appendText(TextArea tv, String s){
        String old = tv.getText();
        tv.setText(old+s);
    }
    public static boolean moreThan(String s1, String s2)throws NullPointerException{
        if(s1==null||s2==null){
            throw new NullPointerException();
        }
        if(s1.length()==s2.length()){
            return s1.compareTo(s2)>0;
        }
        return s1.length()>s2.length();
    }
    public static boolean lessThan(String s1, String s2)throws NullPointerException{
        return !moreThan(s1,s2);
    }
}
