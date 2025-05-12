package views;

import controllers.MenuController;

public class StartMenu implements Menu{
    @Override
    public void handleCommand(String command, MenuController controller) {
        switch (command) {
            case "menu enter register":
                controller.setCurrentMenu(new SignUpMenu());
                break;
            case "menu enter login":
                controller.setCurrentMenu(new LoginMenu());
                break;
            case "show current menu":
                System.out.println("welcome");
                break;
            case "menu exit":
                System.out.println("Bye");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid command");
        }
    }
}
