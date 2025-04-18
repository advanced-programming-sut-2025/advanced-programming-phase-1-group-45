package views;
import controllers.MenuController;
public interface Menu {
    void handleCommand(String command, MenuController controller);
}
