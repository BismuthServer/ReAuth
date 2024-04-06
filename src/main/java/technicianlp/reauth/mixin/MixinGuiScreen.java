package technicianlp.reauth.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import technicianlp.reauth.gui.IGuiScreen;

import java.net.URI;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

@Mixin(Screen.class)
public abstract class MixinGuiScreen implements IGuiScreen {
	@Shadow
	protected abstract <T extends ButtonWidget> T addButton(T button);

	@Override
	public <T extends ButtonWidget> T reAuth$doAddButton(T button) {
		return addButton(button);
	}

	@Shadow
	private void openLink(URI url) {}

	@Override
	public void reAuth$doOpenWebLink(URI url) {
		openLink(url);
	}
}
