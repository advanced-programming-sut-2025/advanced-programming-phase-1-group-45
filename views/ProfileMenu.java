package views;

import controllers.MenuController;

public class ProfileMenu implements Menu {
    @Override
    public void handleCommand(String command, MenuController controller){
        if(command.startsWith("change")){
            controller.getUserManager().updateProfile(command, controller.getCurrentUser());
        } else if(command.equals("user info")){
            System.out.println(controller.getUserManager().getUserInfo(controller.getCurrentUser()));
          } else if(command.equals("menu exit")){
            System.out.println("moving to main menu");
            controller.setCurrentMenu(new MainMenu());
        } else System.out.println("invalid command");
    }
}
