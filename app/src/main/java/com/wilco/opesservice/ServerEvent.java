package com.wilco.opesservice;

/**
 * Created by Vijay on 20.07.2017
 */

public class ServerEvent     {

    private QuesAnsModel.DataResult quesAnsModel;

    public ServerEvent(QuesAnsModel.DataResult quesAnsModel) {
        this.quesAnsModel = quesAnsModel;
    }

    public QuesAnsModel.DataResult getQuesAnsModel() {
        return quesAnsModel;
    }

    public void setQuesAnsModel(QuesAnsModel.DataResult quesAnsModel) {
        this.quesAnsModel = quesAnsModel;
    }
}
