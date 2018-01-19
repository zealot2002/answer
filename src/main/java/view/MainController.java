package view;


import constant.AnswerConstants;
import contract.MainContract;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import presenter.MainPresenter;
import util.ImageUtil;
import util.Log;
import util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/*
 * prepsat bez pouziti fxml-ka, nevim jak na to
 */
public class MainController implements MainContract.View{
    @FXML
    Button btnStart;
    @FXML
    ImageView ivScreenShot,ivQShot;
    @FXML
    Text tvState,tvTime;
    @FXML
    TextArea taQuestion,taAnswer;
    @FXML
    ListView listView;

    private MainPresenter presenter;
    private List<String> wordList;
    private Map<Integer,String> resultMap;
/****************************************************************************************************/
    @FXML
    public void initialize() {
        if(btnStart == null){
            return;
        }

        presenter = new MainPresenter(this);
        btnStart.setStyle("-fx-background-color: green");
        btnStart.setOnAction(event -> {
            clearAll();
            btnStart.setStyle("-fx-background-color: red");
        });
    }

    private void clearAll() {
        if(wordList!=null){
            wordList.clear();
        }
        if(resultMap!=null){
            resultMap.clear();
        }
        Platform.runLater(() -> {
            ivScreenShot.setImage(null);
            ivQShot.setImage(null);
            tvState.setText("");
            tvTime.setText("");
            taQuestion.setText("");
            taAnswer.setText("");
            listView.setItems(FXCollections.observableArrayList());

            presenter.start();
        });
    }

    @Override
    public void updateUI(AnswerConstants.AnswerStateEnum state, Object data) {
        Util.appendText(tvState,state.showingString());
        switch (state){
            case ANSWER_STATE_CUT_IMAGE:
            {
                Platform.runLater(() -> {
                    String localUrl = ImageUtil.getUri(AnswerConstants.SCREEN_SHOT_FILE_PATH);
                    Image image = new Image(localUrl, false);
                    ivScreenShot.setImage(image);
                });
            }
                break;
            case ANSWER_STATE_OCR:
            {
                Platform.runLater(() -> {
                    String localUrl = ImageUtil.getUri(AnswerConstants.SCREEN_SHOT_Q_FILE_PATH);
                    Image image = new Image(localUrl, false);
                    ivQShot.setImage(image);
                });
            }
                break;
            case ANSWER_STATE_SEARCH:
                Platform.runLater(() -> {
                    wordList = (List<String>) data;
                    taQuestion.setText(wordList.get(0));
                    for(int i=1;i<wordList.size();i++){
                        Util.appendText(taQuestion,"\n\r"+wordList.get(i));
                    }
                });
                break;

            case ANSWER_STATE_ANALYSIS:
                Platform.runLater(() -> {
                    //更新JavaFX的主线程的代码放在此处
                    updateList(data);
                });
                break;

            case ANSWER_STATE_DONE:
                Platform.runLater(() -> {
                    String[] ss = (String[]) data;
                    taAnswer.setText(ss[0]);
                    tvTime.setText(ss[1]);
                    btnStart.setStyle("-fx-background-color: green");
                });
                break;
            case ANSWER_STATE_CACEL:
            case ANSWER_STATE_UNKNOWN:
            default:
                Log.e("err!");
                break;
        }
    }

    private void updateList(Object data) {
        resultMap = (Map<Integer, String>) data;
        ArrayList<String> dataList = new ArrayList<>();
        for(int i=1;i<wordList.size();i++){
            dataList.add(wordList.get(i)+"  "+resultMap.get(i));
        }
        listView.setItems(fillList(dataList));
        listView.setCellFactory(TextFieldListCell.forListView());
        listView.setEditable(false);
    }
    private ObservableList<String> fillList(ArrayList<String> dataList) {
        ObservableList<String> list = FXCollections.observableArrayList();
        for (String s : dataList) {
            list.add(s);
        }
        return list;
    }
}
