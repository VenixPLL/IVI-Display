package me.venixpll.ivi.jcef;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import lombok.Getter;
import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import me.friwi.jcefmaven.impl.progress.ConsoleProgressHandler;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.cef.handler.CefFocusHandlerAdapter;

public class JCEFFrame extends JFrame {

    @Getter
    private final CefApp cefApp;

    @Getter
    private final CefClient client;

    @Getter
    private final CefBrowser browser;
    private boolean browserFocus = true;


    public static void initializeCefApp(){
        final var installDir = new File("jcef-bundle");
        final CefAppBuilder cefBuilder = new CefAppBuilder();
        cefBuilder.setInstallDir(installDir);
        cefBuilder.setProgressHandler(new ConsoleProgressHandler());
        cefBuilder.getCefSettings().windowless_rendering_enabled = true;
        final var cache = new File(installDir,"cache");
        if(!cache.exists()) cache.mkdir();
        cefBuilder.getCefSettings().cache_path = cache.getAbsolutePath();
        cefBuilder.getCefSettings().root_cache_path = cache.getAbsolutePath();
        cefBuilder.getCefSettings().persist_session_cookies = true;
    }

    public JCEFFrame(final String startURL, final boolean useOSR, final boolean isTransparent) throws UnsupportedPlatformException, CefInitializationException, IOException, InterruptedException {
        final CefAppBuilder builder = new CefAppBuilder();
        builder.getCefSettings().windowless_rendering_enabled = useOSR;
        builder.setAppHandler(new MavenCefAppHandlerAdapter() {
            @Override
            public void stateHasChanged(final org.cef.CefApp.CefAppState state) {
                if (state == CefApp.CefAppState.TERMINATED || state == CefApp.CefAppState.SHUTTING_DOWN) System.exit(0);
            }
        });

        this.cefApp = builder.build();
        this.client = this.cefApp.createClient();
        final CefMessageRouter msgRouter = CefMessageRouter.create(new CefMessageRouter.
                CefMessageRouterConfig("cefQuery", "cefQueryCancel"));

        this.client.addMessageRouter(msgRouter);
        this.browser = this.client.createBrowser(startURL, useOSR, isTransparent);
        this.browser.createImmediately();
        final var browserUI = this.browser.getUIComponent();
        this.client.addFocusHandler(new CefFocusHandlerAdapter() {
            @Override
            public void onGotFocus(final CefBrowser browser) {
                if (JCEFFrame.this.browserFocus) return;
                JCEFFrame.this.browserFocus = true;
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                browser.setFocus(true);
            }

            @Override
            public void onTakeFocus(final CefBrowser browser, final boolean next) {
                JCEFFrame.this.browserFocus = false;
            }
        });

        this.setResizable(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().add(browserUI, BorderLayout.CENTER);
        this.pack();

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.toFront();
        this.repaint();

        this.setSize(1280,720);

        final GraphicsDevice device = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getScreenDevices()[0];
        //device.setFullScreenWindow(this); // Allow fullscreen

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                JCEFFrame.this.cefApp.dispose();
                JCEFFrame.this.browser.close(true);
                JCEFFrame.this.client.dispose();
                CefApp.getInstance().dispose();
                JCEFFrame.this.dispose();
            }
        });
    }
}
