package com.brogrammer.streamspace.resilience;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RetryService<T> {

    private static final int DEFAULT_MAX_ATTEMPTS = 4;
    private static final long DEFAULT_WAIT_MILLIS = 1000L;

    public T retry(RetryExecutor<T> retryExecutor) {
        return retry(retryExecutor, DEFAULT_MAX_ATTEMPTS, DEFAULT_WAIT_MILLIS);
    }

    public T retry(RetryExecutor<T> retryExecutor, int maxAttempts, long waitMillis) {
        int remainingAttempts = maxAttempts;

        while (remainingAttempts > 0) {
            try {
                T result = retryExecutor.run();
                if (result != null) {
                    return result;
                }
                // Result is null, decrement attempts and retry
                remainingAttempts--;
                if (remainingAttempts > 0 && !waitBeforeNextRetry(waitMillis)) {
                    return null; // Interrupted, exit early
                }
            } catch (Exception e) {
                remainingAttempts--;
                if (remainingAttempts > 0) {
                    log.error(e.getMessage());
                    if (!waitBeforeNextRetry(waitMillis)) {
                        return null; // Interrupted, exit early
                    }
                } else {
                    log.error(e.getMessage(), e);
                }
            }
        }

        return null;
    }

    /**
     * Waits before the next retry attempt.
     * @return true if wait completed successfully, false if interrupted
     */
    private boolean waitBeforeNextRetry(long waitMillis) {
        try {
            log.info("Waiting before next retry...");
            Thread.sleep(waitMillis);
            return true;
        } catch (InterruptedException e) {
            log.error("Retry interrupted: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

}
