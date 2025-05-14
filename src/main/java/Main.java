import models.MapElements.crops.AllCropsLoader;

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
