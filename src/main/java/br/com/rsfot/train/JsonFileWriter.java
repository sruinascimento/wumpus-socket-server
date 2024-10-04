package br.com.rsfot.train;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JsonFileWriter {


    public static void writeTrainObjectListToJsonFile(List<TrainObject> trainObjectList, String filePath) throws IOException {
        JSONArray jsonArray = new JSONArray();

        for (TrainObject trainObject : trainObjectList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("instruction", trainObject.instruction());
            jsonObject.put("input", trainObject.input());
            jsonObject.put("output", trainObject.output());
            jsonArray.put(jsonObject);
        }

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4)); // Indent with 4 spaces for readability
        }

    }

    public static void writeTrainObjectNNListToJsonFile(List<TrainObjectNN> trainObjectList, String filePath) throws IOException {
        JSONArray jsonArray = new JSONArray();

        for (TrainObjectNN trainObject : trainObjectList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("input", trainObject.input());
            jsonObject.put("output", trainObject.output());
            jsonArray.put(jsonObject);
        }

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4));
        }

    }
}