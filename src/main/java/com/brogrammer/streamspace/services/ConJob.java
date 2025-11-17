/*package com.akshathsaipittala.streamspace.services;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Supplier;

@Service
public class ConJob {

    public <T> List<Future<T>> executeAll(List<Callable<T>> tasks)
            throws InterruptedException {

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            List<? extends Supplier<Future<T>>> futures = tasks.stream()
                    .map(ConJob::asFuture)
                    .map(scope::fork)
                    .toList();
            scope.join();
            return futures.stream().map(Supplier::get).toList();
        }

    }

    static <T> Callable<Future<T>> asFuture(Callable<T> task) {

        return () -> {
            try {
                return CompletableFuture.completedFuture(task.call());
            } catch (Exception ex) {
                return CompletableFuture.failedFuture(ex);
            }
        };

    }

}*/
