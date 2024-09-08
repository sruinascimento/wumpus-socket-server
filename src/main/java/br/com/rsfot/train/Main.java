package br.com.rsfot.train;

import br.com.rsfot.domain.Environment;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String filename = "environments7x7.dat";
        int count = 500;
        int dimension = 7;

        try {
            // Salvar os ambientes no arquivo
            EnvironmentManagerRandomGenerator.saveEnvironmentsToFile(filename, count, dimension);
            System.out.println("Ambientes salvos com sucesso!");

            // Carregar os ambientes do arquivo
            List<Environment> environments = EnvironmentManagerRandomGenerator.loadEnvironmentsFromFile(filename);
            System.out.println("Ambientes carregados com sucesso! Total: " + environments.size());

            environments.forEach(System.out::println);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}