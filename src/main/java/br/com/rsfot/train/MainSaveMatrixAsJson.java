package br.com.rsfot.train;

import br.com.rsfot.domain.Environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainSaveMatrixAsJson {
    public static void saveEnvironmentsToFile() throws IOException {
        List<Environment> environments = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            environments.add(new Environment(7));
        }

        List<String[][]> matrices = new ArrayList<>();
        for (Environment environment : environments) {
            matrices.add(environment.getCave());
        }

        MatrixUtils.saveMatricesToFile(matrices, "environments_7x7_100_test.json");
    }

    public static List<Environment> loadEnvironmentsFromFile() throws IOException {
        List<String[][]> matrices = MatrixUtils.loadMatricesFromFile("environments_4x4_40_test.json");
        List<Environment> environments = new ArrayList<>();
        for (String[][] matrix : matrices) {
            environments.add(new Environment(matrix));
        }
        return environments;
    }

    public static void main(String[] args) {
        //Usado para gerar os ambientes e salvar em um arquivo.json
        //ABORDAGEM OFICIAL

        try {
            //SALVA OS ARQUIVOS
//            saveEnvironmentsToFile();
//            System.out.println("Ambientes salvos com sucesso!");

            List<Environment> environments = loadEnvironmentsFromFile();
            System.out.println("Ambientes carregados com sucesso! Total: " + environments.size());

            var count = 0;
            for (Environment env : environments) {
                System.out.println("Envie " + count++);

                Environment environment = new Environment(env.getCave());


//                environment.showCave();
//                environment.getFeelingsByCoordinate().forEach((k, v) -> {
//                    System.out.println(k + " -> " + v);
//                });
//
//                System.out.println();
//                System.out.println();
//                System.out.println("=====================================");
//                System.out.println();
//                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}