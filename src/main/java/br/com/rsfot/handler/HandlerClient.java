package br.com.rsfot.handler;

import br.com.rsfot.domain.Environment;
import br.com.rsfot.domain.action.ProcessCommand;
import br.com.rsfot.game.HuntWumpus;
import br.com.rsfot.report.Report;
import br.com.rsfot.util.MatrixFormatter;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class HandlerClient {
    private static final Logger logger = Logger.getLogger(HandlerClient.class.getName());
    public static void handleClient(Socket socket, Environment environment) {
        logger.info("Client connected: " + socket.getRemoteSocketAddress());
        try (OutputStream outputStream = socket.getOutputStream();
             PrintWriter out = new PrintWriter(outputStream, true);
             InputStream inputStream = socket.getInputStream();
             BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
            HuntWumpus huntWumpus = new HuntWumpus(environment);
            out.println(Report.generate(huntWumpus, false));
            processClientCommands(out, in, socket, huntWumpus);
        } catch (IOException e) {
            logger.severe("Error handling client: " + e.getMessage());
        } finally {
            logger.info("Client disconnected: " + socket.getRemoteSocketAddress());
        }
    }

    private static void processClientCommands(PrintWriter out, BufferedReader in, Socket socket, HuntWumpus huntWumpus) throws IOException {
        String clientResponse;

        while (!(clientResponse = in.readLine()).equals("4")) {
            logger.info("Client %s response: %s".formatted(socket.getRemoteSocketAddress(), clientResponse));
            //TODO: remover esse comentário pois representa a matrix do jogo com as informações
//            out.println(MatrixFormatter.format(huntWumpus.getEnvironment().getCave(), 4, 1));
            System.out.println(MatrixFormatter.format(huntWumpus.getEnvironment().getCave(), 4, 1));
            String serverResponse = ProcessCommand.from(clientResponse.trim(), huntWumpus);
            out.println(serverResponse);
            if (huntWumpus.isGameOver()) {
                break;
            }
        }
        out.println("Gamer Over!");
        socket.close();
    }
}
