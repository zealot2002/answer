package robot;

public enum QuestionTypeEnum {
    QUESTION_TYPE_OTHER(0),
    QUESTION_TYPE_IS(1),
    QUESTION_TYPE_WHEN(2),

    QUESTION_TYPE_WHERE(3),
    QUESTION_TYPE_MATH(4),

    QUESTION_TYPE_NEXT_ONE(5);

    private int value;
    QuestionTypeEnum(int value) {
        this.value = value;
    }
    public int value(){return value;}
    public String showingString(){
        switch (value){
//            case 1: return "是什么";
//            case 2: return "在什么时候";
//            case 3: return "在哪里";
//            case 4: return "数学问题";
//            case 5: return "其他";
            default:return "未知类型";
        }
    }
}
