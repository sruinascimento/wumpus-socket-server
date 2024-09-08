package br.com.rsfot.train;

import br.com.rsfot.domain.Environment;

import java.io.IOException;
import java.util.List;

public class ReaderDat {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<Environment> environments4x4 = EnvironmentManagerRandomGenerator.loadEnvironmentsFromFile("environments4x4.dat");
//        environments4x4.forEach(Environment::showCave);

    }
}
