package com.denknd;

import com.denknd.config.ManualConfig;

public class MonitoringServiceApplication {
    public static void main(String[] args) {
        var context = new ManualConfig();
        var console = context.console();
        console.run();
    }
}