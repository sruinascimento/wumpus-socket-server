package br.com.rsfot.domain;

import br.com.rsfot.util.*;

import java.io.Serializable;
import java.util.*;

public class Environment implements Serializable {
    private String[][] cave;
    private int dimension;
    private Map<String, Set<Feelings>> feelingsByCoordinate;

    public Environment(int dimension) {
        if (dimension < 4) {
            throw new IllegalArgumentException("Dimension must be at least 4");
        }
        this.dimension = dimension;
        this.cave = InitializeElementsMatrix.setup(dimension);
        this.feelingsByCoordinate = InitializeFeelingsMatrix.setup(cave);
    }

    public Environment(String[][] cave) {
        this.cave = cave;
        this.dimension = cave.length;
        this.feelingsByCoordinate = InitializeFeelingsMatrix.setup(cave);
    }

    public String[][] getCave() {
        return cave;
    }

    public int getDimension() {
        return dimension;
    }

    public Map<String, Set<Feelings>> getFeelingsByCoordinate() {
        return Collections.unmodifiableMap(feelingsByCoordinate);
    }

    public void showCave() {
        System.out.println("-------------------");
        System.out.println(MatrixFormatter.format(cave, 2, 1));
        System.out.println("-------------------");
    }

    public void setCave(String[][] cave) {
        this.cave = cave;
    }

    public boolean isThereAPitAt(int x, int y) {
        return cave[x][y].equals(EnvironmentObject.PIT.name());
    }

    public boolean isThereAWumpusAt(int x, int y) {
        return cave[x][y].equals(EnvironmentObject.WUMPUS.name());
    }

    @Override
    public String toString() {
        return "Environment{" +
                "cave=" + Arrays.toString(cave) +
                ", dimension=" + dimension +
                ", feelingsByCoordinate=" + feelingsByCoordinate +
                '}';
    }

    public int[] getCoordinateOf(EnvironmentObject object) {
        for (int i = 0; i < cave.length; i++) {
            for (int j = 0; j < cave.length; j++) {
                if (cave[i][j].equals(object.name())) {
                    return new int[]{i, j};
                }
            }
        }
        throw new RuntimeException("Object not found");
    }
}
