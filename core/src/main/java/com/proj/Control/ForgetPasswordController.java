package com.proj.Control;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.proj.Main;
import com.proj.Model.*;
import com.proj.View.*;

public class ForgetPasswordController {
    private ForgetPasswordView view;
    private Skin skin;

    public void setView(ForgetPasswordView view){
        this.view = view;
       this.skin = GameAssetManager.getGameAssetManager().getSkin();
    }

    public void handleCheck(){
        if (view.getCheckButton().isChecked()) {
            String username = view.getUsernameField().getText();
            User user = App.findUserByUsername(username);

            if(user == null){
                view.getErrorLabel().setText("Username not found");
                return;
            }
            view.getQuestionLabel().setText("What is your dream job?");
            view.getResetButton().setDisabled(false);
            view.getErrorLabel().setText("");
        }
    }

    public void handleReset(){
        String username   = view.getUsernameField().getText();
        String answer     = view.getAnswerField().getText();
        String newPass    = view.getNewPasswordField().getText();

        User user = App.findUserByUsername(username);
        if(user==null){
            view.getErrorLabel().setText("Username not found");
            return;
        }
        if(!user.getSecurityAnswer().equalsIgnoreCase(answer)){
            view.getErrorLabel().setText("Wrong answer");
            return;
        }
        if(!Authenticator.isPasswordStrong(newPass)){
            view.getErrorLabel().setText("Weak password");
            return;
        }
        user.setPassword(newPass);
        view.getErrorLabel().setText("Password reset successful!");

        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task(){
            @Override public void run(){
                Main.getMain().getScreen().dispose();
                Main.getMain().setScreen(new LoginMenuView(new com.proj.Control.LoginMenuController(), skin));
            }
        },1);
    }

    public void handleBack(){
        Main.getMain().getScreen().dispose();
        Main.getMain().setScreen(new LoginMenuView(new com.proj.Control.LoginMenuController(), skin));
    }

}
