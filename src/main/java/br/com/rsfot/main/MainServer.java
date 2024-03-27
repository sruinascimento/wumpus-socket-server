package br.com.rsfot.main;

import br.com.rsfot.domain.Direction;
import br.com.rsfot.domain.Environment;
import br.com.rsfot.report.ReportGenerator;
import br.com.rsfot.game.HuntWumpus;
import br.com.rsfot.util.MatrixFormatter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {
    private static final int PORT = 7373;
    public static Environment environment = new Environment(4);

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
            HuntWumpus huntWumpus = new HuntWumpus(environment);
            out.println(report(huntWumpus));
            out.println(menu());
            processClientCommands(out, in, socket, huntWumpus);

        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
        }
    }

    private static void processClientCommands(PrintWriter out, BufferedReader in, Socket socket, HuntWumpus huntWumpus) throws IOException {
        String clientResponse;

        while (!(clientResponse = in.readLine()).equals("4")) {
            System.out.println("Client %s response: %s".formatted(socket.getRemoteSocketAddress(), clientResponse));
            String serverResponse = processCommand(clientResponse.trim(), huntWumpus);
            out.println(serverResponse);

            if (huntWumpus.isGameOver()) {
                out.println();
            }
        }
        socket.close();
    }

    private static String processCommand(String command, HuntWumpus huntWumpus) {
        if (command.contains("1")) {
            Direction directionToForward = Direction.valueOf(command.split(" ")[1].toUpperCase());
            huntWumpus.moveToDirection(directionToForward);
        } else if (command.equals("2")) {
            huntWumpus.grabGold();
        } else if (command.equals("3")) {
            huntWumpus.shoot();
        } else if (command.equals("4")) {
            return ReportGenerator.generate(huntWumpus);
        } else {
            return "Invalid command";
        }
        return report(huntWumpus);
    }

    private static String report(HuntWumpus huntWumpus) {
        return MatrixFormatter.format(huntWumpus.getEnvironment().getCave(), 1, 1)
                + "\n\n\n" +
                ReportGenerator.generate(huntWumpus);
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
