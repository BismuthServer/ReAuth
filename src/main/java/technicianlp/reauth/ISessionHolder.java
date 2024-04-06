package technicianlp.reauth;

import net.minecraft.client.Session;

public interface ISessionHolder {

	Session getSession();
	
	void setSession(Session session);
	
}
