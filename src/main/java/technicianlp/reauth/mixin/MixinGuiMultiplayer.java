package technicianlp.reauth.mixin;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import technicianlp.reauth.gui.Handler;

@Mixin(MultiplayerScreen.class)
public class MixinGuiMultiplayer {

	@Inject(method = "init", at = @At("TAIL") )
	public void onInitGui(CallbackInfo ci) {
		Handler.openGuiMultiplayer((MultiplayerScreen) (Object) this);
	}

	@Inject(method = "render", at = @At("TAIL") )
	public void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		Handler.onGuiMultiplayerDrawScreen((MultiplayerScreen) (Object) this);
	}

	@Inject(method = "buttonClicked", at = @At("TAIL") )
	public void onActionPerformed(ButtonWidget button, CallbackInfo ci) {
		Handler.onActionPerformed(button.id);
	}

//	@Inject(method = "buttonClicked", at = @At("HEAD") )
//	public void onPreActionPerformed(ButtonWidget button, CallbackInfo ci) {
//		Handler.preGuiMultiplayerActionPerformed(button.id);
//	}

}
