package technicianlp.reauth.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.skin.DefaultSkinUtils;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.resource.Identifier;
import technicianlp.reauth.authentication.YggdrasilAPI;
import technicianlp.reauth.authentication.flows.Flows;
import technicianlp.reauth.configuration.Config;
import technicianlp.reauth.configuration.Profile;
import technicianlp.reauth.configuration.ProfileConstants;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class AccountListScreen extends Screen {

	private Screen parentScreen;

	private ButtonWidget loginButton;
	private ButtonWidget cancelButton;
	private ButtonWidget addButton;
	private ButtonWidget removeButton;

	private Profile selectedProfile;
	private GuiSlotAccounts accountList;

	private final Config config;

	public AccountListScreen(Screen parentScreen) {
		this.config = Config.getInstance();
		this.parentScreen = parentScreen;
	}

	@Override
	public void init() {
		super.init();

		addButton(loginButton = new ButtonWidget(0, 10, height - 50, width / 2 - 30, 20, I18n.translate("reauth.gui.button.login")));
		addButton(cancelButton = new ButtonWidget(1, width / 2 + 20, height - 50, width / 2 - 30, 20, I18n.translate("gui.cancel")));
		addButton(addButton = new ButtonWidget(2, 10, height - 25, width / 2 - 30, 20, I18n.translate("reauth.gui.button.addaccount")));
		addButton(removeButton = new ButtonWidget(4, width / 2 + 20, height - 25, width / 2 - 30, 20, I18n.translate("reauth.gui.button.removeaccount")));

		if (config.getProfiles().isEmpty()) {
			loginButton.active = false;
			removeButton.active = false;
		} else {
			selectedProfile = config.getProfile();
		}

		accountList = new GuiSlotAccounts(minecraft, width, height, 50, height - 60, 38);

		YggdrasilAPI.initSkinStuff();
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

		drawCenteredString(textRenderer, I18n.translate("reauth.gui.ttile.accountlist"), width / 2, 10, 0xffffff);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		switch (button.id) {
		case 0:
			FlowScreen.open(Flows::loginWithProfile, selectedProfile, this.parentScreen);
			break;
		case 1:
			minecraft.openScreen(parentScreen);
			break;
		case 2:
			minecraft.openScreen(new LoginScreen(minecraft.screen));
			break;
		case 4:
			config.getProfiles().remove(selectedProfile);
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
			return config.getProfiles().size();
		}

		@Override
		protected void entryClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
			selectedProfile = config.getProfile(slotIndex);
			if (isDoubleClick) {
				AccountListScreen.this.buttonClicked(loginButton);
			}
		}

		@Override
		protected boolean isEntrySelected(int slotIndex) {
			if (selectedProfile == null) return false;
			return selectedProfile.equals(config.getProfile(slotIndex));
		}

		@Override
		protected void renderBackground() {
			AccountListScreen.this.renderBackground();
		}

		@Override
		protected void renderEntry(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
			Profile profile = config.getProfile(slotIndex);
			String username = profile.getValue(ProfileConstants.NAME);

			drawString(textRenderer, username, xPos + 50, yPos + 10, 0xffffff);

			GameProfile gameProfile = new GameProfile(null, username);
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
