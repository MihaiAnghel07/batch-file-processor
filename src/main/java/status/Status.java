package status;

import java.util.concurrent.atomic.AtomicInteger;

public class Status {

    private final AtomicInteger goodRecordsNumber = new AtomicInteger(0);
    private final AtomicInteger badRecordsNumber = new AtomicInteger(0);

    private Status() {
    }

    public void incrementGoodRecordsNumber() {
        goodRecordsNumber.incrementAndGet();
    }

    public void incrementBadRecordsNumber() {
        badRecordsNumber.incrementAndGet();
    }

    public int getGoodRecordsNumber() {
        return goodRecordsNumber.get();
    }

    public int getBadRecordsNumber() {
        return badRecordsNumber.get();
    }

    private static class SingletonHelper {
        private static final Status INSTANCE = new Status();
    }

    public static Status getInstance() {
        return SingletonHelper.INSTANCE;
    }
}
