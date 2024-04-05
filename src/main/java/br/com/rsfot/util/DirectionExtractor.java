package br.com.rsfot.util;

import br.com.rsfot.domain.Direction;

import java.util.Objects;

public class DirectionExtractor {
    public static Direction from(String command) {
        if (Objects.isNull(command) || command.split(" ").length < 2) {
            return null;
        }
        return Direction.valueOf(command.split(" ")[1].toUpperCase());
    }
}
