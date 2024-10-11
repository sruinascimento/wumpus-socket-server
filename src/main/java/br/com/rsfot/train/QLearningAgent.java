package br.com.rsfot.train;

import br.com.rsfot.domain.Agent;
import br.com.rsfot.domain.Environment;
import br.com.rsfot.game.HuntWumpus;
import org.nd4j.shade.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
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

    public void saveQTable(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(filePath), qTable);
    }

    public void train(Environment environment, int episodes) {
        HuntWumpus game = new HuntWumpus(environment);
        boolean impact = false;
        for (int i = 0; i < episodes; i++) {
            Agent agent = new Agent();
            game.setAgent(agent);
            List<Integer> state = generateVectorRepresentationOfAgentState(game, impact);
            while (agent.isAlive() && !game.isGameOver()) {
                String action = chooseAction(state, possibleActions);
                List<Object> rewardAndImpact = executeAction(action, game);
                impact = (boolean)rewardAndImpact.getLast();
                List<Integer> nextState = generateVectorRepresentationOfAgentState(game, impact);
                updateQTable(state, action, (double)rewardAndImpact.getFirst(), nextState);
                state = nextState;
            }
            game.resetGame();
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
        final var NOT_IMPACT = false;
        double reward = 0.0;
        boolean impact = switch (action) {
            case "MOVE NORTH" -> {
                reward = calculateReward(game);
                yield !game.moveToDirection(NORTH);
            }
            case "MOVE SOUTH" -> {
                reward = calculateReward(game);
                yield !game.moveToDirection(SOUTH);
            }
            case "MOVE EAST" -> {
                reward = calculateReward(game);
                yield !game.moveToDirection(EAST);
            }
            case "MOVE WEST" -> {
                reward = calculateReward(game);
                yield !game.moveToDirection(WEST);
            }
            case "SHOOT NORTH" -> {
                game.shoot(NORTH);
                reward = -10;
                if (game.getAgent().isKilledTheWumpus()) {
                    reward = 500; // Recompensa por matar o Wumpus
                }
                yield NOT_IMPACT;
            }
            case "SHOOT SOUTH" -> {
                game.shoot(SOUTH);
                reward = -10;
                if (game.getAgent().isKilledTheWumpus()) {
                    reward = 500; // Recompensa por matar o Wumpus
                }
                yield NOT_IMPACT;
            }
            case "SHOOT EAST" -> {
                game.shoot(EAST);
                reward = -10;
                if (game.getAgent().isKilledTheWumpus()) {
                    reward = 500; // Recompensa por matar o Wumpus
                }
                yield NOT_IMPACT;
            }
            case "SHOOT WEST" -> {
                game.shoot(WEST);
                reward = -10;
                if (game.getAgent().isKilledTheWumpus()) {
                    reward = 500; // Recompensa por matar o Wumpus
                }
                yield NOT_IMPACT;
            }
            case "GRAB" -> {
                game.grabGold();
                if (game.getAgent().hasGold()) {
                    reward = 1000;
                }
                yield NOT_IMPACT;
            }
            default -> throw new IllegalArgumentException("Action invalid");
        };

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


    public static void main(String[] args) throws IOException {
        List<String[][]> environments = MatrixUtils.loadMatricesFromFile("environments_4x4_160_train.json");
        QLearningAgent agent = new QLearningAgent(0.1, 0.9, 0.1);
        for (String[][] cave : environments) {
            Environment environment = new Environment(cave);
            agent.train(environment, 1000);
        }
//        agent.saveQTable("qTable4x4.json");

    }

}