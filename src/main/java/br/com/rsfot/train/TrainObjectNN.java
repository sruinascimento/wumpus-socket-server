package br.com.rsfot.train;

import java.io.Serializable;
import java.util.List;

public record TrainObjectNN(List<Integer> input, List<Integer> output) implements Serializable {
}
