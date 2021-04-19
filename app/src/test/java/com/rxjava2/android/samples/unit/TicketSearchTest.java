package com.rxjava2.android.samples.unit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class TicketSearchTest {

    @Mock TicketSearch.Listener listener;
    @Mock TicketProvider ticketProvider;
    private TicketSearch ticketSearch;
    private TestScheduler testScheduler = new TestScheduler();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.ticketSearch = new TicketSearch(
                testScheduler,
                testScheduler,
                ticketProvider
        );
        this.ticketSearch.setListener(listener);
    }

    @Test
    public void valid() {
        // Assemble
        doReturn(Single.just("One")).when(ticketProvider).dataAsSingle();

        // Act
        ticketSearch.fetchData();
        testScheduler.triggerActions();

        // Assert
        verify(listener).onSuccess("One");
    }

    @Test
    public void error() {
        // Assemble
        doReturn(Single.error(RuntimeException::new)).when(ticketProvider).dataAsSingle();

        // Act
        ticketSearch.fetchData();
        testScheduler.triggerActions();

        // Assert
        verify(listener).onFailure(any());
    }

    @Test
    public void validTimed() {
        // Assemble
        doReturn(Single.just("One")).when(ticketProvider).dataAsSingle();

        // Act
        ticketSearch.fetchDataDelay();
        testScheduler.advanceTimeBy(5L, TimeUnit.SECONDS);

        // Assert
        verify(listener).onSuccess("One");
    }

    @Test
    public void validTimed_didNotPass() {
        // Assemble
        doReturn(Single.just("One")).when(ticketProvider).dataAsSingle();

        // Act
        ticketSearch.fetchDataDelay();
        testScheduler.advanceTimeBy(1L, TimeUnit.SECONDS);

        // Assert
        verify(listener, never()).onSuccess(any());
    }

    @Test
    public void testStream() {
        // Assemble
        doReturn(Single.just("One")).when(ticketProvider).dataAsSingle();

        // Act
        final TestObserver<String> result = ticketSearch.fetchAsStream().test();

        // Assert
        result.assertValue("OneX");
        result.assertComplete();
    }

}