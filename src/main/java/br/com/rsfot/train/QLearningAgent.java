package br.com.rsfot.train;

import br.com.rsfot.domain.Agent;
import br.com.rsfot.domain.Direction;
import br.com.rsfot.domain.Environment;
import br.com.rsfot.game.HuntWumpus;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static br.com.rsfot.domain.Direction.*;
import static br.com.rsfot.domain.Direction.WEST;
import static br.com.rsfot.domain.Feelings.*;

public class QLearningAgent {
    private final double learningRate;
    private final double discountFactor;
    private final double explorationRate;
    private final Random random;
    private final Map<List<Integer>, Map<String, Double>> qTable;
    private final List<String> possibleActions = List.of("GRAB",
            "MOVE NORTH",
            "MOVE SOUTH",
            "MOVE EAST",
            "MOVE WEST",
            "SHOOT NORTH",
            "SHOOT SOUTH",
            "SHOOT EAST",
            "SHOOT WEST");

    public QLearningAgent(double learningRate, double discountFactor, double explorationRate) {
        this.learningRate = learningRate;
        this.discountFactor = discountFactor;
        this.explorationRate = explorationRate;
        this.random = new Random();
        this.qTable = new HashMap<>();
    }

    public String chooseAction(List<Integer> state, List<String> possibleActions) {
        if (random.nextDouble() < explorationRate) {
            return possibleActions.get(random.nextInt(possibleActions.size()));
        }
        return qTable.getOrDefault(state, new HashMap<>())
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(possibleActions.get(random.nextInt(possibleActions.size())));
    }

    public void updateQTable(List<Integer> state, String action, double reward, List<Integer> nextState) {
        double oldQValue = qTable.getOrDefault(state, new HashMap<>()).getOrDefault(action, 0.0);
        double nextMaxQValue = qTable.getOrDefault(nextState, new HashMap<>())
                .values()
                .stream()
                .max(Double::compare)
                .orElse(0.0);
        double newQValue = oldQValue + learningRate * (reward + discountFactor * nextMaxQValue - oldQValue);
        qTable.computeIfAbsent(state, k -> new HashMap<>()).put(action, newQValue);
    }

    public void saveQTableDat(String filePath) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(qTable);
        }
    }

    public void train(Environment environment, int episodes) {
        HuntWumpus game = new HuntWumpus(environment);
        boolean impact = false;
        for (int i = 0; i < episodes; i++) {
            Agent agent = new Agent();
            int[] coordinate = generateCoordinate(game);
            agent.setCoordinateX(coordinate[0]);
            agent.setCoordinateY(coordinate[1]);
            game.setAgent(agent);
            List<Integer> state = generateVectorRepresentationOfAgentState(game, impact);
            int actionsByEpisode = 0;
            double maxActionsByEpisode = Math.pow(environment.getDimension(), 2) * 2;
            while (agent.isAlive() && !game.isGameOver() && actionsByEpisode < maxActionsByEpisode) {
                String action = chooseAction(state, possibleActions);
                List<Object> rewardAndImpact = executeAction(action, game);
                impact = (boolean)rewardAndImpact.getLast();
                List<Integer> nextState = generateVectorRepresentationOfAgentState(game, impact);
                updateQTable(state, action, (double)rewardAndImpact.getFirst(), nextState);
                state = nextState;
                actionsByEpisode++;
            }
            game.resetGame();
        }
    }

    private int[] generateCoordinate(HuntWumpus game) {
        while (true) {
            int x = random.nextInt(game.getEnvironment().getDimension());
            int y = random.nextInt(game.getEnvironment().getDimension());
            if (game.getEnvironment().isThereAPitAt(x, y) || game.getEnvironment().isThereAWumpusAt(x, y)) {
                continue;
            }
            return new int[]{x, y};
        }
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

    private List<Object> executeAction(String action, HuntWumpus game) {
        double reward = 0.0;
        boolean impact;

        switch (action) {
            case "MOVE NORTH" -> {
                var result = handleMoveAction(game, NORTH, reward);
                impact = result.impact;
                reward = result.reward;
            }
            case "MOVE SOUTH" -> {
                var result = handleMoveAction(game, SOUTH, reward);
                impact = result.impact;
                reward = result.reward;
            }
            case "MOVE EAST" -> {
                var result = handleMoveAction(game, EAST, reward);
                impact = result.impact;
                reward = result.reward;
            }
            case "MOVE WEST" -> {
                var result = handleMoveAction(game, WEST, reward);
                impact = result.impact;
                reward = result.reward;
            }
            case "SHOOT NORTH" -> {
                var result = handleShootAction(game, NORTH, reward);
                impact = result.impact;
                reward = result.reward;
            }
            case "SHOOT SOUTH" -> {
                var result = handleShootAction(game, SOUTH, reward);
                impact = result.impact;
                reward = result.reward;
            }
            case "SHOOT EAST" -> {
                var result = handleShootAction(game, EAST, reward);
                impact = result.impact;
                reward = result.reward;
            }
            case "SHOOT WEST" -> {
                var result = handleShootAction(game, WEST, reward);
                impact = result.impact;
                reward = result.reward;
            }
            case "GRAB" -> {
                var result = handleGrabAction(game, reward);
                impact = result.impact;
                reward = result.reward;
            }
            default -> throw new IllegalArgumentException("Action invalid");
        }

        if (impact) {
            reward -= 500;
        }

        return List.of(reward, impact);
    }

    private double calculateReward(HuntWumpus game) {
        if (game.isAgentWinTheGame()) {
            return 1000; // Recompensa por vencer o jogo
        }
        if (!game.getAgent().isAlive()) {
            return -1000; // Penalidade por morrer
        }
        return -1; // Penalidade padrão por movimento
    }


    private static class ActionResult {
        double reward;
        boolean impact;

        ActionResult(double reward, boolean impact) {
            this.reward = reward;
            this.impact = impact;
        }
    }

    private ActionResult handleMoveAction(HuntWumpus game, Direction direction, double reward) {
        reward += calculateReward(game);
        boolean impact = !game.moveToDirection(direction);
        return new ActionResult(reward, impact);
    }

    private ActionResult handleShootAction(HuntWumpus game, Direction direction, double reward) {
        if (!game.getAgent().hasArrow() || !game.getCurrentFeelingsFromAgent().contains(STENCH)) {
            reward -= 500;
        }
        game.shoot(direction);
        reward -= 10;
        if (game.getAgent().isKilledTheWumpus()) {
            reward += 500; // Recompensa por matar o Wumpus
        }
        return new ActionResult(reward, false);
    }

    private ActionResult handleGrabAction(HuntWumpus game, double reward) {
        if (!game.getAgent().hasGold() && game.getCurrentFeelingsFromAgent().contains(GLITTER)) {
            reward += 1000;
        } else {
            reward -= 500;
        }
        game.grabGold();
        return new ActionResult(reward, false);
    }

    public static void main(String[] args) throws IOException {
        List<String[][]> environments = MatrixUtils.loadMatricesFromFile("environments_4x4_160_train.json");
        QLearningAgent agent = new QLearningAgent(0.1, 0.9, 0.1);
        for (String[][] cave : environments) {
            Environment environment = new Environment(cave);
            agent.train(environment, 2000);
        }
        agent.saveQTableDat("qTable4x4_alpha01_gamma09_explorationRate01_episodes2k_train_001.dat");


    }

}