package br.com.rsfot.train;

import br.com.rsfot.domain.Agent;
import br.com.rsfot.domain.Environment;
import br.com.rsfot.domain.Feelings;
import br.com.rsfot.game.HuntWumpus;
import br.com.rsfot.report.Report;

import java.io.IOException;
import java.util.*;

import org.json.JSONObject;

import static br.com.rsfot.domain.Direction.*;
import static br.com.rsfot.domain.Feelings.*;
import static br.com.rsfot.domain.Feelings.GLITTER;

public class EpisodeGenerator {
    private HuntWumpus game;
    private final Random random;
    private List<TrainObject> trainObjectList = new ArrayList<>();

    public EpisodeGenerator(Environment environment) {
        this.random = new Random();
        this.game = new HuntWumpus(environment);
    }

    public List<TrainObject> generateEpisodes(int numberOfEpisodes) {
        for (int i = 0; i < numberOfEpisodes; i++) {
            System.out.println("Episode " + (i + 1));


            //setup random coordinate to the agent
            Agent agent = new Agent();
            int[] coordinate = generateCoordinate();
            //2, 0 tem ouro
            agent.setCoordinateX(coordinate[0]);
            agent.setCoordinateY(coordinate[1]);
            game.setAgent(agent);


            boolean impact = false;

            while (agent.isAlive() && !game.isGameOver()) {
                String nextAction = generateReactiveMovement();
                String currentState = Report.generate(game, impact);
                trainObjectList.add(createTrainObject(impact, nextAction, currentState));
                impact = executeAction(nextAction);

            }

            trainObjectList.add(createTrainObject(impact, game.getAgent().agentWinTheGame() ? "AGENT WIN" : "AGENT LOSE", Report.generate(game, impact)));

            game.resetGame();
        }

        return trainObjectList;
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

    private TrainObject createTrainObject(boolean impact, String action, String agentState) {

        final var instruction = """
                You are the best and most intelligent agent in the Wumpus World. Your goal is to find the gold and return to the starting position.
                Below is your current state and the sensations you are feeling in your position. Based on this, decide what action to take next to maximize your chances of success.
                You respond with only one action (e.g., MOVE NORTH, SHOOT SOUTH, GRAB).
                The moves you can use are: move north, move south, move east, move west, shoot north, shoot south, shoot east, shoot west, grab.
                The feelings you can feel are: breeze, stench, glitter, impact.
                Explanation of the feelings: breeze indicates that there is a pit in an adjacent house. Stench indicates that the Wumpus is in an adjacent house. Glitter indicates that you are in the house that contains the gold. Impact indicates that you collided with the matrix wall.
                You can only shoot if you have an arrow.
                ----
                Reply with the action you want to take next. the format should be `<next action>`.
                Example:
                `move north`
                `shoot east`
                `grab`
                """;

        JSONObject jsonObject = new JSONObject(agentState);
        JSONObject agentStatus = jsonObject.getJSONObject("agentStatus");
        JSONObject feelings = jsonObject.getJSONObject("feelingByCoordinate");

        final var input = String.format(
                "Agent's Current State:\n" +
                        " - Coordinate: %s\n" +
                        " - Gold: %s\n" +
                        " - Arrow: %s\n" +
                        " - Alive: %s\n\n" +
                        "Feelings by Coordinate:\n" +
                        "- Breeze: %s\n" +
                        "- Stench: %s\n" +
                        "- Glitter: %s\n" +
                        "- Impact: %s\n\n" +
                        "- Wumpus Dead: %s\n\n" +
                        "What's your next movement?",
                agentStatus.getString("coordinate"),
                agentStatus.getBoolean("hasGold") ? "yes" : "no",
                agentStatus.getBoolean("hasArrow") ? "yes" : "no",
                agentStatus.getBoolean("isAlive") ? "yes" : "no",
                feelings.getBoolean("breeze") ? "yes" : "no",
                feelings.getBoolean("stench") ? "yes" : "no",
                feelings.getBoolean("glitter") ? "yes" : "no",
                impact ? "yes" : "no",
                jsonObject.getBoolean("wumpusDead") ? "yes" : "no"
        );

        final var output = action;

        return new TrainObject(instruction, input, output);
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

    private void recordEpisode(List<TrainObject> trainObjectList) {
        try {
            JsonFileWriter.writeTrainObjectListToJsonFile(trainObjectList, "train_data_set4x4.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<Environment> environments4x4 = EnvironmentManagerRandomGenerator.loadEnvironmentsFromFile("environments4x4.dat");
        List<TrainObject> trainObjectList = new ArrayList<>();

        for (Environment environment: environments4x4) {
            EpisodeGenerator generator = new EpisodeGenerator(environment);
            trainObjectList.addAll(generator.generateEpisodes(15));

        }

        try {
            JsonFileWriter.writeTrainObjectListToJsonFile(trainObjectList, "train_data_set4x4.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(trainObjectList.size());
    }
}