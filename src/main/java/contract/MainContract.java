package contract;

import constant.AnswerConstants;

public interface MainContract {
    interface View{
        void updateUI(AnswerConstants.AnswerStateEnum state,Object data);
    }

    interface Presenter{
        void start();
    }
}
