package br.com.rsfot.train;

import br.com.rsfot.domain.Environment;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EnvironmentManagerRandomGenerator {

    public static void saveEnvironmentsToFile( ) throws IOException {
        List<Environment> environments = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            environments.add(new Environment(7));
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("environments7x7_100_matrizes_test.dat"))) {
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