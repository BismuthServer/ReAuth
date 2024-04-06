package technicianlp.reauth.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import technicianlp.reauth.gui.Handler;

@Mixin(TitleScreen.class)
public class MixinGuiMainMenu {

	@Inject(method = "init", at = @At("TAIL") )
	public void onInitGui(CallbackInfo ci) {
		Handler.openGuiMainMenu((TitleScreen) (Object) this);
	}

	@Inject(method = "buttonClicked", at = @At("TAIL") )
	public void onActionPerformed(ButtonWidget button, CallbackInfo ci) {
		Handler.onActionPerformed(button.id);
	}

}
