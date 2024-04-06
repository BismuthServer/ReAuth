package technicianlp.reauth.gui;

import org.lwjgl.input.Keyboard;
import technicianlp.reauth.session.SessionHelper;

import java.io.IOException;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;

public final class OfflineLoginScreen extends AbstractScreen {

    private TextFieldWidget username;
    private ButtonWidget confirm;

    public OfflineLoginScreen(Screen background) {
        super("reauth.gui.title.offline", background);
    }

    @Override
    public final void init() {
        super.init();

        this.username = new TextFieldWidget(2, this.textRenderer, this.centerX - BUTTON_WIDTH / 2, this.centerY - 5, BUTTON_WIDTH, 20);
        this.username.setMaxLength(16);
        this.username.setFocused(true);


        this.confirm = new ButtonWidget(3, this.centerX - BUTTON_WIDTH / 2, this.baseY + this.screenHeight - 42, BUTTON_WIDTH, 20, I18n.translate("reauth.gui.button.username"));
        this.addButton(this.confirm);
    }

    @Override
    public final void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        this.textRenderer.drawWithShadow(I18n.translate("reauth.gui.auth.username"), this.centerX - (BUTTON_WIDTH / 2f), this.centerY - 15, 0xFFFFFFFF);

        this.username.render();
    }

    @Override
    public final void tick() {
        super.tick();
        this.confirm.active = SessionHelper.isValidOfflineUsername(this.username.getText());
    }

    @Override
    public final void keyPressed(char typedChar, int keyCode) {
        super.keyPressed(typedChar, keyCode);
        this.username.keyPressed(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            this.performUsernameChange();
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);
        if (button.id == 3) {
            this.performUsernameChange();
        }
    }

    /**
     * Calls the to do the Login and handles Errors
     * Closes the Screen if successful
     */
    private void performUsernameChange() {
        if (SessionHelper.isValidOfflineUsername(this.username.getText())) {
            SessionHelper.setOfflineUsername(this.username.getText());
            this.requestClose(true);
        }
    }
}
