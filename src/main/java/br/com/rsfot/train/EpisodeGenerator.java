package br.com.rsfot.train;

import br.com.rsfot.domain.Agent;
import br.com.rsfot.domain.Environment;
import br.com.rsfot.domain.Feelings;
import br.com.rsfot.game.HuntWumpus;
import br.com.rsfot.report.Report;

import java.io.IOException;
import java.util.*;

import static br.com.rsfot.domain.Direction.*;
import static br.com.rsfot.domain.Feelings.*;
import static br.com.rsfot.domain.Feelings.GLITTER;

public class EpisodeGenerator {
    private HuntWumpus game;
    private final Random random;

    public EpisodeGenerator(Environment environment) {
        this.random = new Random();
        this.game = new HuntWumpus(environment);
    }

    public void generateEpisodes(int numberOfEpisodes) {
//        for (int i = 0; i < numberOfEpisodes; i++) {
        System.out.println("Episode " + 1);


        //setup random coordinate to the agent
        Agent agent = new Agent();
        int[] coordinate = generateCoordinate();
        agent.setCoordinateX(coordinate[0]);
        agent.setCoordinateY(coordinate[1]);
        game.setAgent(agent);




        while (agent.isAlive()) {
            String nextAction = generateReactiveMovement();
            String currentStateAndAction = executeAction(nextAction);
            System.out.println(currentStateAndAction);
        }


        game.resetGame();
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

    private String getCurrentState(boolean impact, String action) {
        return String.format("Agent's Current State:\n" +
                        "        - Coordinate: %s\n" +
                        "        - Gold: %s\n" +
                        "        - Arrow: %s\n" +
                        "        - Alive: %s\n\n" +
                        "Feelings by Coordinate:\n" +
                        "- Breeze: %s\n" +
                        "- Stench: %s\n" +
                        "- Glitter: %s\n" +
                        "- Impact: %s\n\n" +
                        "- Wumpus Dead: %s\n\n" +
                        "What's your next movement? \n" +
                        "Response: %s ",
                game.getAgent().getStringCoordinate(),
                game.getAgent().hasGold() ? "yes" : "no",
                game.getAgent().hasArrow() ? "yes" : "no",
                game.getAgent().isAlive() ? "yes" : "no",
                game.getEnvironment().getFeelingsByCoordinate().get(game.getAgent().getStringCoordinate()).contains(BREEZE) ? "yes" : "no",
                game.getEnvironment().getFeelingsByCoordinate().get(game.getAgent().getStringCoordinate()).contains(STENCH) ? "yes" : "no",
                game.getEnvironment().getFeelingsByCoordinate().get(game.getAgent().getStringCoordinate()).contains(GLITTER) ? "yes" : "no",
                impact ? "yes" : "no",
                game.getAgent().isKilledTheWumpus() ? "yes" : "no",
                action);
    }


    private String executeAction(String action) {
        return switch (action) {
            case "MOVE NORTH" -> getCurrentState(game.moveToDirection(NORTH), action);
            case "MOVE SOUTH" -> getCurrentState(game.moveToDirection(SOUTH), action);
            case "MOVE EAST" -> getCurrentState(game.moveToDirection(EAST), action);
            case "MOVE WEST" -> getCurrentState(game.moveToDirection(WEST), action);
            case "SHOOT NORTH" -> {
                game.shoot(NORTH);
                yield getCurrentState(false, action);
            }
            case "SHOOT SOUTH" -> {
                game.shoot(SOUTH);
                yield getCurrentState(false, action);
            }
            case "SHOOT EAST" -> {
                game.shoot(EAST);
                yield getCurrentState(false, action);
            }
            case "SHOOT WEST" -> {
                game.shoot(WEST);
                yield getCurrentState(false, action);
            }
            case "GRAB" -> {
                game.grabGold();
                yield getCurrentState(false, action);
            }
            default -> throw new IllegalArgumentException("Action invalid");
        };
    }

    private void recordEpisode(String state, String action, String newState) {
        // Implement the logic to record the episode, e.g., save to a file or database
        System.out.println("State: " + state);

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<Environment> environments4x4 = EnvironmentManagerRandomGenerator.loadEnvironmentsFromFile("environments4x4.dat");

        EpisodeGenerator generator = new EpisodeGenerator(environments4x4.get(0));
        generator.generateEpisodes(1);
//        generator.generateEpisodes(10); // Generate 10 episodes
    }
}