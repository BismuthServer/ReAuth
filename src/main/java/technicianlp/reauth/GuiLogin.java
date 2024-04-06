package technicianlp.reauth;

import java.awt.Color;

import net.minecraft.client.gui.widget.ToggleButton;
import net.ornithemc.osl.config.api.config.option.BooleanOption;
import org.lwjgl.input.Keyboard;

import com.mojang.authlib.exceptions.AuthenticationException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import technicianlp.reauth.configuration.Config;
import technicianlp.reauth.gui.ConfigToggleWidget;

final class GuiLogin extends Screen {

	private String startingAccount;

    private TextFieldWidget username;
    private GuiPasswordField pw;
    private ButtonWidget login;
    private ButtonWidget cancel;
    private ButtonWidget offline;
    private ConfigToggleWidget save;

    private Screen successPrevScreen;
    private Screen failPrevScreen;

    private int basey;

    private String message = "";

    GuiLogin(Screen successPrevScreen, Screen failPrevScreen) {
    	this(successPrevScreen, failPrevScreen, "");
    }

    GuiLogin(Screen successPrevScreen, Screen failPrevScreen, String startingAccount) {
        this.minecraft = Minecraft.getInstance();
        this.textRenderer = minecraft.textRenderer;
        this.successPrevScreen = successPrevScreen;
        this.failPrevScreen = failPrevScreen;
        this.startingAccount = startingAccount;
    }

    @Override
    protected void buttonClicked(ButtonWidget b) {
        switch (b.id) {
            case 0:
                if (login())
                    this.minecraft.openScreen(successPrevScreen);
                break;
            case 3:
                if (playOffline())
                    this.minecraft.openScreen(successPrevScreen);
                break;
            case 1:
                this.minecraft.openScreen(failPrevScreen);
                break;
            case 2:
				this.save.toggle();
            	break;
        }

    }

    @Override
    public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        this.renderBackground();

        this.drawCenteredString(this.textRenderer, "Username/E-Mail:", this.width / 2, this.basey,
                Color.WHITE.getRGB());
        this.drawCenteredString(this.textRenderer, "Password:", this.width / 2, this.basey + 45,
                Color.WHITE.getRGB());
        if (!(this.message == null || this.message.isEmpty())) {
            this.drawCenteredString(this.textRenderer, this.message, this.width / 2, this.basey - 15, 0xFFFFFF);
        }
        this.username.render();
        this.pw.render();

        super.render(p_73863_1_, p_73863_2_, p_73863_3_);
    }

    @Override
    public void tick() {
        super.tick();
        this.username.render();
        this.pw.render();
    }

    @Override
    public void init() {
        super.init();
        Keyboard.enableRepeatEvents(true);

        this.basey = this.height / 2 - 110 / 2;

        this.username = new TextFieldWidget(0, this.textRenderer, this.width / 2 - 155, this.basey + 15, 2 * 155, 20);
        this.username.setMaxLength(512);
        this.username.setText(startingAccount);
        this.username.setFocused(true);

        this.pw = new GuiPasswordField(this.textRenderer, this.width / 2 - 155, this.basey + 60, 2 * 155, 20);
        this.pw.setPassword(Secure.accounts.get(this.username.getText()));

        this.save = new ConfigToggleWidget(2, this.width / 2 - 155, this.basey + 85, Config.SAVE_TO_CONFIG);
        this.buttons.add(this.save);

        if (!Config.OFFLINE_MODE.get()) {
            this.login = new ButtonWidget(0, this.width / 2 - 155, this.basey + 105, 153, 20, "Login");
            this.cancel = new ButtonWidget(1, this.width / 2 + 2, this.basey + 105, 155, 20, "Cancel");
            this.buttons.add(this.login);
            this.buttons.add(this.cancel);
        } else {
            this.login = new ButtonWidget(0, this.width / 2 - 155, this.basey + 105, 100, 20, "Login");
            this.offline = new ButtonWidget(3, this.width / 2 - 50, this.basey + 105, 100, 20, "Play Offline");
            this.cancel = new ButtonWidget(1, this.width / 2 + 55, this.basey + 105, 100, 20, "Cancel");
            this.buttons.add(this.login);
            this.buttons.add(this.cancel);
            this.buttons.add(this.offline);
        }
    }

    @Override
    protected void keyPressed(char c, int k) {
        super.keyPressed(c, k);
        this.username.keyPressed(c, k);
        this.pw.keyPressed(c, k);
        if (k == Keyboard.KEY_TAB) {
            this.username.setFocused(!this.username.isFocused());
            this.pw.setFocused(!this.pw.isFocused());
        } else if (k == Keyboard.KEY_RETURN) {
            if (this.username.isFocused()) {
                this.username.setFocused(false);
                this.pw.setFocused(true);
            } else if (this.pw.isFocused()) {
                this.buttonClicked(this.login);
            }
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int b) {
        super.mouseClicked(x, y, b);
        this.username.mouseClicked(x, y, b);
        this.pw.mouseClicked(x, y, b);
    }

    /**
     * used as an interface between this and the secure class
     * <p>
     * returns whether the login was successful
     */
    private boolean login() {
        try {
            Secure.login(this.username.getText(), this.pw.getPW(), this.save.getOption().get());
            this.message = (char) 167 + "aLogin successful!";
            return true;
        } catch (AuthenticationException e) {
            this.message = (char) 167 + "4Login failed: " + e.getMessage();
            ReAuth.log.error("Login failed:", e);
            return false;
        } catch (Exception e) {
            this.message = (char) 167 + "4Error: Something went wrong!";
            ReAuth.log.error("Error:", e);
            return false;
        }
    }

    /**
     * sets the name for playing offline
     */
    private boolean playOffline() {
        String username = this.username.getText();
        if (!(username.length() >= 2 && username.length() <= 16)) {
            this.message = (char) 167 + "4Error: Username needs a length between 2 and 16";
            return false;
        }
        if (!username.matches("[A-Za-z0-9_]{2,16}")) {
            this.message = (char) 167 + "4Error: Username has to be alphanumerical";
            return false;
        }
        try {
            Secure.offlineMode(username);
            return true;
        } catch (Exception e) {
            this.message = (char) 167 + "4Error: Something went wrong!";
            ReAuth.log.error("Error:", e);
            return false;
        }
    }

    @Override
    public void removed() {
        super.removed();
        this.pw.setPassword(new char[0]);
        Keyboard.enableRepeatEvents(false);
    }
}
