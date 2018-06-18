package com.wilco.opesservice;

import android.util.Log;

import com.squareup.otto.Produce;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Vijay on 20.07.2017
 */

public class Communicator {

    private static  final String TAG = "Communicator";
    private static final String SERVER_URL = "https://wilcodemo-26ee5.firebaseio.com/";


    public void loadJSON(){

        //Here a logging interceptor is created
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        //The Retrofit builder will have the client attached, in order to get connection logs
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SERVER_URL)
                .build(); RetroInterface service = retrofit.create(RetroInterface.class);

        Call<QuesAnsModel.DataResult> call = service.getQuesAns();

        call.enqueue(new Callback<QuesAnsModel.DataResult>() {
            @Override
            public void onResponse(Call<QuesAnsModel.DataResult> call, Response<QuesAnsModel.DataResult> response) {
                BusProvider.getInstance().post(new ServerEvent(response.body()));
                Log.e(TAG,"Success");

            }

            @Override
            public void onFailure(Call<QuesAnsModel.DataResult> call, Throwable t) {
                // handle execution failures like no internet connectivity
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));
            }
        });
    }

    @Produce
    public ServerEvent produceServerEvent(QuesAnsModel.DataResult quesAnsModel) {
        return new ServerEvent(quesAnsModel);
    }

    @Produce
    public ErrorEvent produceErrorEvent(int errorCode, String errorMsg) {
        return new ErrorEvent(errorCode, errorMsg);
    }

}
