package com.proj.Control;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.proj.Main;
import com.proj.Model.User;
import com.proj.Session;
import com.proj.View.ChangeInfoMenuView;
import com.proj.View.MainMenuView;
import com.proj.Model.PasswordGenerator;

public class ChangeInfoController {
    private ChangeInfoMenuView view;
    private User currentUser;
    private User userManager;

    public void setView(ChangeInfoMenuView view) {
        this.view = view;
        this.currentUser = Session.getCurrentUser();
        this.userManager = Session.getUserManager();
        setupHandlers();
    }

    private void setupHandlers() {
        view.getSaveButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleSaveChanges();
            }
        });
        view.getGeneratePasswordButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String randomPassword = PasswordGenerator.generateRandomPassword();
                view.getPasswordField().setText(randomPassword);
            }
        });
    }

    private void handleSaveChanges() {
        String newUsername = view.getUsernameField().getText();
        String newNickname = view.getNicknameField().getText();
        String newPassword = view.getPasswordField().getText();
        String newEmail = view.getEmailField().getText();

        if (!newUsername.isEmpty()) {
            userManager.changeUsername(currentUser, newUsername);
        }

        if (!newNickname.isEmpty()) {
            currentUser.setNickname(newNickname);
        }

        if (!newPassword.isEmpty()) {
            userManager.changePassword(currentUser, "current-password-placeholder", newPassword);
        }
        if (!newUsername.isEmpty()) {
            userManager.changeUsername(currentUser, newUsername);
        }

        if (!newNickname.isEmpty()) {
            currentUser.setNickname(newNickname);
        }

        if (!newEmail.isEmpty()) {
            if (isValidEmail(newEmail)) { 
                currentUser.setEmail(newEmail);
            } else {
                System.out.println("Invalid email format");
            }
        }

        if (!newPassword.isEmpty()) {
            userManager.changePassword(currentUser, "current-password-placeholder", newPassword);
        }

        // Return to main menu
        Skin skin = view.getSaveButton().getSkin(); 
        Main.getMain().setScreen(new MainMenuView(
            new MainMenuController(),
            skin
        ));
    }
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
}
