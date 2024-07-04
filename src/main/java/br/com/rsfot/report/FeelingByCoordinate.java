package br.com.rsfot.report;

import br.com.rsfot.domain.Agent;
import br.com.rsfot.domain.Environment;

import static br.com.rsfot.domain.Feelings.*;

public record FeelingByCoordinate(
        boolean breeze,
        boolean stench,
        boolean glitter,
        boolean impact) {
    public FeelingByCoordinate(Environment environment, Agent agent, boolean impact) {
        this(
                environment.getFeelingsByCoordinate().get(agent.getStringCoordinate()).contains(BREEZE),
                environment.getFeelingsByCoordinate().get(agent.getStringCoordinate()).contains(STENCH),
                environment.getFeelingsByCoordinate().get(agent.getStringCoordinate()).contains(GLITTER) && !agent.hasGold(),
                impact
        );
    }


}
