package io.SousaLJ.playersafelogin.client.gui;

import io.SousaLJ.playersafelogin.PlayerSafeLogin;
import io.SousaLJ.playersafelogin.client.ClientPasswordManager;
import io.SousaLJ.playersafelogin.client.PlayerSafeLoginClient;
import io.SousaLJ.playersafelogin.util.SecurityConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PasswordScreen extends Screen {

    private final Screen parentScreen;
    private EditBox passwordField;
    private Button proceedButton;

    private static final int LOGO_WIDTH = 128;

    private static final int LOGO_HEIGHT = 64;

    private Button randomPasswordButton;

    private Component subtitle = Component.translatable("playersafelogin.gui.login_subtitle");
    private static final ResourceLocation LOGO_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            PlayerSafeLogin.MODID, "textures/gui/logo_player_safe_login.png"
    );


    public PasswordScreen(Screen parent) {
        super(Component.translatable("playersafelogin.gui.login_title"));
        this.parentScreen = parent;
    }

    @Override
    protected void init() {
        this.passwordField = new EditBox(
                this.font,
                this.width/2 - 100,
                this.height/2 - 20,
                200,
                20,
                Component.translatable("playersafelogin.gui.master_password")
        );

        this.passwordField.setBordered(true);
        this.passwordField.setEditable(true);
        this.passwordField.setMaxLength(SecurityConstants.MAX_PASSWORD_LENGTH);
        this.passwordField.setFilter((p) -> p.length() <= SecurityConstants.MAX_PASSWORD_LENGTH);
        this.passwordField.setResponder((p) -> proceedButton.active = isPasswordValid(p));
        addRenderableWidget(passwordField);

        this.randomPasswordButton = Button.builder(
                Component.translatable("playersafelogin.gui.random_password"),
                (button) -> {
                    String randomPassword = ClientPasswordManager.generateRandomPassword();
                    passwordField.setValue(randomPassword);
                }
        ).pos(this.width/2 + 35, this.height/2 + 10).size(50, 20).build();
        addRenderableWidget(randomPasswordButton);

       this.proceedButton = Button.builder(
                Component.translatable("playersafelogin.gui.save_password"),
                this::onProceed
        ).pos(this.width/2 - 80, this.height/2 + 10).size(100, 20).build();

        proceedButton.active = false; // Desabilita o botão inicialmente

        addRenderableWidget(proceedButton);
    }

    private void onProceed(Button button) {
        String password = passwordField.getValue();
        String passwordHash = ClientPasswordManager.hashPassword(password);
        PlayerSafeLoginClient.passwordScreenCompleted = ClientPasswordManager.savePassword(passwordHash, password);;
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        renderBackground(gui, mouseX, mouseY, partialTicks);

        super.render(gui, mouseX, mouseY, partialTicks);

        // Posição centralizada da logo
        int logoX = (this.width - LOGO_WIDTH) / 2;
        int logoY = this.height / 4 - LOGO_HEIGHT / 2;

        // Desenha a logo na tela
        gui.blit(LOGO_TEXTURE, logoX, logoY, 0, 0, LOGO_WIDTH, LOGO_HEIGHT, LOGO_WIDTH, LOGO_HEIGHT);


        int textY = logoY + LOGO_HEIGHT - 5;
        gui.drawCenteredString(this.font, subtitle, this.width / 2, textY, 0xFFA500); // laranja
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= SecurityConstants.MIN_PASSWORD_LENGTH &&
                password.length() <= SecurityConstants.MAX_PASSWORD_LENGTH;
    }
}