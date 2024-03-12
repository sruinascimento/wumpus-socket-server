package br.com.rsfot.main;

import br.com.rsfot.domain.EnvironmentFeelings;
import br.com.rsfot.game.HuntWumpus;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

import static br.com.rsfot.domain.Rotation.LEFT;
import static br.com.rsfot.domain.Rotation.RIGHT;

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
        while (!(clientResponse = in.readLine()).equals("6")) {
            System.out.println("Client %s response: %s".formatted(socket.getRemoteSocketAddress(), clientResponse));
            String serverResponse = processCommand(clientResponse.trim());
            out.println(serverResponse);
            out.println(menu());
        }
    }

    private static String processCommand(String command) {
        String actionResult = "Last movement: ";
        switch (command) {
            case "1":
                huntWumpus.turnAgentTo(LEFT);
                actionResult += "Turned left";
                break;
            case "2":
                huntWumpus.turnAgentTo(RIGHT);
                actionResult += "Turned right";
                break;
            case "3":
                huntWumpus.moveForward();
                actionResult += "Moved forward";
                break;
            case "4":
                huntWumpus.grabGold();
                actionResult += "Grabbed Gold";
                break;
            case "5":
                huntWumpus.shoot();
                actionResult += "Shot";
                break;
            default:
                return "Invalid command";
        }

        String agentStatus = huntWumpus.getAgent().toString();
        Set<EnvironmentFeelings> environmentFeelings = huntWumpus.getEnvironment().getFeelingsByCoordinate().get(huntWumpus.getAgent().getStringCoordinate());
        return actionResult + "\n" + agentStatus + "\n" + environmentFeelings;

//        return actionResult + "\n" + agentStatus + "\n" + feelings;
    }

    private static String menu() {
        return """
                1 - Turn left
                2 - Turn right
                3 - Move forward
                4 - Grab Gold
                5 - Shoot
                6 - Exit
                """;
    }
}
