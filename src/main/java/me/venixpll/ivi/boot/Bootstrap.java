package me.venixpll.ivi.boot;

import me.venixpll.ivi.IVIDisplay;
import me.venixpll.ivi.jcef.JCEFFrame;

public class Bootstrap {

    public static void main(String[] args) throws Exception{
        final var display = new IVIDisplay();
        display.run();
    }

}
