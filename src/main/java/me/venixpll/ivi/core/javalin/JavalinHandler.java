package me.venixpll.ivi.core.javalin;

import io.javalin.Javalin;
import me.venixpll.ivi.IVIDisplay;

public abstract class JavalinHandler {

    public abstract void addHandler(final Javalin javalin, final IVIDisplay rootApp) throws Exception;

}
