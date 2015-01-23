package org.abyeti.footinfo;

import javax.xml.bind.annotation.XmlAccessorType;

public class ApiDataModels {

    @XmlAccessorType
    static class AddTeamModel {
        public String team_name;
        public String players;
    }

    @XmlAccessorType
    static class CreateGameModel {
        public String teamA;
        public String teamB;
    }

    @XmlAccessorType
    static class FinishGameModel {
        public String matchId;
    }
}
