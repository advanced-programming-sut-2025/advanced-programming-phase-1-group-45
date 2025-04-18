package views;

import Managers.GameManager;
import controllers.MenuController;
import models.GameSession;

public class GameMenu implements Menu{
    @Override
    public void handleCommand(String command, MenuController controller) {
        GameManager gm = controller.getGameManager();
        String me = controller.getCurrentUser().getUsername(); //current player
        if(command.startsWith("game new ")){
            GameSession s = gm.createNewGame(command, me);
            if(s != null){
                controller.setCurrentSession(s);
                System.out.println("New Game Created, now choose your map");
            }
        } else if(command.startsWith("game map")){
            if(controller.getCurrentSession() != null && gm.selectMap(controller.getCurrentSession(), command)){
                System.out.println("map selected");
            }
        } else if(command.equals("load game")){
            GameSession s= gm.loadLastSession(controller.getCurrentUser());
            if(s != null){
                controller.setCurrentSession(s);
                System.out.println("the last game loaded");
            } else System.out.println("no game exists");
        } else if(command.equals("next turn")){
            GameSession s = controller.getCurrentSession();
            if(s != null){
                s.nextTurn();
                System.out.println("it's " + s.getTurn() + " turn!");
            }
        } else if(command.equals("exit game")){
            GameSession s = controller.getCurrentSession();
            if(!me.equals(s.getPlayers().get(0))){
                System.out.println("only the creator can exit the game");
            } else {
                controller.getGameManager().saveSession(s);

                controller.setCurrentSession(null);
                System.out.println("exiting the game");
            }
        } else if(command.equals("force terminate")){
            GameSession s = controller.getCurrentSession();
            s.startVote(me);
            System.out.println("voting terminated");
        } else if(command.startsWith("vote")){
            GameSession s = controller.getCurrentSession();
            if(!s.isVoteInProgress()){
                System.out.println("no active voting");
            } else if(s.hasVoted(me)){
                System.out.println("you already voted");
            } else {
                boolean yes = command.equals("vote yes");
                s.recordVote(me, yes);
                System.out.println("vote submitted");
                if(s.allVoted()){
                    if(s.isVoteSuccessful()){
                        controller.getGameManager().endSession(s);
                        System.out.println("All votes were positive ,ending the game");
                        controller.setCurrentSession(null);
                        controller.setCurrentMenu(new MainMenu());
                    } else {
                        s.clearVote();
                        System.out.println("there is at least one negative vote, continuing the game");
                    }
                }
            }
        }
    }
    }