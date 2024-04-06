package technicianlp.reauth;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.skin.DefaultSkinUtils;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.resource.Identifier;
import technicianlp.reauth.configuration.Config;

public class GuiAccountList extends Screen {

	private Screen parentScreen;

	private ButtonWidget loginButton;
	private ButtonWidget cancelButton;
	private ButtonWidget addButton;
	private ButtonWidget editButton;
	private ButtonWidget removeButton;

	private String selectedAccount = "";
	private GuiSlotAccounts accountList;

	public GuiAccountList(Screen parentScreen) {
		this.parentScreen = parentScreen;
	}

	@Override
	public void init() {
		super.init();

		addButton(loginButton = new ButtonWidget(0, 10, height - 50, width / 2 - 30, 20, "Login"));
		addButton(cancelButton = new ButtonWidget(1, width / 2 + 20, height - 50, width / 2 - 30, 20,
				I18n.translate("gui.cancel")));
		addButton(addButton = new ButtonWidget(2, 10, height - 25, width / 3 - 40, 20, "Add Account"));
		addButton(editButton = new ButtonWidget(3, width / 3 + 20, height - 25, width / 3 - 40, 20, "Edit account"));
		addButton(
				removeButton = new ButtonWidget(4, width * 2 / 3 + 30, height - 25, width / 3 - 40, 20, "Remove account"));
		if (Secure.accounts.isEmpty()) {
			loginButton.active = false;
			editButton.active = false;
			removeButton.active = false;
		} else {
			selectedAccount = Secure.accounts.keySet().iterator().next();
		}

		accountList = new GuiSlotAccounts(minecraft, width, height, 50, height - 60, 38);

		Secure.initSkinStuff();
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		accountList.handleMouse();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		accountList.render(mouseX, mouseY, partialTicks);
		super.render(mouseX, mouseY, partialTicks);

		drawCenteredString(textRenderer, "Account List", width / 2, 10, 0xffffff);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		switch (button.id) {
		case 0:
			char[] pw = Secure.accounts.get(selectedAccount);
			if (pw == null) {
				minecraft.openScreen(new GuiLogin(parentScreen, this, selectedAccount));
			} else {
				try {
					Secure.login(selectedAccount, pw, true);
					minecraft.openScreen(parentScreen);
				} catch (AuthenticationException e) {
					minecraft.openScreen(new FatalErrorScreen("ReAuth", "Authentication Failed"));
				}
			}
			break;
		case 1:
			minecraft.openScreen(parentScreen);
			break;
		case 2:
			minecraft.openScreen(new GuiLogin(parentScreen, this));
			break;
		case 3:
			minecraft.openScreen(new GuiLogin(parentScreen, this, selectedAccount));
			break;
		case 4:
			Secure.accounts.remove(selectedAccount);
			if (Secure.accounts.isEmpty())
				minecraft.openScreen(parentScreen);
			else
				selectedAccount = Secure.accounts.keySet().iterator().next();
			Config.getInstance().save();
			break;
		}
	}

	private class GuiSlotAccounts extends ListWidget {

		public GuiSlotAccounts(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
			super(mcIn, width, height, topIn, bottomIn, slotHeightIn);
		}

		@Override
		protected int size() {
			return Secure.accounts.size();
		}

		@Override
		protected void entryClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
			int i = 0;
			for (String accName : Secure.accounts.keySet()) {
				if (i == slotIndex) {
					selectedAccount = accName;
					break;
				}
				i++;
			}
			if (isDoubleClick) {
				GuiAccountList.this.buttonClicked(loginButton);
			}
		}

		@Override
		protected boolean isEntrySelected(int slotIndex) {
			int i = 0;
			for (String accName : Secure.accounts.keySet()) {
				if (i == slotIndex)
					return selectedAccount.equals(accName);
				i++;
			}
			return false;
		}

		@Override
		protected void renderBackground() {
			GuiAccountList.this.renderBackground();
		}

		@Override
		protected void renderEntry(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn,
				float partialTicks) {
			String username = "";
			int i = 0;
			for (String accName : Secure.accounts.keySet()) {
				if (i == slotIndex) {
					username = accName;
					break;
				}
				i++;
			}

			String displayName = Secure.displayNames.get(username);
			drawString(textRenderer, displayName, xPos + 50, yPos + 10, 0xffffff);

			GameProfile gameProfile = new GameProfile(null, displayName);
			gameProfile = SkullBlockEntity.updateProfile(gameProfile);
			Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> profileTextures = Minecraft.getInstance()
					.getSkinManager().getTextures(gameProfile);
			Identifier skinLocation;
			if (profileTextures.containsKey(MinecraftProfileTexture.Type.SKIN)) {
				skinLocation = Minecraft.getInstance().getSkinManager().register(
						profileTextures.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
			} else {
				UUID id = PlayerEntity.getUuid(gameProfile);
				skinLocation = DefaultSkinUtils.getDefaultSkin(id);
			}

			Minecraft.getInstance().getTextureManager().bind(skinLocation);
			drawTexture(xPos + 1, yPos + 1, 8, 8, 8, 8, 32, 32, 64, 64);
		}

	}

}
