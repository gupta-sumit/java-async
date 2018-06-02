package com.codeexamples.java.async;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockingServer {

    public static void main(String[] args) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Waiting for connection");
        final Socket socket = serverSocket.accept();
        System.out.println("ManySockets connected " + socket);
        final InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = reader.readLine()) != null) {
            socket.getOutputStream().write(line.toUpperCase().getBytes());
        }
        inputStream.close();
        socket.getOutputStream().close();
        socket.close();
    }
}
