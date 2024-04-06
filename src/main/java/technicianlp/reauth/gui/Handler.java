package technicianlp.reauth.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Session;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import technicianlp.reauth.session.SessionChecker;
import technicianlp.reauth.session.SessionStatus;

public final class Handler {
    public static void openGuiMultiplayer(MultiplayerScreen gui) {
        ((IGuiScreen) gui).reAuth$doAddButton(new ButtonWidget(17325, 5, 5, 100, 20, I18n.translate("reauth.gui.button")));
    }

    public static void openGuiMainMenu(TitleScreen gui) {
    	// Support for Custom Main Menu (add button outside of viewport)
        ((IGuiScreen) gui).reAuth$doAddButton(new ButtonWidget(17325, -50, -50, 20, 20, I18n.translate("reauth.gui.title.main")));
    }

    public static void onGuiMultiplayerDrawScreen(MultiplayerScreen gui) {
        Session user = Minecraft.getInstance().getSession();
        SessionStatus state = SessionChecker.getSessionStatus(user.getAccessToken(), user.getUuid());
        Minecraft.getInstance().textRenderer.drawWithShadow(I18n.translate(state.getTranslationKey()), 110, 10, 0xFFFFFFFF);
    }

    public static void onActionPerformed(int buttonId) {
        if (buttonId == 17325) {
            Minecraft.getInstance().openScreen(new AccountListScreen(Minecraft.getInstance().screen));
        }
    }
}
