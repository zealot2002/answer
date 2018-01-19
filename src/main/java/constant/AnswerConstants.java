package constant;

public class AnswerConstants {
    public static final String SCREEN_SHOT_FILE_PATH = "D:\\projects\\java\\answer\\out\\production\\answer\\screenshot.png";
    public static final String SCREEN_SHOT_Q_FILE_PATH = "D:\\projects\\java\\answer\\out\\production\\answer\\q_screenshot.png";

    public enum AnswerStateEnum {
        ANSWER_STATE_START(1),
        ANSWER_STATE_SCREEN_SHOT(2),
        ANSWER_STATE_CUT_IMAGE(4),
        ANSWER_STATE_OCR(5),
        ANSWER_STATE_SEARCH(6),
        ANSWER_STATE_ANALYSIS(7),
        ANSWER_STATE_DONE(8),
        ANSWER_STATE_CACEL(9),
        ANSWER_STATE_ERROR(10),
        ANSWER_STATE_UNKNOWN(11);

        private int value;
        AnswerStateEnum(int value) {
            this.value = value;
        }
        public int value(){return value;}
        public String showingString(){
            switch (value){
                case 1: return "开始";
                case 2: return "截屏";
                case 3: return "保存截屏图片";
                case 4: return "切割";
                case 5: return "识别";
                case 6: return "搜索";
                case 7: return "分析";
                case 8: return "完成";
                case 9: return "取消";
                case 10: return "错误";
                case 11:
                default:return "未知状态";
            }
        }
    }
}
