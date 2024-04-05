package br.com.rsfot.util;

import java.util.Objects;

public class CommandKeyExtractor {
    public static String from(String command) {
        if ( Objects.isNull(command) || command.split(" ").length < 1) {
            return null;
        }
        return command.split(" ")[0].toUpperCase();
    }
}
