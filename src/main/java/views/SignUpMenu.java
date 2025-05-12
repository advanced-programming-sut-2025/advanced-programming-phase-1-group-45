package views;

import controllers.MenuController;

public class SignUpMenu implements Menu{
    @Override
    public void handleCommand(String command, MenuController controller) {
        if(command.startsWith("register ")) {
            controller.getUserManager().register(command);
        } else if (command.startsWith("-a")){
            controller.getUserManager().setAnswer(command);
            System.out.println("User registered successfully");
            controller.setCurrentMenu(new MainMenu());
        } else if(command.equals("show current menu")){
            System.out.println("You are now in sign up menu");
        } else if(command.equals("exit")){
            System.out.println("Goodbye");
            System.exit(0);
        } else System.out.println("Invalid command");
    }
}
