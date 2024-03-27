package br.com.rsfot.game;

import br.com.rsfot.domain.*;

import static br.com.rsfot.domain.Direction.*;
import static br.com.rsfot.domain.Feelings.GLITTER;
import static br.com.rsfot.domain.EnvironmentObject.*;
import static br.com.rsfot.domain.Rotation.LEFT;
import static br.com.rsfot.domain.Rotation.RIGHT;

public class HuntWumpus {
    private Agent agent = new Agent();
    private Environment environment;
    private boolean agentWinTheGame = false;

    public HuntWumpus() {
    }

    public HuntWumpus(Agent agent, Environment environment) {
        this.agent = agent;
        this.environment = environment;
    }
    public HuntWumpus(Environment environment) {
        this.environment = environment;
    }

    public Agent getAgent() {
        return agent;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void turnAgentTo(Rotation rotation) {
        if (agent.isAlive()) {
            if (LEFT.equals(rotation)) {
                turnAgentLeft();
            }
            if (RIGHT.equals(rotation)) {
                turnAgentRight();
            }
            agent.decreasePointByAction();
        }
    }

    private void turnAgentLeft() {
        switch (agent.getFacingDirection()) {
            case NORTH -> agent.setFacingDirection(WEST);
            case WEST -> agent.setFacingDirection(SOUTH);
            case SOUTH -> agent.setFacingDirection(EAST);
            case EAST -> agent.setFacingDirection(NORTH);
        }
    }

    private void turnAgentRight() {
        switch (agent.getFacingDirection()) {
            case NORTH -> agent.setFacingDirection(EAST);
            case EAST -> agent.setFacingDirection(SOUTH);
            case SOUTH -> agent.setFacingDirection(WEST);
            case WEST -> agent.setFacingDirection(NORTH);
        }
    }

    public void moveForward() {
        if (canWalk() && agent.isAlive()) {
            agent.moveForward();
            agent.decreasePointByAction();
            if (isTheAgentDead()) {
                agent.die();
                agent.decreasePointByDeath();
            }
            if (agent.isAtInitialPosition() && agent.hasGold()) {
                agent.increasePointByGrabGoldAndWinTheGame();
                this.agentWinTheGame = true;
            }
        }
    }

    private boolean canWalk() {
        return switch (agent.getFacingDirection()) {
            case NORTH -> agent.getCoordinateX() - 1 >= 0;
            case SOUTH -> agent.getCoordinateX() + 1 <= environment.getDimension() - 1;
            case EAST -> agent.getCoordinateY() + 1 <= environment.getDimension() - 1;
            case WEST -> agent.getCoordinateY() - 1 >= 0;
        };
    }

    public void grabGold() {
        if (environment.getFeelingsByCoordinate().get(agent.getStringCoordinate()).contains(GLITTER)) {
            agent.grab();
            agent.decreasePointByAction();
        }
    }

    public void shoot() {
        if (agent.hasArrow()) {
            agent.shoot();
            agent.decreasePointByShoot();
            if (isTheAgentKillTheWumpus()) {
                agent.killTheWumpus();
                System.out.println(Feelings.WUMPUS_SCREAM.name());
                System.out.println("You killed the Wumpus");
            }
        }
    }

    public boolean isTheAgentDead() {
        boolean agentFallIntoAPit = environment.isThereAPitAt(agent.getCoordinateX(), agent.getCoordinateY());
        boolean agentDevouredByWumpus = environment.isThereAWumpusAt(agent.getCoordinateX(), agent.getCoordinateY()) && !agent.isKilledTheWumpus();
        return agentFallIntoAPit || agentDevouredByWumpus;
    }

    public boolean isTheAgentKillTheWumpus() {
        if (isAgentOnTheSameLineOfWumpus()) {
            if (agentIsOnTheLeftOfWumpus()) {
                return agent.getFacingDirection().equals(EAST);
            }
            if (agentIsOnTheRightOfWumpus()) {
                return agent.getFacingDirection().equals(WEST);
            }
        }

        if (isAgentOnTheSameColumnOfWumpus()) {
            if (agentIsAboveWumpus()) {
                return agent.getFacingDirection().equals(SOUTH);
            }
            if (agentIsBelowWumpus()) {
                return agent.getFacingDirection().equals(NORTH);
            }
        }
        return false;
    }

    private boolean isAgentOnTheSameLineOfWumpus() {
        int[] wumpusCoordinate = environment.getCoordinateOf(WUMPUS);
        int[] agentCoordinate = {agent.getCoordinateX(), agent.getCoordinateY()};

        return agentCoordinate[0] == wumpusCoordinate[0];
    }

    private boolean isAgentOnTheSameColumnOfWumpus() {
        int[] wumpusCoordinate = environment.getCoordinateOf(WUMPUS);
        int[] agentCoordinate = {agent.getCoordinateX(), agent.getCoordinateY()};

        return agentCoordinate[1] == wumpusCoordinate[1];
    }

    private boolean agentIsOnTheLeftOfWumpus() {
        int[] wumpusCoordinate = environment.getCoordinateOf(WUMPUS);
        int[] agentCoordinate = {agent.getCoordinateX(), agent.getCoordinateY()};

        return agentCoordinate[1] < wumpusCoordinate[1];
    }

    private boolean agentIsOnTheRightOfWumpus() {
        int[] wumpusCoordinate = environment.getCoordinateOf(WUMPUS);
        int[] agentCoordinate = {agent.getCoordinateX(), agent.getCoordinateY()};

        return agentCoordinate[1] > wumpusCoordinate[1];
    }

    private boolean agentIsAboveWumpus() {
        int[] wumpusCoordinate = environment.getCoordinateOf(WUMPUS);
        int[] agentCoordinate = {agent.getCoordinateX(), agent.getCoordinateY()};

        return agentCoordinate[0] < wumpusCoordinate[0];
    }

    private boolean agentIsBelowWumpus() {
        int[] wumpusCoordinate = environment.getCoordinateOf(WUMPUS);
        int[] agentCoordinate = {agent.getCoordinateX(), agent.getCoordinateY()};

        return agentCoordinate[0] > wumpusCoordinate[0];
    }

    public boolean isAgentWinTheGame() {
        return agentWinTheGame;
    }

    public boolean isGameOver() {
        return !agent.isAlive() || agentWinTheGame;
    }

    public void moveToDirection(Direction desiredDirection) {
        switch (desiredDirection) {
            case NORTH -> agent.setFacingDirection(NORTH);
            case SOUTH -> agent.setFacingDirection(SOUTH);
            case EAST -> agent.setFacingDirection(EAST);
            case WEST -> agent.setFacingDirection(WEST);
        }
        this.moveForward();
    }

    public String getJsonOfAgent() {
        return """                                
                "coordinate": "%s",
                "direction": "%s",
                "score": %d,
                "hasArrow": %b,
                "hasGold": %b,
                "killedTheWumpus": %b
                     """.formatted(agent.getStringCoordinate(),
                agent.getFacingDirection(),
                agent.getScore(),
                agent.hasArrow(),
                agent.hasGold(),
                agent.isKilledTheWumpus());
    }
}
