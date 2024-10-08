package br.com.rsfot.train;

import br.com.rsfot.domain.Agent;
import br.com.rsfot.domain.Environment;
import br.com.rsfot.domain.Feelings;
import br.com.rsfot.game.HuntWumpus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static br.com.rsfot.domain.Direction.*;
import static br.com.rsfot.domain.Feelings.*;

public class EpisodeGeneratorNeuralNetWork {
    private HuntWumpus game;
    private final Random random;
    private List<TrainObjectNN> trainObjectList = new ArrayList<>();
    private Map<String, List<Integer>> outputBinaryRepresentationByAction = Map.of(
            "GRAB", List.of(1, 0, 0, 0, 0, 0, 0, 0, 0),
            "MOVE NORTH", List.of(0, 1, 0, 0, 0, 0, 0, 0, 0),
            "MOVE SOUTH", List.of(0, 0, 1, 0, 0, 0, 0, 0, 0),
            "MOVE EAST", List.of(0, 0, 0, 1, 0, 0, 0, 0, 0),
            "MOVE WEST", List.of(0, 0, 0, 0, 1, 0, 0, 0, 0),
            "SHOOT NORTH", List.of(0, 0, 0, 0, 0, 1, 0, 0, 0),
            "SHOOT SOUTH", List.of(0, 0, 0, 0, 0, 0, 1, 0, 0),
            "SHOOT EAST", List.of(0, 0, 0, 0, 0, 0, 0, 1, 0),
            "SHOOT WEST", List.of(0, 0, 0, 0, 0, 0, 0, 0, 1),
            "NO ACTION", List.of(0, 0, 0, 0, 0, 0, 0, 0, 0));

    public EpisodeGeneratorNeuralNetWork(Environment environment) {
        this.random = new Random();
        this.game = new HuntWumpus(environment);
    }

    public List<TrainObjectNN> generateEpisodes(int numberOfEpisodes) {
        for (int i = 0; i < numberOfEpisodes; i++) {
            System.out.println("Episode " + (i + 1));


            //setup random coordinate to the agent
            Agent agent = new Agent();
            int[] coordinate = generateCoordinate();
            agent.setCoordinateX(coordinate[0]);
            agent.setCoordinateY(coordinate[1]);
            game.setAgent(agent);


            boolean impact = false;

            int count = 0;
            while ((agent.isAlive() && !game.isGameOver()) && count < 10) {
                String nextAction = generateReactiveMovement();
                List<Integer> currentStateInVectorRepresentation = generateVectorRepresentationOfAgentState(game, impact);
                trainObjectList.add(new TrainObjectNN(currentStateInVectorRepresentation, outputBinaryRepresentationByAction.get(nextAction)));
                impact = executeAction(nextAction);
                count++;
            }
            if (!agent.isAlive()) {
                trainObjectList.add(new TrainObjectNN(generateVectorRepresentationOfAgentState(game, impact), outputBinaryRepresentationByAction.get("NO ACTION")));
            }


            game.resetGame();
        }

        return trainObjectList;
    }

    private List<Integer> generateVectorRepresentationOfAgentState(HuntWumpus huntWumpus, boolean impact) {
        int coordinateX = huntWumpus.getAgent().getCoordinateX();
        int coordinateY = huntWumpus.getAgent().getCoordinateY();
        int isAlive = huntWumpus.getAgent().isAlive() ? 1 : 0;
        int hasGold = huntWumpus.getAgent().hasGold() ? 1 : 0;
        int hasArrow = huntWumpus.getAgent().hasArrow() ? 1 : 0;
        int isWumpusAlive = huntWumpus.getAgent().isKilledTheWumpus() ? 0 : 1;
        int breeze = huntWumpus.getEnvironment().getFeelingsByCoordinate().get(huntWumpus.getAgent().getStringCoordinate()).contains(BREEZE) ? 1 : 0;
        int stench = huntWumpus.getEnvironment().getFeelingsByCoordinate().get(huntWumpus.getAgent().getStringCoordinate()).contains(STENCH) ? 1 : 0;
        int glitter = huntWumpus.getEnvironment().getFeelingsByCoordinate().get(huntWumpus.getAgent().getStringCoordinate()).contains(GLITTER) && !huntWumpus.getAgent().hasGold() ? 1 : 0;
        int impactValue = impact ? 1 : 0;

        return List.of(
                coordinateX,
                coordinateY,
                isAlive,
                hasGold,
                hasArrow,
                isWumpusAlive,
                breeze,
                stench,
                glitter,
                impactValue
        );
    }

