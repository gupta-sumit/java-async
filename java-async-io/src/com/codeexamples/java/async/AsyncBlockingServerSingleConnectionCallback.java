package com.codeexamples.java.async;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncBlockingServerSingleConnectionCallback {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
        asynchronousServerSocketChannel.bind(new InetSocketAddress(8090));

        asynchronousServerSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

            @Override
            public void completed(AsynchronousSocketChannel asynchronousSocketChannel, Object attachment) {
                if(asynchronousServerSocketChannel.isOpen()) {
                    asynchronousServerSocketChannel.accept(attachment,this);
                }
                System.out.println( Thread.currentThread() + " " + "Connection accepted . " + asynchronousSocketChannel);
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                while(true) {
                    Future<Integer> bytesRead = asynchronousSocketChannel.read(byteBuffer);
                    Integer dataRead = null;
                    try {
                        dataRead = bytesRead.get();
                        if(dataRead == -1) {
                            asynchronousSocketChannel.close();
                            break;
                        } else if(dataRead != 0) {
                            byteBuffer.flip();
                            asynchronousSocketChannel.write(upperCase(byteBuffer)).get();
                            byteBuffer.clear();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {

            }
        });
        System.in.read();

    }

    private static ByteBuffer upperCase(ByteBuffer byteBuffer) throws UnsupportedEncodingException {
        byte [] buff = new byte[byteBuffer.limit()];
        for(int i=0; i < byteBuffer.limit(); i++) {
            buff[i] = byteBuffer.get(i);
        }
        return ByteBuffer.wrap(new String(buff,"UTF-8").toUpperCase().getBytes());
    }
}
