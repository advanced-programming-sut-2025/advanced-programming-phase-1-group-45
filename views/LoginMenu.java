package views;

import controllers.MenuController;
import models.User;

public class LoginMenu implements Menu{
    @Override
    public void handleCommand(String command, MenuController controller){
        if(command.startsWith("login")){
            User u = controller.getUserManager().login(command));
            if(u != null){
                controller.setCurrentUser(u);
                System.out.println("Login Successful, moving to main menu");
                controller.setCurrentMenu(new MainMenu());
            }
        } else if(command.startsWith("forget password")){
            controller.getUserManager().startPasswordRecovery(command);
        } else if(command.startsWith("answer")){
            controller.getUserManager().completePasswordRecovery(command);
        } else if(command.equals("show current menu"){
            System.out.println("you are now in Login Menu");
        } else if(command.equals("menu exit")){
            System.out.println("Goodbye");
            System.exit(0);
        } else System.out.println("Invalid command");
    }
}
