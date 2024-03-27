package br.com.rsfot.report;

import br.com.rsfot.domain.Environment;

import static br.com.rsfot.domain.Feelings.*;

public record FeelingByCoordinate(
        boolean breeze,
        boolean stench,
        boolean glitter,
        boolean scream
) {
    public FeelingByCoordinate(Environment environment, String coordinate) {
        this(
                environment.getFeelingsByCoordinate().get(coordinate).contains(BREEZE),
                environment.getFeelingsByCoordinate().get(coordinate).contains(STENCH),
                environment.getFeelingsByCoordinate().get(coordinate).contains(GLITTER),
                environment.getFeelingsByCoordinate().get(coordinate).contains(WUMPUS_SCREAM)
        );
    }


}
