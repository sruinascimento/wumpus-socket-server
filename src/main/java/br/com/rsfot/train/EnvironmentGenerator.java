package br.com.rsfot.train;

import br.com.rsfot.domain.Environment;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

public class EnvironmentGenerator {
    public static void main(String[] args) {
        try {
//             Salvar os ambientes no arquivo
            EnvironmentManagerRandomGenerator.saveEnvironmentsToFile();
            System.out.println("Ambientes salvos com sucesso!");

//             Carregar os ambientes do arquivo
            List<Environment> environments = EnvironmentManagerRandomGenerator.loadEnvironmentsFromFile("src/main/resources/environments4x4_40_matrizes_test.dat");
            System.out.println("Ambientes carregados com sucesso! Total: " + environments.size());

            for (Environment env: environments) {
                Environment environment = new Environment(env.getCave());
                environment.showCave();
                environment.getFeelingsByCoordinate().forEach((k, v) -> {
                    System.out.println(k + " -> " + v);
                });
            }

            // Mostrar os ambientes
//            Environment environment = new Environment(environments.getFirst().getCave());
//            environment.showCave();
//            environment.getFeelingsByCoordinate().forEach((k, v) -> {
//                System.out.println(k + " -> " + v);
//            });
//            IntStream.range(0, environments.size()).forEach(i -> {
//                System.out.println();
//                System.out.println("Ambiente " + i );
//                environments.get(i).showCave();
//                System.out.println();
//                System.out.println(environments.get(i).getFeelingsByCoordinate());
//            });

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}