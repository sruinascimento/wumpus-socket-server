package br.com.rsfot.train;

import java.io.Serializable;

public record TrainObject (String instruction, String input, String output) implements Serializable {
}
