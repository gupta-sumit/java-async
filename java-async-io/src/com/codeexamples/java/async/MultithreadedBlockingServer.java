package com.codeexamples.java.async;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * The problem in this code whenever you reached max thread limit configured for executor, your connections are queued.
 * Even though connection is not doing anything. It will wait up till
 *
 *
 */
public class MultithreadedBlockingServer {

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(8080);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) executorService;
            System.out.println("Queued task count " + tpe.getQueue().size());
        },0,2000, TimeUnit.MILLISECONDS);
        while(true) {
            System.out.println("Waiting for connection");
            final Socket socket = serverSocket.accept();
            System.out.println("ManySockets connected " + socket);
            executorService.submit(() -> {
                try(final InputStream inputStream = socket.getInputStream();
                    final OutputStream os = socket.getOutputStream()
                ) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while((line = reader.readLine()) != null) {
                        os.write(line.toUpperCase().getBytes());
                    }
                } catch(IOException e) {
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
