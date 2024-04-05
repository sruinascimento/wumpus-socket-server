package br.com.rsfot;

import br.com.rsfot.domain.Environment;
import br.com.rsfot.handler.HandlerClient;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class MainServer {
    private static final Logger logger = Logger.getLogger(MainServer.class.getName());
    private static final int PORT = 7373;
    public static final Environment environment = new Environment(4);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("""
                    Server started on port %s
                    Waiting for clients...
                    """.formatted(PORT));

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> HandlerClient.handleClient(socket, environment)).start();
            }
        } catch (Exception e) {
            logger.severe("There's an error: " + e.getMessage());
        }
    }
}
