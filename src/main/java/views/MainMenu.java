package views;

import controllers.MenuController;


public class MainMenu implements Menu {
    @Override
    public void handleCommand(String command, MenuController controller) {
        switch (command) {
            case "menu enter avatar":
                System.out.println("avatar menu");
                controller.setCurrentMenu(new AvatarMenu());
                break;
            case "menu enter profile":
                System.out.println("profile menu");
                controller.setCurrentMenu(new ProfileMenu());
                break;
            case "menu enter game":
                System.out.println("game menu");
                controller.setCurrentMenu(new GameMenu());
                break;
            case "show current menu":
                System.out.println("You are now in main menu");
                break;
            case "user logout":
                controller.setCurrentUser(null);
                System.out.println("logged out successfully");
                controller.setCurrentMenu(new StartMenu());
                break;
            case"exit":
                System.exit(0);
                break;
            default:
                System.out.println("Invalid command");
        }
    }
}

