package com.codeexamples.java.async;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

public class SimpleSocketClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        try (Socket socket = new Socket(InetAddress.getLocalHost(), 8090)) {
            System.out.println("Created socket " + socket);
            final InputStream inputStream = socket.getInputStream();
            socket.getOutputStream().write("Hello World".getBytes());
//            byte [] buff = new byte[1024];
//            int count = 0;
//            while(count < 5) {
//                System.out.println("Writing");
//                socket.getOutputStream().write("Hello World".getBytes());
//                Thread.sleep(1000);
//                int read = inputStream.read(buff, 0, buff.length);
//                if(read == -1) {
//                    break;
//                } else if(read != 0) {
//                    System.out.println(new String(buff,0,read,"UTF-8"));
//
//                }
//                count++;
//            }
        }

    }
}
