package br.com.rsfot.report;

import br.com.rsfot.game.HuntWumpus;
import com.google.gson.Gson;

public class ReportGenerator {
    public static String generate(HuntWumpus huntWumpus) {
        AgentStatus agentStatus = new AgentStatus(huntWumpus.getAgent());
        FeelingByCoordinate feelingByCoordinate = new FeelingByCoordinate(huntWumpus.getEnvironment(), huntWumpus.getAgent().getStringCoordinate());
        ReportOfTurn reportOfTurn = new ReportOfTurn(agentStatus, feelingByCoordinate, huntWumpus.isAgentWinTheGame());
        return new Gson().toJson(reportOfTurn);
    }
}
