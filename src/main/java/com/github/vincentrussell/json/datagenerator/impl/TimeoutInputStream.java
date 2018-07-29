package com.github.vincentrussell.json.datagenerator.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * {@link InputStream} wrapper that will timeout if read doesn't happen within specified time
 */
public final class TimeoutInputStream extends InputStream {

    private static ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);

    private final InputStream inputStream;
    private final long timeout;
    private final TimeUnit timeUnit;
    private boolean receivedData = false;


    /**
     * default constuctor
     * @param inputStream wrapped {@link InputStream}
     * @param timeout timeout value
     * @param timeUnit time unit value like millis or seconds
     */
    public TimeoutInputStream(final InputStream inputStream, final long timeout,
        final TimeUnit timeUnit) {
        this.inputStream = inputStream;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    public int read() throws IOException {

        int result = -1;
        Future<Integer> future = EXECUTOR.submit(new ReadNext(inputStream));
        try {
            result = getInteger(future);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            result = -1;
        }

        receivedData = true;
        return result;
    }

    private Integer getInteger(final Future<Integer> future) throws InterruptedException,
        ExecutionException, TimeoutException {
        if (!receivedData) {
            return future.get(1, TimeUnit.MINUTES);
        } else {
            return future.get(timeout, timeUnit);
        }
    }

    @Override
    public void close() throws IOException {
        //noop
    }

    /**
     * helper class to support using a future to get the next integer from the inputstream
     */
    private static final class ReadNext implements Callable<Integer> {

        private final InputStream inputStream;

        private ReadNext(final InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public Integer call() throws Exception {
            return inputStream.read();
        }
    }
}
