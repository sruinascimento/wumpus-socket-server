package br.com.rsfot.train;

import br.com.rsfot.domain.Environment;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

public class EnvironmentGenerator {
    public static void main(String[] args) {
        try {
//             Salvar os ambientes no arquivo
//            EnvironmentManagerRandomGenerator.saveEnvironmentsToFile();
//            System.out.println("Ambientes salvos com sucesso!");

//             Carregar os ambientes do arquivo
            List<Environment> environments = EnvironmentManagerRandomGenerator.loadEnvironmentsFromFile("environments7x7_400_matrizes_train.dat");
            System.out.println("Ambientes carregados com sucesso! Total: " + environments.size());


            IntStream.range(0, environments.size()).forEach(i -> {
                System.out.println();
                System.out.println("Ambiente " + i );
                environments.get(i).showCave();
            });

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}