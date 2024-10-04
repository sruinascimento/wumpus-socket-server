package br.com.rsfot.train;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

import org.nd4j.linalg.factory.Nd4j;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;

import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WumpusMLP {

    public static void main(String[] args) {
        String filePath = "train_data_set4x4_to_NN.json";
        List<TrainObjectNN> data = new ArrayList<>();
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<TrainObjectNN>>() {}.getType();
            data.addAll(gson.fromJson(reader, listType));

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (data.isEmpty()) {
            System.exit(1);
        }

        List<double[]> inputData = data.stream()
                .map(obj -> obj.input().stream()
                        .mapToDouble(Integer::doubleValue) // Converte List<Double> para int[]
                        .toArray())
                .collect(Collectors.toList());

        List<double[]> outputData = data.stream()
                .map(obj -> obj.output().stream()
                        .mapToDouble(Integer::doubleValue) // Converte List<Double> para int[]
                        .toArray())
                .collect(Collectors.toList());

        // Converter para INDArray
        double[][] inputArray = inputData.toArray(new double[0][0]);
        double[][] outputArray = outputData.toArray(new double[0][0]);

        INDArray input = Nd4j.create(inputArray);
        INDArray output = Nd4j.create(outputArray);

        // Configuração da rede neural
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.01))
                .list()
                .layer(new DenseLayer.Builder().nIn(10).nOut(16).activation(Activation.RELU).build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT).nOut(9).activation(Activation.SOFTMAX).build())
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        // Criar um DataSet
        DataSet dataSet = new DataSet(input, output);

        // Treinar a rede
        model.fit(dataSet);

        // Fazer uma predição
        INDArray inputExample = Nd4j.create(new double[][]{{2, 3, 1, 0, 0, 0, 1, 0, 0, 0}});
        INDArray outputPredicted = model.output(inputExample);

        // Encontrar a ação com maior probabilidade
        int maxIndex = Nd4j.argMax(outputPredicted, 1).getInt(0);
        System.out.println("Ação predita: " + maxIndex); // 0 = GRAB, 1 = MOVE NORTH, etc.
    }
}