package views;

import controllers.AvatarController;
import controllers.MenuController;
import models.User;

public class AvatarMenu implements Menu{
    public void handleCommand(String command, MenuController controller){
        if(command.equals("show current menu")){
            System.out.println("you are now in profile menu");
        }
        else if(command.equals("menu exit")) {
            System.out.println("moving to main menu");
            controller.setCurrentMenu(new MainMenu());
        }
        else if(command.equals("show avatar")) {
            System.out.println("your avatar is @");
        } else System.out.println("invalid command");
    }
}
