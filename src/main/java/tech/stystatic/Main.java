package tech.stystatic;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {
    public static AsynchronousSocketChannel client;
    public static AsynchronousServerSocketChannel server;
    public static boolean clientConnected = false;
    static int port = 2832;
    static String ip = "0.0.0.0";

    public static void main(String[] args) {
        try {
            while (true) {
                if (!clientConnected) {
                    // Starts server
                    server = AsynchronousServerSocketChannel.open();
                    server.bind(new InetSocketAddress(ip, port));
                    System.out.println("Server started on port " + port);

                    // Accepts a new client
                    Future<AsynchronousSocketChannel> acceptCon = server.accept();
                    client = acceptCon.get();

                    if ((client != null) && (client.isOpen())) { // Checks for a connected client
                        // Log connections
                        System.out.println("New client connected: " + client.getRemoteAddress());
                        System.out.println("Connection Established");

                        // Start a thread to handle reading messages sent over socket
                        MessageReaderThread readThread = new MessageReaderThread(client);
                        readThread.start();

                        // Start an Event Handler that reads in game chat and sends to the client over sockets
                        clientConnected = true;
                    }
                    server.close();
                } else {
                    Thread.sleep(1000); // Wait to attempt starting new server
                }
            }
        } catch (IOException | ExecutionException | InterruptedException e) { // Error Handling
            e.printStackTrace();
            clientConnected = false;
            System.out.println("Connection Lost");
        }
    }
}