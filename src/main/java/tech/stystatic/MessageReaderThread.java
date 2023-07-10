package tech.stystatic;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MessageReaderThread extends Thread {
    final AsynchronousSocketChannel client;

    public MessageReaderThread(AsynchronousSocketChannel client) {
        this.client = client;
    }

    public void run() {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter a command without the /");

        while (true) {
            String s = in.nextLine(); // Blocking
            try {
                sendMessageAcrossSocket(client, s);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static void sendMessageAcrossSocket(AsynchronousSocketChannel client, String str) throws ExecutionException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(str.getBytes()); // Put the string into the buffer
        buffer.flip(); // Reset the position and limit of the buffer
        Future<Integer> writeval = client.write(buffer);
        System.out.println("Writing to socket: " + str);
        buffer.clear(); // Reset the buffer for reuse
        writeval.get();
    }
}
