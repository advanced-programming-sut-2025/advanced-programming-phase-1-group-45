package views;

import controllers.MenuController;
import models.Player;

public class LoginMenu implements Menu{
    @Override
    public void handleCommand(String command, MenuController controller){
        if(command.startsWith("login")){
            Player u = controller.getUserManager().login(command);
            if(u != null){
                controller.setCurrentUser(u);
                System.out.println("Login Successful, moving to main menu");
                controller.setCurrentMenu(new MainMenu());
            }
        } else if(command.startsWith("forget password")){
            String um = controller.getUserManager().startPasswordRecovery(command);
            controller.setPendingRecoveryUsername(um);
        } else if(command.startsWith("answer")){
            controller.getUserManager().completePasswordRecovery(command);
        } else if(command.equals("reset random")){
            String user = controller.getPendingRecoveryUsername();
            if(user != null){
                String newPassword = controller.getUserManager().resetPasswordRandom(user);
                System.out.println("your new password is: " + newPassword);
                controller.setPendingRecoveryUsername(null);
            }
        } else if(command.startsWith("reset set -p ")){
            String user = controller.getPendingRecoveryUsername();
            if(user != null){
                String newPassword = command.substring("reset set -p ".length());
                if(controller.getUserManager().resetPasswordManual(user, newPassword)){
                    System.out.println("Password reset successful");
                    controller.setPendingRecoveryUsername(null);
                }
            }
        }
        else if(command.equals("show current menu")){
            System.out.println("you are now in Login Menu");
        } else if(command.equals("menu exit")){
            System.out.println("Goodbye");
            System.exit(0);
        } else System.out.println("Invalid command");
    }
}