    private int[] generateCoordinate() {
        while (true) {
            int x = random.nextInt(game.getEnvironment().getDimension());
            int y = random.nextInt(game.getEnvironment().getDimension());
            if (game.getEnvironment().isThereAPitAt(x, y) || game.getEnvironment().isThereAWumpusAt(x, y)) {
                continue;
            }
            return new int[]{x, y};
        }
    }

    private String generateReactiveMovement() {
        String[] actionWithFeet = {"MOVE NORTH", "MOVE SOUTH", "MOVE EAST", "MOVE WEST"};
        String[] actionWithHands = {"SHOOT NORTH", "SHOOT SOUTH", "SHOOT EAST", "SHOOT WEST"};

        List<Feelings> currentFeelingsFromAgent = game.getCurrentFeelingsFromAgent();

        if (currentFeelingsFromAgent.isEmpty()) {
            return actionWithFeet[random.nextInt(actionWithFeet.length)];
        }

        if (currentFeelingsFromAgent.contains(GLITTER) && !game.getAgent().hasGold()) {
            return "GRAB";
        }

        if (currentFeelingsFromAgent.contains(STENCH)) {
            if (random.nextInt() * 100 < 20) {
                return actionWithHands[random.nextInt(actionWithHands.length)];
            }

            return actionWithFeet[random.nextInt(actionWithFeet.length)];
        }

        return actionWithFeet[random.nextInt(actionWithFeet.length)];

    }

    private boolean executeAction(String action) {
        final var NOT_IMPACT = false;
        return switch (action) {
            case "MOVE NORTH" -> !game.moveToDirection(NORTH);
            case "MOVE SOUTH" -> !game.moveToDirection(SOUTH);
            case "MOVE EAST" -> !game.moveToDirection(EAST);
            case "MOVE WEST" -> !game.moveToDirection(WEST);
            case "SHOOT NORTH" -> {
                game.shoot(NORTH);
                yield NOT_IMPACT;
            }
            case "SHOOT SOUTH" -> {
                game.shoot(SOUTH);
                yield NOT_IMPACT;
            }
            case "SHOOT EAST" -> {
                game.shoot(EAST);
                yield NOT_IMPACT;
            }
            case "SHOOT WEST" -> {
                game.shoot(WEST);
                yield NOT_IMPACT;
            }
            case "GRAB" -> {
                game.grabGold();
                yield NOT_IMPACT;
            }
            default -> throw new IllegalArgumentException("Action invalid");
        };
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //Classe usada para gerar episódios para treinamento da rede neural

        List<TrainObjectNN> trainObjectNNList = new ArrayList<>();
        List<String[][]> environments = loadEnvironmentsFromFile("environments_7x7_400_train.json");


        for (String[][] cave: environments) {
            Environment environment = new Environment(cave);
            EpisodeGeneratorNeuralNetWork generator = new EpisodeGeneratorNeuralNetWork(environment);
            trainObjectNNList.addAll(generator.generateEpisodes(15));
        }

        System.out.println(trainObjectNNList.size());

//        try {
//            JsonFileWriter.writeTrainObjectNNListToJsonFile(trainObjectNNList, "nn_episodes7x7_train.json");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }

    public static List<String[][]> loadEnvironmentsFromFile(String pathFile) throws IOException {
        return MatrixUtils.loadMatricesFromFile(pathFile);
    }
}