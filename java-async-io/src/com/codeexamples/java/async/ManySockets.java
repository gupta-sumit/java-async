package com.codeexamples.java.async;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ManySockets {

    public static void main(String[] args) throws IOException, InterruptedException {
        List<Socket> sockets = IntStream.range(0, 2000).mapToObj(i -> {
            try {
                Socket socket = new Socket(InetAddress.getLocalHost(), 8090);
                System.out.println("Created socket " + socket);
                return socket;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }).collect(Collectors.toList());
        while(true) {
            Socket socket = sockets.get((int)Math.random()*2000);
            socket.getOutputStream().write(("Hello World " + UUID.randomUUID().toString()).getBytes() );
            Thread.sleep((long) (Math.random()*3000));
        }
    }

}

