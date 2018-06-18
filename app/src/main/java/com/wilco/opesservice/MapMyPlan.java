package com.wilco.opesservice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.List;

public class MapMyPlan extends AppCompatActivity {

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


    @Subscribe
    public void onServerEvent(ServerEvent serverEvent) throws JSONException {
        Toast.makeText(this, "" + serverEvent.getQuesAnsModel().getData(), Toast.LENGTH_SHORT).show();

        displayQuestions(serverEvent);
    }

    private void displayQuestions(final ServerEvent serverEvent) {

        if (serverEvent.getQuesAnsModel().getStatus() && serverEvent.getQuesAnsModel().getCode() == 200)
            if (serverEvent.getQuesAnsModel().getData() != null) {
                QuesAnsModel quesAnsModel = new QuesAnsModel();
                quesAnsModel = serverEvent.getQuesAnsModel().getData().get(0);

                TextView quesText = new TextView(this);
                quesText.setText(quesAnsModel.getQuestion());
                linearLayout.addView(quesText);

                for (int i = 0; i < (quesAnsModel.getOptions().size() > 3 ? 2 : 1); i++) {

                    LinearLayout row = new LinearLayout(this);

                    row.setLayoutParams(new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    row.setOrientation(LinearLayout.HORIZONTAL);

//                    (quesAnsModel.getOptions().size() > 3 ? 2 : 1)
                    for (int j = 0; j < quesAnsModel.getOptions().size(); j++) {
                        btnTag = new Button(this);
                        btnTag.setLayoutParams(new LinearLayout.LayoutParams
                                (LinearLayout.LayoutParams.WRAP_CONTENT, ActionMenuView.LayoutParams.WRAP_CONTENT));

                        if (i >= 1) {
                            btnTag.setText(quesAnsModel.getOptions().get(2 + j));
                            btnTag.setTag(quesAnsModel.getOptionsId().get(2 + j));
                        } else {
                            btnTag.setText(quesAnsModel.getOptions().get(j));
                            btnTag.setTag(quesAnsModel.getOptionsId().get(j));

                            row.addView(btnTag);

                        }

                        btnTag.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(getApplicationContext(), "Selected pos : " + v.getTag().toString(), Toast.LENGTH_SHORT).show();

                                if (v.getTag().equals("1") && (serverEvent.getQuesAnsModel().getData().get(1)
                                        != null)) {
                                    QuesAnsModel quesAnsModel1 = new QuesAnsModel();
                                    quesAnsModel1 = serverEvent.getQuesAnsModel().getData().get(1);
                                    TextView quesText1 = new TextView(getApplicationContext());
                                    quesText1.setText(quesAnsModel1.getQuestion());

                                    linearLayout.addView(quesText1);

                                    for (int i = 0; i < (quesAnsModel1.getOptions().size() > 3 ? 2 : 1); i++) {

                                        LinearLayout row = new LinearLayout(MapMyPlan.this);
                                        row.setLayoutParams(new LinearLayout.LayoutParams
                                                (LinearLayout.LayoutParams.MATCH_PARENT,
                                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                        row.setOrientation(LinearLayout.HORIZONTAL);

//                            (quesAnsModel.getOptions().size() > 3 ? 2 : 1)
                                        for (int j = 0; j < quesAnsModel1.getOptions().size(); j++) {
                                            btnTag = new Button(MapMyPlan.this);
                                            btnTag.setLayoutParams(new LinearLayout.LayoutParams
                                                    (LinearLayout.LayoutParams.WRAP_CONTENT,
                                                            ActionMenuView.LayoutParams.WRAP_CONTENT));

                                            if (i >= 1) {
                                                btnTag.setText(quesAnsModel1.getOptions().get(2 + j));
                                                btnTag.setTag(quesAnsModel1.getOptionsId().get(2 + j));
                                            } else {
                                                btnTag.setText(quesAnsModel1.getOptions().get(j));
                                                btnTag.setTag(quesAnsModel1.getOptionsId().get(j));

                                                row.addView(btnTag);
                                                Toast.makeText(getApplicationContext(), "Diaplay 1 " +
                                                        btnTag, Toast.LENGTH_SHORT).show();

                                            }
                                        }
//Toast.makeText(getApplicationContext(), "Diaplay 1 ", Toast.LENGTH_SHORT).show();
                                    }
                                }

//                                else if (v.getTag().equals("2")) {
//
//                                    QuesAnsModel quesAnsModel1 = new QuesAnsModel();
//                                    quesAnsModel1 = serverEvent.getQuesAnsModel().getData().get(2);
//                                    TextView quesText1 = new TextView(getApplicationContext());
//                                    quesText1.setText(quesAnsModel1.getQuestion());
//                                    linearLayout.addView(quesText1);
//
//                                    Toast.makeText(getApplicationContext(), "Diaplay 2 ", Toast.LENGTH_SHORT).show();
//
//                                }

                            }
                        });
                    }

//                    row.addView(btnTag);
                    linearLayout.addView(row);
                }
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
}
