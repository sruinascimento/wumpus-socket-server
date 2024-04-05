package br.com.rsfot.domain.action;

import br.com.rsfot.domain.Direction;
import br.com.rsfot.game.HuntWumpus;
import br.com.rsfot.report.Report;

public class ShootCommand implements Command {
    private Direction direction;

    public ShootCommand(Direction direction) {
        this.direction = direction;
    }

    @Override
    public String execute(HuntWumpus huntWumpus) {
        huntWumpus.shoot(direction);
        return Report.generate(huntWumpus, false);
    }
}
