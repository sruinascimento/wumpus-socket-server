package br.com.rsfot.domain.action;

import br.com.rsfot.domain.Direction;
import br.com.rsfot.game.HuntWumpus;
import br.com.rsfot.util.CommandKeyExtractor;
import br.com.rsfot.util.DirectionExtractor;

import java.util.Objects;


public class ProcessCommand {
    public static String from(String command, HuntWumpus huntWumpus) {
        if (Objects.isNull(command) || command.isEmpty()) {
            return "Invalid command";
        }
        try {
            Direction direction = DirectionExtractor.from(command);
            String commandKey = CommandKeyExtractor.from(command);
            CommandType commandType = CommandType.valueOf(commandKey);
            return commandType.execute(huntWumpus, direction);
        } catch (Exception e) {
            return "Invalid command";
        }
    }
}
