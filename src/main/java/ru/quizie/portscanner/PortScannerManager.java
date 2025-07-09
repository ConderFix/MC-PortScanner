package ru.quizie.portscanner;

import ru.quizie.Config;
import ru.quizie.portscanner.data.PortScanner;
import ru.quizie.utils.LogFileUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PortScannerManager {

    private static final ExecutorService dbExecutor = Executors.newFixedThreadPool(Integer.parseInt(Config.getProperty("fixed-thread-pool")));

    public static void start(PortScanner portScanner) {
        final LogFileUtil logfile = portScanner.getLogfile();

        for (int port = portScanner.getStartPort(); port < portScanner.getEndPort(); port++) {
            final int currentPort = port;
            final String ip = portScanner.getIp();

            dbExecutor.submit(() -> {
                final boolean result = isOnlineServer(ip, currentPort, portScanner.getTimeout());
                System.out.println(ip + ":" + currentPort + " result:" + result);

                if (result) logfile.record(portScanner.getIp() + ":" + currentPort);
            });
        }

        dbExecutor.shutdown();
    }

    public static boolean isOnlineServer(String ip, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.setSoTimeout(timeout);
            socket.connect(new InetSocketAddress(ip, port), 3000);

            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            final DataInputStream in = new DataInputStream(socket.getInputStream());

            out.writeByte(0x00);
            out.writeByte(0x00);
            out.writeByte(0x016);
            out.writeByte(ip.length());
            out.writeBytes(ip);
            out.writeShort(port);
            out.writeByte(0x01);

            out.writeByte(0x01);
            out.writeByte(0x00);

            for(int i = 0; ; i++) {
                byte b = in.readByte();
                if(i > 5) return false;
                if((b & 0x80) != 128) break;
            }

            int packetId = 0;
            for(int i = 0; ; i++) {
                byte b = in.readByte();
                packetId |= (b & 0x7F) << i*7;
                if(i > 5) return false;
                if((b & 0x80) != 128) break;
            }
            if(packetId != 0) return false;

            int jsonLength = 0;
            for(int i = 0; ; i++) {
                byte b = in.readByte();
                jsonLength |= (b & 0x7F) << i*7;
                if(i > 5) return false;
                if((b & 0x80) != 128) break;
            }
            byte[] json = new byte[jsonLength];
            in.readFully(json);

            out.writeByte(0x09);
            out.writeByte(0x01);
            out.writeLong(System.currentTimeMillis());

            for(int i = 0; ; i++) {
                byte b = in.readByte();
                if(i > 5) return false;
                if((b & 0x80) != 128) break;
            }

            for(int i = 0; ; i++) {
                byte b = in.readByte();
                packetId |= (b & 0x7F) << i*7;
                if(i > 5) return false;
                if((b & 0x80) != 128) break;
            }

            return packetId == 1;

        } catch (Exception e) {
            return false;
        }
    }


}
