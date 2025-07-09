package ru.quizie.portscanner.data;

import lombok.Data;
import ru.quizie.utils.LogFileUtil;

@Data
public class PortScanner {
    private String ip;
    private int startPort;
    private int endPort;
    private int timeout;
    private LogFileUtil logfile;
}
