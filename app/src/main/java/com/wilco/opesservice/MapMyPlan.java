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


    private List<QuesAnsModel> quesAnsModelList = new ArrayList<>();

    @Subscribe
    public void onServerEvent(final ServerEvent serverEvent) throws JSONException {
        //Toast.makeText(this, "" + serverEvent.getQuesAnsModel().getData(), Toast.LENGTH_SHORT).show();

        if (serverEvent.getQuesAnsModel().getStatus() && serverEvent.getQuesAnsModel().getCode() == 200)
            if (serverEvent.getQuesAnsModel().getData() != null) {
                quesAnsModelList = serverEvent.getQuesAnsModel().getData();
                /*QuesAnsModel quesAnsModel = new QuesAnsModel();
                quesAnsModel = quesAnsModelList.get(0);*/
                displayQuestions(quesAnsModelList.get(0));

                /*final QuesAnsModel finalQuesAnsModel = quesAnsModel;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayQuestions(quesAnsModel);
                    }
                });*/

            }
    }

    // Function to divide a by b and
    // return floor value it
    static int divide(int dividend, int divisor) {

        // Calculate sign of divisor i.e.,
        // sign will be negative only iff
        // either one of them is negative
        // otherwise it will be positive
        int sign = ((dividend < 0) ^ (divisor < 0)) ? -1 : 1;

        // Update both divisor and
        // dividend positive
        dividend = Math.abs(dividend);
        divisor = Math.abs(divisor);

        // Initialize the quotient
        int quotient = 0;

        while (dividend >= divisor) {
            dividend -= divisor;
            ++quotient;
        }

        return sign * quotient;
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
                final QuesAnsModel model = quesAnsModelList.get(i);

                if (model.getId().get(0).matches(stringList.get(1))) {

                    int hCode = v.getParent().getParent().hashCode();
                    String s = "" + linearLayout.getTag().toString();

                    boolean flag = false;
                    for (int k = 0; k < linearLayout.getChildCount(); k++) {

//                        View v11 = ((Button) linearLayout.getChildAt(k).findViewById(Integer.parseInt(stringList.get(1))));
//                        hCode == v11.getParent().getParent().hashCode();

                        if (flag) {
                            for (int v1 = k; v1 <= linearLayout.getChildCount(); v1++) {
                                linearLayout.removeViewAt(k);
                                linearLayout.requestLayout();
                            }
                            break;
                        }

                        LinearLayout xx = (LinearLayout) linearLayout.getChildAt(k);
                        xx.getChildAt(1).hashCode();
                        if (hCode == linearLayout.getChildAt(k).hashCode() || v.getParent().getParent().getParent().getParent().hashCode() == linearLayout.getChildAt(k).hashCode() ) {
                            flag = true;
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayQuestions(model);
                        }
                    });

                    break;
                }
            }

        } catch (Exception e) {
            Log.d("Exception", "e : " + e.getMessage());
        }
    }

    private void displayQuestions(QuesAnsModel quesAnsModel) {

        if (quesAnsModel.getId().size() != 0) {

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

            int noOfOptions = quesAnsModel.getOptions().size();

            if (noOfOptions <= 3) {
                // one coloumn
                for (int j = 0; j < noOfOptions; j++) {
                    btnTag = new Button(this);
                    btnTag.setId(Integer.parseInt(quesAnsModel.getOptionsId().get(j)));
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.0f
                    );
                    btnTag.setLayoutParams(param);
                    /*btnTag.setLayoutParams(new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.WRAP_CONTENT, ActionMenuView.LayoutParams.WRAP_CONTENT));*/

                    btnTag.setOnClickListener(this);

                    btnTag.setText(quesAnsModel.getOptions().get(j));
                    btnTag.setTag(quesAnsModel.getQuestion() + "," + quesAnsModel.getOptionsId().get(j));

                    row.addView(btnTag);
                }
            } else {
                // more then one
                LinearLayout columnChild = new LinearLayout(this);
                columnChild.setLayoutParams(new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                columnChild.setOrientation(LinearLayout.VERTICAL);


                int quotient = divide(noOfOptions, 3);
                int noOfRows = noOfOptions / 3 + quotient;
                int noOfQueForColoumn = 3;
                int index = 0;

                if (noOfRows == 2 && noOfOptions == 4) {
                    noOfQueForColoumn = 2;
                }


                for (int j = 0; j < noOfRows; j++) {

                    LinearLayout rowChild = new LinearLayout(this);
                    rowChild.setLayoutParams(new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    rowChild.setOrientation(LinearLayout.HORIZONTAL);

                    for (int z = 0; z < noOfQueForColoumn; z++) {
                        if (index == noOfOptions)
                            break;

                        btnTag = new Button(this);
                        btnTag.setId(Integer.parseInt(quesAnsModel.getOptionsId().get(index)));

                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1.0f
                        );
                        btnTag.setLayoutParams(param);

                        /*btnTag.setLayoutParams(new LinearLayout.LayoutParams
                                (LinearLayout.LayoutParams.WRAP_CONTENT, ActionMenuView.LayoutParams.WRAP_CONTENT));
                        */
                        btnTag.setOnClickListener(this);

                        btnTag.setText(quesAnsModel.getOptions().get(index));
                        btnTag.setTag(quesAnsModel.getQuestion() + "," + quesAnsModel.getOptionsId().get(index));

                        rowChild.addView(btnTag);

                        index = index + 1;
                    }

                    if (rowChild.getChildCount() != 0)
                        columnChild.addView(rowChild);
                }

                row.addView(columnChild);

            }

            column.addView(row);
            linearLayout.addView(column);
            linearLayout.setTag("" + quesAnsModel.getId().get(0));
            Log.d("linearLayout", "Tag : " + linearLayout.getTag());
            Log.d("linearLayout", "child's : " + linearLayout.getChildCount());
        }
    }

}
