package technicianlp.reauth.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

abstract class AbstractScreen extends Screen {

    static final int BUTTON_WIDTH = 196;

    private final String title;

    protected int baseX;
    protected int centerX;
    protected int baseY;
    protected int centerY;
    protected final int screenWidth = 300;
    protected final int screenHeight = 175;

    protected final Screen background;

    AbstractScreen(String title, Screen background) {
        this.title = title;
        this.background = background;
    }

    @Override
    public void init() {
        super.init();
        Keyboard.enableRepeatEvents(true);

        this.centerX = this.width / 2;
        this.baseX = this.centerX - this.screenWidth / 2;
        this.centerY = this.height / 2;
        this.baseY = this.centerY - this.screenHeight / 2;

        ButtonWidget cancel = new ButtonWidget(1, this.centerX + this.screenWidth / 2 - 22, this.baseY + 2, 20, 20, I18n.translate("reauth.gui.close"));
        this.addButton(cancel);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.background.render(Integer.MAX_VALUE, Integer.MAX_VALUE, partialTicks);
        this.fillGradient(0, 0, this.width, this.height, 0xc0101010, 0xd0101010);

        // modified renderDirtBackground(0);
        this.minecraft.getTextureManager().bind(BACKGROUND_LOCATION);
        GL11.glColor4f(80F / 256F, 80F / 256F, 80F / 256F, 1F);
        Screen.drawTexture(this.baseX, this.baseY, 0, 0, this.screenWidth, this.screenHeight, 32, 32);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        super.render(mouseX, mouseY, partialTicks);

        this.textRenderer.drawWithShadow(I18n.translate(this.title), this.centerX - (BUTTON_WIDTH / 2f), this.baseY + 8, 0xFFFFFFFF);
    }

    protected void requestClose(boolean completely) {
        if (completely) {
            this.minecraft.openScreen(this.background);
        } else {
            this.minecraft.openScreen(new AccountListScreen(this.background));
        }
    }

    @Override
    public void removed() {
        super.removed();
        Keyboard.enableRepeatEvents(false);
    }

    protected void keyPressed(char typedChar, int keyCode) {
        if (keyCode == 1) {
            this.requestClose(false);
        } else {
            super.keyPressed(typedChar, keyCode);
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);
        if (button.id == 1) {
            this.requestClose(false);
        }
    }
}
