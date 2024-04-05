package br.com.rsfot.domain.action;

import br.com.rsfot.domain.Direction;
import br.com.rsfot.game.HuntWumpus;

public enum CommandType {
    MOVE {
        @Override
        public String execute(HuntWumpus huntWumpus, Direction direction) {
            return new MoveCommand(direction).execute(huntWumpus);
        }
    },
    GRAB {
        @Override
        public String execute(HuntWumpus huntWumpus, Direction direction) {
            return new GrabCommand().execute(huntWumpus);
        }
    },
    SHOOT {
        @Override
        public String execute(HuntWumpus huntWumpus, Direction direction) {
            return new ShootCommand(direction).execute(huntWumpus);
        }
    };

    public abstract String execute(HuntWumpus huntWumpus, Direction direction);
}
