package technicianlp.reauth.gui;

import net.ornithemc.osl.config.api.config.option.BooleanOption;
import technicianlp.reauth.authentication.flows.Flows;

import java.awt.*;
import java.io.IOException;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import technicianlp.reauth.configuration.Config;

final class LoginScreen extends AbstractScreen {
    private ButtonWidget loginAuthCode;
    private ButtonWidget loginDeviceCode;
    private ButtonWidget offline;
    private ConfigToggleWidget save;

    private int basey;

    private String message = "";

    LoginScreen(Screen background) {super("reauth.gui.title.main", background);}

    @Override
    protected void buttonClicked(ButtonWidget b) {
        super.buttonClicked(b);
        switch (b.id) {
            case 0: // Save checked
                this.save.toggle();
                break;
            case 2: // LoginAuthCode
                FlowScreen.open(Flows::loginWithAuthCode, this.save.getOption().get(), this.background);
                break;
            case 3: // LoginDeviceCode
                FlowScreen.open(Flows::loginWithDeviceCode, this.save.getOption().get(), this.background);
                break;
            case 4: // Offline
                this.minecraft.openScreen(new OfflineLoginScreen(this.background));
                break;
        }

    }

    @Override
    public void render(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        super.render(p_73863_1_, p_73863_2_, p_73863_3_);

        this.drawString(this.textRenderer, I18n.translate("reauth.gui.text.microsoft"), this.centerX - 105, this.basey, Color.WHITE.getRGB());
        this.drawString(this.textRenderer, I18n.translate("reauth.gui.text.offline"), this.centerX - 105, this.basey + 63, Color.WHITE.getRGB());

//        if (!(this.message == null || this.message.isEmpty())) {
//            this.drawCenteredString(this.fontRenderer, this.message, this.width / 2, this.basey - 15, 0xFFFFFF);
//        }

    }

    @Override
    public void init() {
        super.init();

        this.basey = this.centerY - 40;

        this.loginAuthCode = new ButtonWidget(2, this.centerX - 105, this.basey + 12, 100, 20, I18n.translate("reauth.gui.button.authcode"));
        this.buttons.add(this.loginAuthCode);
        this.loginDeviceCode = new ButtonWidget(3, this.centerX + 5, this.basey + 12, 100, 20, I18n.translate("reauth.gui.button.devicecode"));
        this.buttons.add(this.loginDeviceCode);

        this.save = new ConfigToggleWidget(0, this.centerX - 105, this.basey + 35, Config.SAVE_TO_CONFIG);
        this.buttons.add(this.save);

        this.offline = new ButtonWidget(4, this.centerX - 105, this.basey + 75, 210, 20, I18n.translate("reauth.gui.button.offline"));
        this.buttons.add(this.offline);
    }

    /**
     * used as an interface between this and the secure class
     * <p>
     * returns whether the login was successful
     */
    private boolean login() {
//        try {
//            Secure.login("this.username.getText(", "this.pw.getPW()".toCharArray(), this.save.checked);
//            this.message = (char) 167 + "aLogin successful!";
//            return true;
//        } catch (AuthenticationException e) {
//            this.message = (char) 167 + "4Login failed: " + e.getMessage();
//            LiteModReAuth.log.error("Login failed:", e);
//            return false;
//        } catch (Exception e) {
//            this.message = (char) 167 + "4Error: Something went wrong!";
//            LiteModReAuth.log.error("Error:", e);
//            return false;
//        }
        return false;
    }
}
