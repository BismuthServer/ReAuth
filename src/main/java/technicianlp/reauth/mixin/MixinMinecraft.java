package technicianlp.reauth.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Session;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import technicianlp.reauth.session.ISessionHolder;

@Mixin(Minecraft.class)
public class MixinMinecraft implements ISessionHolder {

    @Mutable
	@Final
	@Shadow
    private Session session;

    @Override
    @Shadow
    public Session getSession() {
        return null;
    }

    @Override
    public void reAuth$setSession(Session session) {
        this.session = session;
    }

}
