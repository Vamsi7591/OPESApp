package com.wilco.opesservice;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vijay on 20.07.2017
 */

public class QuesAnsModel implements Serializable{
    @SerializedName("type")
    private String type;

    @SerializedName("question")
    private String question;

    @SerializedName("options")
    private ArrayList<String> options = new ArrayList<>();

    @SerializedName("options_id")
    private ArrayList<String> options_id = new ArrayList<>();

    @SerializedName("id")
    private List<String> id = null;

    public QuesAnsModel(String type, String question, ArrayList<String> options, ArrayList<String> options_id,
                        List<String> id) {
        this.type = type;
        this.question = question;
        this.options = options;
        this.options_id = options_id;
        this.id = id;

    }

    public QuesAnsModel() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public List<String> getOptionsId() {
        return options_id;
    }

    public void setOptionsId(ArrayList<String> optionsId) {
        this.options_id = optionsId;
    }

    public List<String> getId() {
        return id;
    }

    public void setId(List<String> id) {
        this.id = id;
    }


    public static class DataResult {

        @SerializedName("status")
        private Boolean status;

        @SerializedName("code")
        private Integer code;

        @SerializedName("data")
        private List<QuesAnsModel> data = new ArrayList<>();

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public List<QuesAnsModel> getData() {
            return data;
        }

        public void setData(List<QuesAnsModel> data) {
            this.data = data;
        }


    }

}
