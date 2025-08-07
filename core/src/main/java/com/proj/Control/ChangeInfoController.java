package com.proj.Control;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.proj.Main;
import com.proj.Model.User;
import com.proj.Session;
import com.proj.View.ChangeInfoMenuView;
import com.proj.View.MainMenuView;

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
    }

    private void handleSaveChanges() {
        // Get inputs
        String newUsername = view.getUsernameField().getText();
        String newNickname = view.getNicknameField().getText();
        String newPassword = view.getPasswordField().getText();

        // Process changes
        if (!newUsername.isEmpty()) {
            userManager.changeUsername(currentUser, newUsername);
        }

        if (!newNickname.isEmpty()) {
            currentUser.setNickname(newNickname);
        }

        if (!newPassword.isEmpty()) {
            // Note: You'll need to implement proper password change flow
            userManager.changePassword(currentUser, "current-password-placeholder", newPassword);
        }

        // Return to main menu
        Skin skin = view.getSaveButton().getSkin(); // Get skin from existing UI element
        Main.getMain().setScreen(new MainMenuView(
            new MainMenuController(),
            skin
        ));
    }
}
