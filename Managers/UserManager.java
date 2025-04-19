package Managers;

public class UserManager {
    //switch case
    //register
    //login

    //getUserinfo
    //updateProfile
    //load
    //save same as gameManager
    private void save(){
        try(Writer w = Files.newBufferedWriter(storage)){
            gson.toJson(gameSessions, w);
        } catch (IOException ignored) {
        }
    }
}
