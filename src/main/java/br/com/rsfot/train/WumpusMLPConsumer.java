package br.com.rsfot.train;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;

public class WumpusMLPConsumer {
    public static void main(String[] args) {
        File modelFile = new File("5x5_matrix_trained_model_30_epochs.zip");
        MultiLayerNetwork loadedModel = null;
        try {
            loadedModel = ModelSerializer.restoreMultiLayerNetwork(modelFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        INDArray inputExample = Nd4j.create(new double[][]{{2, 3, 1, 0, 0, 0, 0, 0, 1, 0}});
        INDArray outputPredicted = loadedModel.output(inputExample);

        // Encontrar a ação com maior probabilidade
        int maxIndex = Nd4j.argMax(outputPredicted, 1).getInt(0);
        System.out.println("Ação predita: " + maxIndex); // 0 = GRAB, 1 = MOVE NORTH, etc.

        String[] actions = {"GRAB", "MOVE NORTH", "MOVE SOUTH", "MOVE EAST", "MOVE WEST", "SHOOT NORTH", "SHOOT SOUTH", "SHOOT EAST", "SHOOT WEST", "NO ACTION"};
        String predictedAction = actions[maxIndex];

        System.out.println("Ação predita: " + predictedAction);

    }
}
