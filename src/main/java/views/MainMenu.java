package views;

import controllers.MenuController;


public class MainMenu implements Menu {
    @Override
    public void handleCommand(String command, MenuController controller) {
        switch (command) {
            case "menu enter Avatar":
                //controller.setCurrentMenu(new AvatarMenu());
                break;
            case "menu enter profile":
                controller.setCurrentMenu(new ProfileMenu());
                break;
            case "menu enter game":
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
            default:
                    System.out.println("Invalid command");
        }
    }
}

