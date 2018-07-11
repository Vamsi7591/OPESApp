package com.wilco.opesservice;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
    Button btnTag, submit;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_my_plan);

        communicator = new Communicator();

        usePost();

        linearLayout = (LinearLayout) findViewById(R.id.baseLayout);
        submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.getChildCount();

                for (int x = 0 ; x < linearLayout.getChildCount(); x++){

                    if(linearLayout.getChildAt(x).getTag() == "columnHeader") {
                        Log.v("TAG", "Que ==> " + linearLayout.getChildAt(x).getTag());

                        LinearLayout aa = (LinearLayout) linearLayout.getChildAt(x);
                        Log.v("TAG", "Que ==> " + aa.getChildAt(0).getTag());

                        LinearLayout bb = (LinearLayout) aa.getChildAt(1);
                        Log.v("TAG", "Options count ==> " + bb.getChildCount());
                    }

                }
            }
        });
    }


    private void usePost() {
        communicator.loadJSON();
    }


    private List<QuesAnsModel> quesAnsModelList = new ArrayList<>();

    @Subscribe
    public void onServerEvent(final ServerEvent serverEvent) throws JSONException {

        if (serverEvent.getQuesAnsModel().getStatus() && serverEvent.getQuesAnsModel().getCode() == 200)
            if (serverEvent.getQuesAnsModel().getData() != null) {
                quesAnsModelList = serverEvent.getQuesAnsModel().getData();
                displayQuestions(quesAnsModelList.get(0));
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

            submit.setVisibility(View.INVISIBLE);

            Log.d("linearLayout", "click btnTag : " + v.getTag());
            Log.d("linearLayout", "v btnTag : " + v.getRootView().hashCode());

            for (int i = 0; i < quesAnsModelList.size(); i++) {
                final QuesAnsModel model = quesAnsModelList.get(i);

                if (model.getId().get(0).matches(stringList.get(1))) {

                    int hCode = v.getParent().getParent().hashCode();

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

                            LinearLayout aa = ((LinearLayout) linearLayout.getChildAt(k));
                            aa.getChildCount(); // always 2 children's (TV , LL)
                            LinearLayout bb = (LinearLayout) aa.getChildAt(1); // 1 - for LL , 0 - TV

                            for (int b = 0; b < bb.getChildCount(); b++) {

                                if (bb.getChildAt(b).getTag() == v.getTag()) {
                                    v.setBackground(getResources().getDrawable(R.drawable.button_selected));
                                } else {
                                    bb.getChildAt(b).setBackground(getResources().getDrawable(R.drawable.button_unselected));
                                }
                            }

                            flag = true;
                        } else if (v.getParent().getParent().getParent().getParent().hashCode() == linearLayout.getChildAt(k).hashCode()) {

                            LinearLayout aa = ((LinearLayout) linearLayout.getChildAt(k));
                            aa.getChildCount(); // always 2 children's (TV , LL)
                            LinearLayout bb = (LinearLayout) aa.getChildAt(1); // 1 - for LL , 0 - TV

                            for (int b = 0; b < bb.getChildCount(); b++) {

                                LinearLayout cc = (LinearLayout) bb.getChildAt(b);

                                for (int c = 0; c < cc.getChildCount(); c++) {

                                    LinearLayout dd = (LinearLayout) cc.getChildAt(c);
                                    dd.getChildCount();

                                    for (int d = 0; d < dd.getChildCount(); d++) {
                                        if (dd.getChildAt(d).getTag() == v.getTag()) {
                                            v.setBackground(getResources().getDrawable(R.drawable.button_selected));
                                        } else {
                                            dd.getChildAt(d).setBackground(getResources().getDrawable(R.drawable.button_unselected));
                                        }
                                    }
                                }
                            }

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
                } else {

                    if (linearLayout.getChildCount() < i && model.getQuestion().contentEquals(stringList.get(0))) {

                        int hCode = v.getParent().getParent().hashCode();

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

                                LinearLayout aa = ((LinearLayout) linearLayout.getChildAt(k));
                                aa.getChildCount(); // always 2 children's (TV , LL)
                                LinearLayout bb = (LinearLayout) aa.getChildAt(1); // 1 - for LL , 0 - TV

                                for (int b = 0; b < bb.getChildCount(); b++) {

                                    if (bb.getChildAt(b).getTag() == v.getTag()) {
                                        v.setBackground(getResources().getDrawable(R.drawable.button_selected));
                                    } else {
                                        bb.getChildAt(b).setBackground(getResources().getDrawable(R.drawable.button_unselected));
                                    }
                                }

                                flag = true;
                            } else if (v.getParent().getParent().getParent().getParent().hashCode() == linearLayout.getChildAt(k).hashCode()) {

                                LinearLayout aa = ((LinearLayout) linearLayout.getChildAt(k));
                                aa.getChildCount(); // always 2 children's (TV , LL)
                                LinearLayout bb = (LinearLayout) aa.getChildAt(1); // 1 - for LL , 0 - TV

                                for (int b = 0; b < bb.getChildCount(); b++) {

                                    LinearLayout cc = (LinearLayout) bb.getChildAt(b);

                                    for (int c = 0; c < cc.getChildCount(); c++) {

                                        LinearLayout dd = (LinearLayout) cc.getChildAt(c);
                                        dd.getChildCount();

                                        for (int d = 0; d < dd.getChildCount(); d++) {
                                            if (dd.getChildAt(d).getTag() == v.getTag()) {
                                                v.setBackground(getResources().getDrawable(R.drawable.button_selected));
                                            } else {
                                                dd.getChildAt(d).setBackground(getResources().getDrawable(R.drawable.button_unselected));
                                            }
                                        }
                                    }
                                }

                                flag = true;
                            }
                        }

                        submit.setVisibility(View.VISIBLE);
                        Toast.makeText(MapMyPlan.this, "Thanks for the survey.", Toast.LENGTH_SHORT).show();
                    }

                }
            }

        } catch (Exception e) {
            Log.d("Exception", "e : " + e.getMessage());
        }
    }

    private void displayQuestions(QuesAnsModel quesAnsModel) {

        if (quesAnsModel.getId().size() != 0) {

            LinearLayout columnHeader = new LinearLayout(this);

            columnHeader.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            columnHeader.setOrientation(LinearLayout.VERTICAL);

            LinearLayout rowHeader = new LinearLayout(this);

            rowHeader.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            rowHeader.setOrientation(LinearLayout.HORIZONTAL);
            rowHeader.setGravity(Gravity.CENTER);
            rowHeader.setTag("rowHeader");

            TextView quesText = new TextView(this);
            LinearLayout.LayoutParams paramTV = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            paramTV.setMargins(5, 5, 5, 5);
            quesText.setLayoutParams(paramTV);
            quesText.setTextSize(18f);
            quesText.setText(quesAnsModel.getQuestion());
            quesText.setTag(quesAnsModel.getQuestion());
            columnHeader.addView(quesText);
            columnHeader.setTag("columnHeader");

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
                    param.setMargins(5, 5, 5, 5);
                    btnTag.setLayoutParams(param);

                    btnTag.setOnClickListener(this);

                    btnTag.setText(quesAnsModel.getOptions().get(j));
                    btnTag.setTag(quesAnsModel.getQuestion() + "," + quesAnsModel.getOptionsId().get(j));
                    btnTag.setBackground(getResources().getDrawable(R.drawable.button_unselected));

                    rowHeader.addView(btnTag);
                }
            } else {
                // more then one
                LinearLayout columnChild = new LinearLayout(this);
                columnChild.setLayoutParams(new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                columnChild.setOrientation(LinearLayout.VERTICAL);
                columnChild.setGravity(Gravity.CENTER);
                columnChild.setTag("columnChild");


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
                    rowChild.setGravity(Gravity.CENTER);
                    rowChild.setTag("rowChild");

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
                        param.setMargins(5, 5, 5, 5);
                        btnTag.setLayoutParams(param);

                        btnTag.setOnClickListener(this);

                        btnTag.setText(quesAnsModel.getOptions().get(index));
                        btnTag.setTag(quesAnsModel.getQuestion() + "," + quesAnsModel.getOptionsId().get(index));
                        btnTag.setBackground(getResources().getDrawable(R.drawable.button_unselected));

                        rowChild.addView(btnTag);

                        index = index + 1;
                    }

                    if (rowChild.getChildCount() != 0)
                        columnChild.addView(rowChild);
                }

                rowHeader.addView(columnChild);

            }

            columnHeader.addView(rowHeader);
            linearLayout.addView(columnHeader);
            linearLayout.setTag("" + quesAnsModel.getId().get(0));
//            Log.d("linearLayout", "Tag : " + linearLayout.getTag());
//            Log.d("linearLayout", "child's : " + linearLayout.getChildCount());
        }
    }

}
