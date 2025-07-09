package ru.quizie.utils;

import lombok.Builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogFileUtil {

    public PrintWriter printWriter;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LogFileUtil(String fileName) throws FileNotFoundException {
        fileName = fileName + ".log";

        this.printWriter = new PrintWriter(fileName);
        System.out.println("Created a log file '"+fileName+"'");
    }

    public void record(String text) {
        final String time = LocalDateTime.now().format(dateTimeFormatter);
        printWriter.println("[" + time + "] " + text);
        printWriter.close();
    }

}
