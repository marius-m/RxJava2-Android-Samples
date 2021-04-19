package com.rxjava2.android.samples.unit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * Lifecycle: {@link #onAttach()}, {@link #onDetach()}
 */
class TicketSearch {

    private static final String TAG = TicketSearch.class.getSimpleName();

    @NonNull private final Scheduler schedulerIo;
    @NonNull private final Scheduler schedulerUi;
    @NonNull private final TicketProvider ticketProvider;

    @Nullable private Disposable disp;
    @Nullable private Listener listener;

    public void onAttach() { }

    public void onDetach() {
        if (disp != null) {
            disp.dispose();
        }
    }

    public TicketSearch(
            @NonNull Scheduler schedulerIo,
            @NonNull Scheduler schedulerUi,
            @NonNull TicketProvider ticketProvider
    ) {
        this.schedulerIo = schedulerIo;
        this.schedulerUi = schedulerUi;
        this.ticketProvider = ticketProvider;
    }

    public Single<String> fetchAsStream() {
        return ticketProvider.dataAsSingle()
                .map((data) -> String.format("%sX", data));
    }

    public void fetchData() {
        this.disp = ticketProvider.dataAsSingle()
                .subscribeOn(schedulerIo)
                .observeOn(schedulerUi)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Timber.tag(TAG).d(String.format("onNext: %s", s));
                        listener.onSuccess(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.tag(TAG).d(String.format("onError: %s", throwable));
                        listener.onFailure(throwable);
                    }
                });
    }

    public void fetchDataDelay() {
        this.disp = Single.timer(2L, TimeUnit.SECONDS, schedulerIo)
                .flatMap((time) -> ticketProvider.dataAsSingle())
                .subscribeOn(schedulerIo)
                .observeOn(schedulerUi)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Timber.tag(TAG).d(String.format("onNext: %s", s));
                        listener.onSuccess(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.tag(TAG).d(String.format("onError: %s", throwable));
                        listener.onFailure(throwable);
                    }
                });
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess(@NonNull String item);
        void onFailure(@NonNull Throwable error);
    }

}
