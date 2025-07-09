package ru.quizie;

import ru.quizie.portscanner.PortScannerManager;
import ru.quizie.portscanner.data.PortScanner;
import ru.quizie.utils.LogFileUtil;

import java.io.FileNotFoundException;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Config.init();

        final LogFileUtil logfile = new LogFileUtil(UUID.randomUUID().toString());
        final PortScanner scanner = new PortScanner();

        // ip
        scanner.setIp(Config.getProperty("ip"));

        // ports
        scanner.setStartPort(Integer.parseInt(Config.getProperty("beginning-port")));
        scanner.setEndPort(Integer.parseInt(Config.getProperty("ending-port")));

        // others 4837
        scanner.setTimeout(Integer.parseInt(Config.getProperty("timeout")));
        scanner.setLogfile(logfile);

        PortScannerManager.start(scanner);
    }
}