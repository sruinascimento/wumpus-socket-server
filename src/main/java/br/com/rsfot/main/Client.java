package br.com.rsfot.main;

import java.io.*;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 7373;
    public static void main(String[] args) {
        try (Socket client = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))) {
            System.out.println("Connected to server: ");
            client.getInputStream().transferTo(System.out);

            String message = "Hello from the client!";
            writer.write(message);
            writer.newLine();
            writer.flush();
            System.out.println("Message sent to server: " + message);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
