package br.com.rsfot.domain.action;

import br.com.rsfot.game.HuntWumpus;

public interface Command {
    String execute(HuntWumpus huntWumpus);
}
