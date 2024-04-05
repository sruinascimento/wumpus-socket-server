package br.com.rsfot.domain.action;

import br.com.rsfot.domain.Direction;
import br.com.rsfot.game.HuntWumpus;
import br.com.rsfot.report.Report;

public class MoveCommand implements Command {
    private Direction direction;

    public MoveCommand(Direction direction) {
        this.direction = direction;
    }

    @Override
    public String execute(HuntWumpus huntWumpus) {
        boolean impact = !huntWumpus.moveToDirection(direction);
        return Report.generate(huntWumpus, impact);
    }
}
