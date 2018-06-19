package com.wilco.opesservice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapMyPlan extends AppCompatActivity implements View.OnClickListener {

    private List<QuesAnsModel.DataResult> data = new ArrayList<QuesAnsModel.DataResult>();
    public static Bus bus;

    private Communicator communicator;

    LinearLayout linearLayout;
    Button btnTag;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_my_plan);

        communicator = new Communicator();

        usePost();

        linearLayout = (LinearLayout) findViewById(R.id.baseLayout);
    }


    private void usePost() {
        communicator.loadJSON();
    }


    List<QuesAnsModel> quesAnsModelList = new ArrayList<>();

    @Subscribe
    public void onServerEvent(ServerEvent serverEvent) throws JSONException {
        //Toast.makeText(this, "" + serverEvent.getQuesAnsModel().getData(), Toast.LENGTH_SHORT).show();

        if (serverEvent.getQuesAnsModel().getStatus() && serverEvent.getQuesAnsModel().getCode() == 200)
            if (serverEvent.getQuesAnsModel().getData() != null) {
                quesAnsModelList = serverEvent.getQuesAnsModel().getData();
                QuesAnsModel quesAnsModel = new QuesAnsModel();
                quesAnsModel = quesAnsModelList.get(0);
                displayQuestions(quesAnsModel);
            }
    }

    private void displayQuestions(final QuesAnsModel quesAnsModel) {

//        TextView quesText = new TextView(this);
//        quesText.setText(quesAnsModel.getQuestion());
//        linearLayout.addView(quesText);


//        3 ? 2 : 1
        for (int i = 0; i < quesAnsModel.getId().size(); i++) {

            LinearLayout column = new LinearLayout(this);

            column.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            column.setOrientation(LinearLayout.VERTICAL);

            LinearLayout row = new LinearLayout(this);

            row.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView quesText = new TextView(this);
            quesText.setText(quesAnsModel.getQuestion());
            column.addView(quesText);

//                    (quesAnsModel.getOptions().size() > 3 ? 2 : 1)
            for (int j = 0; j < quesAnsModel.getOptions().size(); j++) {
                btnTag = new Button(this);
                btnTag.setId(Integer.parseInt(quesAnsModel.getOptionsId().get(j)));
                btnTag.setLayoutParams(new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT, ActionMenuView.LayoutParams.WRAP_CONTENT));
                btnTag.setOnClickListener(this);
                        /*if (i >= 1) {
                            btnTag.setText(quesAnsModel.getOptions().get(2 + j));
                            btnTag.setTag(quesAnsModel.getOptionsId().get(2 + j));
                        } else {*/
                btnTag.setText(quesAnsModel.getOptions().get(j));
                btnTag.setTag(quesAnsModel.getQuestion() + "," + quesAnsModel.getOptionsId().get(j));

                row.addView(btnTag);

                String str = String.valueOf(btnTag.getTag());
                List<String> stringList = Arrays.asList(str.split(","));
            }

            column.addView(row);
            linearLayout.addView(column);
            linearLayout.setTag("" + quesAnsModel.getId().get(0));
            Log.d("linearLayout", "Tag : " + linearLayout.getTag());
            Log.d("linearLayout", "child's : " + linearLayout.getChildCount());
        }
    }


    @Subscribe
    public void onErrorEvent(ErrorEvent errorEvent) {
        Toast.makeText(this, "" + errorEvent.getErrorMsg(), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onClick(View v) {

        try {
            String str = String.valueOf(v.getTag());
            List<String> stringList = Arrays.asList(str.split(","));

            v = ((Button) linearLayout.findViewById(Integer.parseInt(stringList.get(1))));

            Log.d("linearLayout", "click btnTag : " + v.getTag());
            Log.d("linearLayout", "v btnTag : " + v.getRootView().hashCode());

            for (int i = 0; i < quesAnsModelList.size(); i++) {
                QuesAnsModel model = quesAnsModelList.get(i);

                if (model.getId().get(0).matches(stringList.get(1))) {

                    int hCode = v.getParent().getParent().hashCode();
                    String s = "" + linearLayout.getTag().toString();

                    boolean flag = false;
                    for (int k = 0; k < linearLayout.getChildCount(); k++) {

                        if (flag) {
                            for (int v1 = k; v1 <= linearLayout.getChildCount(); v1++) {
                                linearLayout.removeViewAt(k);
                                linearLayout.requestLayout();
                            }
                            break;
                        }

                        if (hCode == linearLayout.getChildAt(k).hashCode()) {
                            flag = true;
                        }
                    }

                    displayQuestions(model);
                    break;
                }
            }

        } catch (Exception e) {
            Log.d("Exception", "e : " + e.getMessage());
        }
    }
}
