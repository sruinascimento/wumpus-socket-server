package br.com.rsfot.domain.action;

import br.com.rsfot.game.HuntWumpus;
import br.com.rsfot.report.Report;

public class GrabCommand implements Command{
    @Override
    public String execute(HuntWumpus huntWumpus) {
        huntWumpus.grabGold();
        return Report.generate(huntWumpus, false);
    }
}
