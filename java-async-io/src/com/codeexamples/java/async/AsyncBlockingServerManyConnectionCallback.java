package com.codeexamples.java.async;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.*;

public class AsyncBlockingServerManyConnectionCallback {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadScheduledExecutor());
        AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open(group);
        asynchronousServerSocketChannel.bind(new InetSocketAddress(8090));
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        while(true) {
            asynchronousServerSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

                @Override
                public void completed(AsynchronousSocketChannel asynchronousSocketChannel, Object attachment) {
                    if(asynchronousServerSocketChannel.isOpen()) {
                        asynchronousServerSocketChannel.accept(attachment,this);
                    }

                    System.out.println( Thread.currentThread() + " " + "Connection accepted . " + asynchronousSocketChannel);

                    Attachment socketReadAttachment = new Attachment();
                    socketReadAttachment.channel = asynchronousSocketChannel;
                    socketReadAttachment.byteBuffer = ByteBuffer.allocate(1024);
                    asynchronousSocketChannel.read(socketReadAttachment.byteBuffer, socketReadAttachment, new CompletionHandler<Integer,Attachment>() {

                        StringBuilder strBuffer = new StringBuilder();

                        @Override
                        public void completed(Integer result, final Attachment attachment) {
                                System.out.println(Thread.currentThread() + "  Bytes read " + result);
                                final AsynchronousSocketChannel channel = attachment.channel;
                                final ByteBuffer byteBuffer = attachment.byteBuffer;
                                if(result == -1) {
                                    System.out.println("Closing socket " + channel);
                                    try {
                                        channel.close();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                } else if(result != 0 ) {
                                    CompletableFuture.runAsync(() -> {

                                        byteBuffer.flip();
                                        String str;
                                        try {
                                            str = readAsString(byteBuffer);
                                            System.out.println(Thread.currentThread() + "  Received String " + str);
                                            if (str.equals("EOF\r\n")) {
                                                System.out.println("Request Completed ");
                                                System.out.println("Processing Request ");
                                                Thread.sleep(10000);
                                                channel.write(ByteBuffer.wrap(strBuffer.toString().toUpperCase().getBytes())).get();
                                                strBuffer.setLength(0);
                                            } else {
                                                strBuffer.append(str);
                                                byteBuffer.clear();
                                            }
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        } catch (InterruptedException e1) {
                                            e1.printStackTrace();
                                        } catch (ExecutionException e1) {
                                            e1.printStackTrace();
                                        }


                                    },executorService).thenRun(() -> {
                                        channel.read(byteBuffer, attachment, this);
                                    });
                                }
                        }

                        @Override
                        public void failed(Throwable exc, Attachment attachment) {
                            exc.printStackTrace();
                        }
                    });
                    System.out.println("Connection read callback registered");
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    exc.printStackTrace();
                }
            });
            System.in.read();
        }

    }

    private static class Attachment {
        AsynchronousSocketChannel channel;
        ByteBuffer byteBuffer;

    }


    private static String readAsString(ByteBuffer byteBuffer) throws UnsupportedEncodingException {
        byte [] buff = new byte[byteBuffer.limit()];
        for(int i=0; i < byteBuffer.limit(); i++) {
            buff[i] = byteBuffer.get(i);
        }
        return new String(buff,"UTF-8");
    }

    private static ByteBuffer upperCase(ByteBuffer byteBuffer) throws UnsupportedEncodingException {
        byte [] buff = new byte[byteBuffer.limit()];
        for(int i=0; i < byteBuffer.limit(); i++) {
            buff[i] = byteBuffer.get(i);
        }
        return ByteBuffer.wrap(new String(buff,"UTF-8").toUpperCase().getBytes());
    }

}
