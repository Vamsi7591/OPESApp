package com.wilco.opesservice;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Vijay on 20.07.2017
 */

public interface RetroInterface {

    @GET(".json")
    Call<QuesAnsModel.DataResult> getQuesAns();
}
