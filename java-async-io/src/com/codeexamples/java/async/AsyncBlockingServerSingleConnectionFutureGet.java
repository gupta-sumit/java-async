package com.codeexamples.java.async;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncBlockingServerSingleConnectionFutureGet {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
        asynchronousServerSocketChannel.bind(new InetSocketAddress(8090));
        Future<AsynchronousSocketChannel> accept = asynchronousServerSocketChannel.accept();
        AsynchronousSocketChannel asynchronousSocketChannel = accept.get();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        while(true) {
            Future<Integer> bytesRead = asynchronousSocketChannel.read(byteBuffer);
            Integer dataRead = bytesRead.get();
            if(dataRead == -1) {
                System.out.println("Closing socket " + asynchronousSocketChannel);
                asynchronousSocketChannel.close();
                break;
            } else if(dataRead != 0) {
                byteBuffer.flip();
                asynchronousSocketChannel.write(upperCase(byteBuffer)).get();
                byteBuffer.clear();
            }
        }
    }

    private static ByteBuffer upperCase(ByteBuffer byteBuffer) throws UnsupportedEncodingException {
        byte [] buff = new byte[byteBuffer.limit()];
        for(int i=0; i < byteBuffer.limit(); i++) {
            buff[i] = byteBuffer.get(i);
        }
        return ByteBuffer.wrap(new String(buff,"UTF-8").toUpperCase().getBytes());
    }
}
