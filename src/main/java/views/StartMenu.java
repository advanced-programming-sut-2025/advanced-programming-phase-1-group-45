package views;

import controllers.MenuController;

public class StartMenu implements Menu{
    @Override
    public void handleCommand(String command, MenuController controller) {
        if (command.equals("menu enter register")){
            System.out.println("Register Menu");
            controller.setCurrentMenu(new SignUpMenu());
        }else if(command.equals("menu enter login")){
            System.out.println("Login Menu");
            controller.setCurrentMenu(new LoginMenu());
        }else if(command.equals("show current menu")){
            System.out.println("welcome");
        }else if(command.equals("menu exit")){
            System.out.println("Bye");
            System.exit(0);
        }else if(command.startsWith("forget password")){
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
        else System.out.println("Invalid command");
    }
}
