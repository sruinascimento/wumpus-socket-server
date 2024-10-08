package br.com.rsfot.train;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class MatrixUtils {
    private static final Gson gson = new Gson();

    public static void saveMatricesToFile(List<String[][]> matrices, String filename) throws IOException {
        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(matrices, writer);
        }
    }

    public static List<String[][]> loadMatricesFromFile(String filename) throws IOException {
        try (Reader reader = new FileReader(filename)) {
            Type type = new TypeToken<List<String[][]>>() {}.getType();
            return gson.fromJson(reader, type);
        }
    }
}