package br.com.rsfot.report;

public record ReportOfTurn(
        AgentStatus agentStatus,
        FeelingByCoordinate feelingByCoordinate,
        boolean agentWin,
        boolean wumpusDead,
        boolean gameOver) {
}
