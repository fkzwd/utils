package com.vk.dwzkf.utils.stages;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class StageTest {
    @Test
    public void testCatchProperly() {
        Stage<Throwable> stage = Stage.anyThrowable(() -> {
            throw new RuntimeException();
        });
        stage.execute();
        StageState expected = StageState.FAILED;
        Assert.assertEquals(expected, stage.getStageState());
    }

    @Test(expected = RuntimeException.class)
    public void testThrowIfExceptionIsNotInstanceOfGenericType() {
        Stage<?> stage = getWithChild(() -> {throw new RuntimeException();});
        stage.execute();
    }


    private Stage<?> getWithChild(Runnable action) {
        class ExceptionParent extends RuntimeException { }
        class ExceptionChild extends ExceptionParent{}
        return new Stage<>(ExceptionParent.class, action);
    }
}
