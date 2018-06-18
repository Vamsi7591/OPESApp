package com.wilco.opesservice;

import com.squareup.otto.Bus;

/**
 * Created by Vijay on 20.07.2017
 */

public class BusProvider {

    private static final Bus BUS = new Bus();

    public static Bus getInstance(){
        return BUS;
    }

    public BusProvider(){}
}
