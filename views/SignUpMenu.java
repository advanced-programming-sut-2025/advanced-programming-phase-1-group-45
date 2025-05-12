package views;

import controllers.MenuController;

public class SignUpMenu implements Menu{
    @Override
    public void handleCommand(String command, MenuController controller) {
        if(command.startsWith("register ")) {
            controller.getUserManager().register(command, controller);
        } else if (command.startsWith("-a")){
            String answer = command.split("\\s+")[1];
            controller.getCurrentUser().setSecurityAnswer(answer);
            System.out.println("User registered successfully");
            System.out.println("you are now in main menu");
            controller.setCurrentMenu(new MainMenu());
        } else if(command.equals("show current menu")){
            System.out.println("You are now in sign up menu");
        } else if(command.equals("exit")){
            System.out.println("Goodbye");
            System.exit(0);
        } else System.out.println("Invalid command");
    }
}
