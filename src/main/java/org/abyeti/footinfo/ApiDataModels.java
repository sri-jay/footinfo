package org.abyeti.footinfo;

import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Datamodels for java type safety
**/
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
        public String match_id;
    }

    @XmlAccessorType
    static class PlayerData {
        public String player_id;
    }

    @XmlAccessorType
    static class GetTeam {
        public String team_id;
    }

    @XmlAccessorType
    static class GameData {
        public String match_id;
    }
}
