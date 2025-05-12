package views;

import controllers.MenuController;
import models.User;

public class ProfileMenu implements Menu {
    @Override
    public void handleCommand(String command, MenuController controller){
        if(command.equals("user info")){
            User user =  controller.getCurrentUser();
            System.out.println(controller.getUserManager().getUserInfo(user));
        } else if(command.equals("menu exit")){
            System.out.println("moving to main menu");
            controller.setCurrentMenu(new MainMenu());
        } else if(command.equals("show current menu")){
            System.out.println("you are now in profile menu");
        } else if(command.startsWith("change username")){
            String[] parts = command.split("\\s+");
            String newUsername = parts[3];
            User user = controller.getCurrentUser();
            controller.getUserManager().changeUsername(user, newUsername);
        } else if (command.startsWith("change password")){
            String[] parts = command.split("\\s+");
            String newPassword = parts[3];
            String oldPassword = parts[5];
            User user = controller.getCurrentUser();
            controller.getUserManager().changePassword(user, newPassword, oldPassword);
        } else if(command.startsWith("change email")){
            String[] parts = command.split("\\s+");
            String newEmail = parts[3];
            User user = controller.getCurrentUser();
            controller.getUserManager().changeEmail(user, newEmail);
        }
        else if (command.startsWith("change nickname")){
            String[] parts = command.split("\\s+");
            String newNickname = parts[3];
            User user = controller.getCurrentUser();
            controller.getUserManager().changeNickname(user, newNickname);
        } else System.out.println("invalid command");
    }
}
