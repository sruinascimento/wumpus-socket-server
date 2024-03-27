package br.com.rsfot.report;

import br.com.rsfot.domain.Agent;

public record AgentStatus(
        String coordinate,
        String direction,
        int score,
        boolean hasGold,
        boolean hasArrow,
        boolean isAlive) {

    public AgentStatus(Agent agent) {
        this(
                String.format("(%d, %d)", agent.getCoordinateX(), agent.getCoordinateY()),
                agent.getFacingDirection().name(),
                agent.getScore(),
                agent.hasGold(),
                agent.hasArrow(),
                agent.isAlive()
        );
    }
}
