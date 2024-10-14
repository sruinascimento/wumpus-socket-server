package br.com.rsfot.train;

import java.io.*;
import java.util.List;
import java.util.Map;

public class QTableLoader {
    private Map<List<Integer>, Map<String, Double>> qTable;

    public void loadQTable(String filePath) throws IOException, ClassNotFoundException {
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            qTable = (Map<List<Integer>, Map<String, Double>>) objectInputStream.readObject();
        }
    }

    public void saveQTable(String filePath) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(qTable);
        }
    }

    public Map<List<Integer>, Map<String, Double>> getQTable() {
        return qTable;
    }

    public void setQTable(Map<List<Integer>, Map<String, Double>> qTable) {
        this.qTable = qTable;
    }

    public static void main(String[] args) {
        QTableLoader qTableLoader = new QTableLoader();
        try {
            qTableLoader.loadQTable("qTable4x4_alpha01_gamma09_explorationRate01_episodes2k_train_001.dat");

            Map<List<Integer>, Map<String, Double>> qTable = qTableLoader.getQTable();
            System.out.println(qTable.size());

//            qTable.forEach((k, v) -> {
//                System.out.println(k + " -> " + v);
//            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}