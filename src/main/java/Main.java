import controllers.MenuController;
import models.crops.AllCropsLoader;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        MenuController controller = new MenuController();
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("========");
//
//        while(true) {
//            System.out.print("\n> ");
//            String command = scanner.nextLine().trim();
//
//            if(command.equalsIgnoreCase("exit")) {
//                System.out.println("exiting");
//                break;
//            }
//
//            controller.processCommand(command);
//        }
//
//        scanner.close();
        System.out.println(AllCropsLoader.allCrops.get(0).getName());
    }
}
