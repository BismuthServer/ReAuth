package technicianlp.reauth.session;

import net.minecraft.client.Session;

public interface ISessionHolder {

    Session getSession();

    void reAuth$setSession(Session session);

}
