package br.com.rsfot.main;

import br.com.rsfot.domain.Direction;
import br.com.rsfot.domain.Feelings;
import br.com.rsfot.game.HuntWumpus;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

public class MainServer {
    private static final int PORT = 7373;
    private static HuntWumpus huntWumpus = new HuntWumpus();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server waiting for client on port " + serverSocket.getLocalPort());
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        System.out.println("Client connected: " + socket.getRemoteSocketAddress());
        try (OutputStream outputStream = socket.getOutputStream();
             PrintWriter out = new PrintWriter(outputStream, true);
             InputStream inputStream = socket.getInputStream();
             BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {

            out.println(menu());
            processClientCommands(out, in, socket);

        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
        }
    }

    private static void processClientCommands(PrintWriter out, BufferedReader in, Socket socket) throws IOException {
        String clientResponse;

        while (!(clientResponse = in.readLine()).equals("4") && !huntWumpus.isGameOver()) {
            System.out.println("Client %s response: %s".formatted(socket.getRemoteSocketAddress(), clientResponse));
            String serverResponse = processCommand(clientResponse.trim());
            out.println(serverResponse);
        }
        socket.close();
    }

    private static String processCommand(String command) {

        if (command.contains("1")) {
            Direction directionToForward = Direction.valueOf(command.split(" ")[1].toUpperCase());
            huntWumpus.moveToDirection(directionToForward);
        } else if (command.equals("2")) {
            huntWumpus.grabGold();
        } else if (command.equals("3")) {
            huntWumpus.shoot();
        } else if (command.equals("4")) {
            return reportOfTurn();
        } else {
            return "Invalid command";
        }
        return reportOfTurn();
    }

    private static String reportOfTurn() {
        return """
                                {
                                    "agent: {
                                        %s
                                    },
                                    "environment": [
                                        %s
                                    ] 
                                }
                                
                """.formatted(huntWumpus.getJsonOfAgent(),
                huntWumpus.getEnvironment().getJsonOfFeelingsByCoordinate(huntWumpus.getAgent().getStringCoordinate()));
    }

    private static String menu() {
        return """
                1 - Move to Direction
                2 - Grab Gold
                3 - Shoot
                4 - Exit
                 """;
    }
}
