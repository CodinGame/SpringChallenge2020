package com.codingame.spring2020;

public class LeagueRules {
    public int numberOfCherries = 4;
    public boolean forOfWar = true;
    public boolean mapWraps = true;
    public boolean bodyBlock = true;
    public boolean friendlyBodyBlock = true;
    public boolean speedAbilityAvailable = true;
    public boolean switchAbilityAvailable = true;
    public int minPacsPerPlayer = 2;
    public int maxPacsPerPlayer = 5;
    
    public static LeagueRules fromIndex(int index) {
        LeagueRules rules = new LeagueRules();
        
        if (index == 1) {
            rules.minPacsPerPlayer = 1;
            rules.maxPacsPerPlayer = 1;
        }
        if (index <= 2) {
            rules.forOfWar = false;
            rules.speedAbilityAvailable = false;
            rules.switchAbilityAvailable = false;
        }

        return rules;
    }
}