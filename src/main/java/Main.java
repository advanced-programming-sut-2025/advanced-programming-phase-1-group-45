//package org.example;


import managers.PlayerTurnManager;
import models.Player;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
//        System.out.printf("Hello and welcome!");
//
//        for (int i = 1; i <= 5; i++) {
//            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
//            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
//            System.out.println("i = " + i);
//        }
        Player firstPlayer = new Player("man", "999999" , "hoo", "jssj", "zan");
        Player secondplayer = new Player("kkk", "999999" , "hoo", "nnn", "zan");
        Player thirdplayer = new Player("pla", "999999" , "hoo", "eee", "zan");
        List<Player> players = Arrays.asList(firstPlayer, secondplayer, thirdplayer);
        PlayerTurnManager gameTurnManager = new PlayerTurnManager(players);
        for (int i = 0; i < 10; i++) {
            gameTurnManager.addCurrentTurn();
        }
    }
}