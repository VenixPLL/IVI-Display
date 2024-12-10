package me.venixpll.ivi.core.javalin.handler.error;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import java.nio.file.Paths;
import me.venixpll.ivi.IVIDisplay;
import me.venixpll.ivi.core.error.InternalExceptionTracker;
import me.venixpll.ivi.core.javalin.JavalinHandler;
import org.jetbrains.annotations.NotNull;

public class ErrorRelay extends JavalinHandler {

    @Override
    public void addHandler(final Javalin javalin, final IVIDisplay rootApp) throws Exception {
        javalin.get("/errors",ctx -> {
            final var errors = new JSONArray();

            InternalExceptionTracker.getThrowableHashMap().forEach((time, throwable) -> {
                final var error = new JSONObject();

                error.put("time", time);
                error.put("type", throwable.getClass().getName());
                error.put("message", throwable.getMessage());
                error.put("cause", throwable.getCause());

                final var stacktrace = getElements(throwable);
                error.put("stacktrace",stacktrace);

                errors.add(error);
            });

            ctx.contentType(ContentType.JSON);
            ctx.result(errors.toString());
            ctx.status(200);
        });

        javalin.get("/errorSummary", ctx -> {
            final var path =  Paths.get("");
            ctx.redirect(path.toAbsolutePath() + "/error/errorDetails.html");
        });

        javalin.get("/reboot",ctx -> {
            System.exit(0);
        });
    }

    private static @NotNull JSONArray getElements(final Throwable throwable) {
        final var stacktrace = new JSONArray();
        for(final StackTraceElement element : throwable.getStackTrace()){
            // classLoader, fileName, className, lineNumber, methodName,
            stacktrace.add(String.format("%s -> %s.%s(%s:%s)",
                    element.getClassLoaderName(),
                    element.getClassName(),
                    element.getMethodName(),
                    element.getFileName(),
                    element.getLineNumber()));
        }
        return stacktrace;
    }
}
