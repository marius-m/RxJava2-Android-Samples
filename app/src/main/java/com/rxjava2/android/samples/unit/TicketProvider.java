package com.rxjava2.android.samples.unit;

import io.reactivex.Flowable;
import io.reactivex.Single;

class TicketProvider {

    public Single<String> dataAsSingle() {
        return Single.just("One");
    }

    public Flowable<String> dataAsFlowable() {
        return Flowable.just("One", "Two", "Three");
    }

}
