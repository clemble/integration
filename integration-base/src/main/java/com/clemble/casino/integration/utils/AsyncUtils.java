package com.clemble.casino.integration.utils;

import org.junit.Assert;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Created by mavarazy on 11/26/14.
 */
public class AsyncUtils {

    private AsyncUtils() {
        throw new IllegalAccessError();
    }

    public static void verify(Supplier<Boolean> check) {
        Assert.assertTrue(check(check));
    }

    public static <T> void verifyNotNull(Supplier<T> check) {
        Assert.assertNotNull(checkNotNull(check));
    }

    public static <T> void verifyEquals(Supplier<T> A, Supplier<T> B) {
        boolean check = check(() -> {
            try {
                return A.get().equals(B.get());
            } catch (Exception e) {
                return false;
            }
        });
        if (!check) {
            Assert.assertEquals(A.get(), B.get());
        }
    }

    public static boolean check(Supplier<Boolean> check) {
        return check(check, 30_000);
    }

    public static boolean check(Supplier<Boolean> check, long checkTimeout) {
        long timeout = System.currentTimeMillis() + checkTimeout;
        while(timeout > System.currentTimeMillis()) {
            boolean result = false;
                try {
                    result = check.get();
                } catch (Throwable throwable) {
                }
            if (result) {
                return true;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static <T> boolean checkNotNull(Supplier<T> f) {
        return check(() -> {
            try {
                return f.get() != null;
            } catch (Throwable throwable) {
                return false;
            }
        });
    }

    public static <T> T get(Callable<T> get, long getTimeout) {
        long timeout = System.currentTimeMillis() + getTimeout;
        while(timeout > System.currentTimeMillis()) {
            try {
                T candidate = get.call();
                if (candidate != null)
                    return candidate;
            } catch (Throwable throwable) {
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
