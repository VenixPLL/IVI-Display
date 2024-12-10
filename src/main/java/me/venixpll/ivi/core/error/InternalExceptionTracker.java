package me.venixpll.ivi.core.error;

import java.util.HashMap;
import lombok.Getter;
import me.venixpll.ivi.IVIDisplay;

public class InternalExceptionTracker {

    @Getter
    private static final HashMap<Long, Throwable> throwableHashMap = new HashMap<>();

    public static void showErrors(){
        IVIDisplay.getInstance().displayPage("/web/error/errorPage.html");
    }

    public static void handleException(final Exception exception){
        System.err.println("Exception handled -> " + exception.getMessage());
        exception.printStackTrace();
        throwableHashMap.put(System.currentTimeMillis(),exception);
    }

    public static void handleThrowable(final Throwable throwable){
        System.err.println("Throwable handled -> " + throwable.getMessage());
        throwable.printStackTrace();
        throwableHashMap.put(System.currentTimeMillis(), throwable);
    }

    public HashMap<Long, Throwable> getRecordedErrors(){
        return throwableHashMap;
    }
}
