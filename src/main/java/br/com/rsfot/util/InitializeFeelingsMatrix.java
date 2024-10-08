package br.com.rsfot.util;

import br.com.rsfot.domain.Feelings;

import java.util.*;

import static br.com.rsfot.domain.Feelings.GLITTER;
import static br.com.rsfot.domain.EnvironmentObject.*;

public class InitializeFeelingsMatrix {
    private static Map<String, Set<Feelings>> feelingsByCoordinate = new LinkedHashMap<>();

    private void InitializeElementsMatrix() {
        throw new UnsupportedOperationException("Object cannot be instancied!");
    }

    public static Map<String, Set<Feelings>> setup(String[][] environmentMatrix) {
        int dimension = environmentMatrix.length;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {

                Set<Feelings> feelings = new HashSet<>();

                if (senseElement(environmentMatrix, i, j, GOLD.name())) {
                    feelings.add(GLITTER);
                }
                if (senseElement(environmentMatrix, i, j, WUMPUS.name())) {
                    feelings.add(Feelings.STENCH);
                }
                if (senseElement(environmentMatrix, i, j, PIT.name())) {
                    feelings.add(Feelings.BREEZE);
                }

                feelingsByCoordinate.put(i + "," + j, feelings);
            }
        }
        return feelingsByCoordinate;
    }

    private static boolean isGlitter(int x, int y, String[][] environmentMatrix) {
        return environmentMatrix[x][y].equals(GOLD.name());
    }

    private static boolean isStench(int x, int y, String[][] environmentMatrix) {
        return hasAdjacentElement(x, y, WUMPUS.name(), environmentMatrix);
    }

    private static boolean isBreeze(int x, int y, String[][] environmentMatrix) {
        return hasAdjacentElement(x, y, PIT.name(), environmentMatrix);
    }

    private static boolean hasAdjacentElement(int x, int y, String elementType, String[][] environmentMatrix) {
        int dimension = environmentMatrix.length;

        if (x + 1 < dimension && environmentMatrix[x + 1][y].equals(elementType)) {
            return true;
        }
        if (x - 1 >= 0 && environmentMatrix[x - 1][y].equals(elementType)) {
            return true;
        }
        if (y + 1 < dimension && environmentMatrix[x][y + 1].equals(elementType)) {
            return true;
        }
        if (y - 1 >= 0 && environmentMatrix[x][y - 1].equals(elementType)) {
            return true;
        }
        return false;
    }

    //NEW IMPLEMENTATION
    private static boolean senseElement(String[][] cave, int x, int y, String element) {
        if (x < 0 || y < 0 || x >= cave.length || y >= cave[0].length) {
            return false;
        }

        switch (element) {
            case "GOLD":
                return cave[x][y].equals("GOLD");
            case "WUMPUS":
                return senseStench(x, y, cave);
            case "PIT":
                return senseBreeze(x, y, cave);
            default:
                throw new RuntimeException("Unknown element");
        }
    }

    private static boolean senseStench(int x, int y, String[][] cave) {
        // Check neighbors (north, south, east, west)
        int[][] neighbors = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] neighbor : neighbors) {
            int nx = x + neighbor[0];
            int ny = y + neighbor[1];
            if (nx >= 0 && ny >= 0 && nx < cave.length && ny < cave[0].length) {
                if (cave[nx][ny].equals("WUMPUS")) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean senseBreeze(int x, int y, String[][] cave) {
        // Check neighbors (north, south, east, west)
        int[][] neighbors = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] neighbor : neighbors) {
            int nx = x + neighbor[0];
            int ny = y + neighbor[1];
            if (nx >= 0 && ny >= 0 && nx < cave.length && ny < cave[0].length) {
                if (cave[nx][ny].equals("PIT")) {
                    return true;
                }
            }
        }
        return false;
    }
}
