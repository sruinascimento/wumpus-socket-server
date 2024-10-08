package br.com.rsfot.domain;

import static br.com.rsfot.domain.Direction.EAST;

public class Agent {
    private int coordinateX = 0;
    private int coordinateY = 0;
    private String name = "ANAKIN";
    private boolean arrow = true;
    private int score = 1000;
    private Direction facingDirection = EAST;
    private boolean gold = false;
    private boolean alive = true;
    private boolean killedTheWumpus = false;

    public Agent() {
    }

    public Agent(int coordinateX, int coordinateY) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    public int getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(int coordinateX) {
        this.coordinateX = coordinateX;
    }

    public int getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(int coordinateY) {
        this.coordinateY = coordinateY;
    }
    public String getName() {
        return name;
    }

    public Direction getFacingDirection() {
        return facingDirection;
    }

    public void setFacingDirection(Direction facingDirection) {
        this.facingDirection = facingDirection;
    }

    public void moveForward() {
        this.moveTo(this.facingDirection);
    }

    private void moveTo(Direction direction) {
        switch (direction) {
            case NORTH -> this.coordinateX--;
            case SOUTH -> this.coordinateX++;
            case EAST -> this.coordinateY++;
            case WEST -> this.coordinateY--;
        }
    }

    public void shoot() {
        if (arrow) {
            arrow = false;
        }
    }

    public boolean hasArrow() {
        return arrow;
    }

    public void setArrow(boolean arrow) {
        this.arrow = arrow;
    }

    public boolean hasGold() {
        return gold;
    }

    public void setGold(boolean gold) {
        this.gold = gold;
    }

    public void grab() {
        this.gold = true;
    }

    public boolean isAlive() {
        return alive;
    }

    public void die() {
        this.alive = false;
    }

    public int getScore() {
        return score;
    }

    public String getStringCoordinate() {
        return coordinateX + "," + coordinateY;
    }

    public void decreasePointByAction() {
        this.score--;
    }

    public void decreasePointByShoot() {
        this.score -= 10;
    }
    public void decreasePointByDeath() {
        this.score -= 1000;
    }

    public void increasePointByGrabGoldAndWinTheGame() {
        this.score += 1000;
    }

    public void increasePointByKillTheWumpus() {
        this.score += 500;
    }

    public void increasePointByGrabGold() {
        this.score += 1000;
    }

    public boolean isKilledTheWumpus() {
        return killedTheWumpus;
    }

    public void killTheWumpus() {
        this.killedTheWumpus = true;
    }

    public boolean isAtInitialPosition() {
        return this.coordinateX == 0 && this.coordinateY == 0;
    }

    public boolean agentWinTheGame() {
        return this.gold && this.isAtInitialPosition();
    }

    @Override
    public String toString() {
        return "Agent{" +
                "coordinateX=" + coordinateX +
                ", coordinateY=" + coordinateY +
                ", name='" + name + '\'' +
                ", arrow=" + arrow +
                ", score=" + score +
                ", facingDirection=" + facingDirection +
                ", gold=" + gold +
                ", alive=" + alive +
                ", killedWumpus=" + killedTheWumpus +
                '}';
    }
}
