package br.com.rsfot.train;

import br.com.rsfot.domain.Environment;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EnvironmentManagerRandomGenerator {

    public static void saveEnvironmentsToFile( ) throws IOException {
        List<Environment> environments = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            environments.add(new Environment(4));
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("environments4x4_40_matrizes_test.dat"))) {
            oos.writeObject(environments);
        }



    }
    @SuppressWarnings("unchecked")
    public static List<Environment> loadEnvironmentsFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<Environment>) ois.readObject();
        }
    }
}