package me.venixpll.ivi;

import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import me.venixpll.ivi.core.error.InternalExceptionTracker;
import me.venixpll.ivi.core.javalin.JavalinInternalServer;
import me.venixpll.ivi.core.openfm.OpenFMIntegration;
import me.venixpll.ivi.jcef.JCEFFrame;

public class IVIDisplay {

    private static IVIDisplay INSTANCE;

    @Getter
    private final InternalExceptionTracker internalExceptionTracker;

    private JCEFFrame jcefFrame;

    @Getter
    private final OpenFMIntegration openFM = new OpenFMIntegration();
    private final JavalinInternalServer javalinInternalServer;

    public IVIDisplay(){
        this.internalExceptionTracker = new InternalExceptionTracker();
        JCEFFrame.initializeCefApp();
        this.javalinInternalServer = new JavalinInternalServer();

        INSTANCE = this;
    }

    public void run() throws Exception {
        this.javalinInternalServer.registerHandlers(this);
        this.javalinInternalServer.start(7070);

        this.jcefFrame = new JCEFFrame("about:blank", false, false);
        this.displayPage("/web/load/loading.html");

        this.openFM.fetchStations();
        this.openFM.queryAllRDS();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this.openFM::queryAllRDS,15,15, TimeUnit.SECONDS);

        this.displayPage("/web/music/index.html");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.javalinInternalServer.stop();
            this.jcefFrame.getBrowser().close(true);
            this.jcefFrame.getCefApp().dispose();
        }));

    }

    public void displayPage(final String pagePath){
        final var relativePath = Paths.get("");
        this.jcefFrame.getBrowser().loadURL(relativePath.toAbsolutePath() + pagePath);
    }

    public static IVIDisplay getInstance() {
        return INSTANCE;
    }
}
