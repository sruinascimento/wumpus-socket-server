package br.com.rsfot.report;

import br.com.rsfot.game.HuntWumpus;
import com.google.gson.Gson;

public class Report {
    public static String generate(HuntWumpus huntWumpus, boolean impactOgAgentOnTheWall) {
        AgentStatus agentStatus = new AgentStatus(huntWumpus.getAgent());
        FeelingByCoordinate feelingByCoordinate = new FeelingByCoordinate(huntWumpus.getEnvironment(), huntWumpus.getAgent(), impactOgAgentOnTheWall);
        ReportOfTurn reportOfTurn = new ReportOfTurn(agentStatus,
                feelingByCoordinate,
                huntWumpus.isAgentWinTheGame(),
                huntWumpus.getAgent().isKilledTheWumpus(),
                huntWumpus.isGameOver());
        return new Gson().toJson(reportOfTurn);
    }
}
