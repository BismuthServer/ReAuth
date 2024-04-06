package technicianlp.reauth;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Session;
import net.minecraft.server.GameProfileCache;
import net.minecraft.server.MinecraftServer;
import technicianlp.reauth.configuration.Config;

final class Secure {

	/**
	 * Username/email -> display name map
	 */
	static Map<String, String> displayNames = new HashMap<>();
    /**
     * Username/email -> password map
     */
    static Map<String, char[]> accounts = new LinkedHashMap<>();
    static String offlineUsername = "";

    /**
     * Mojang authentificationservice
     */
    private static final YggdrasilAuthenticationService yas;
    private static final YggdrasilUserAuthentication yua;
    private static final YggdrasilMinecraftSessionService ymss;

    static {
        /* initialize the authservices */
        yas = new YggdrasilAuthenticationService(Minecraft.getInstance().getNetworkProxy(), UUID.randomUUID().toString());
        yua = (YggdrasilUserAuthentication) yas.createUserAuthentication(Agent.MINECRAFT);
        ymss = (YggdrasilMinecraftSessionService) yas.createMinecraftSessionService();
    }

    public static void initSkinStuff() {
        GameProfileRepository gpr = yas.createProfileRepository();
        GameProfileCache ppc = new GameProfileCache(gpr, new File(Minecraft.getInstance().runDir, MinecraftServer.USER_CACHE_FILE.getName()));
        SkullBlockEntity.setPlayerCache(ppc);
        SkullBlockEntity.setSessionService(ymss);
    }

    /**
     * Logs you in; replaces the Session in your client; and saves to config
     */
    static void login(String user, char[] pw, boolean savePassToConfig) throws AuthenticationException {
        /* set credentials */
        Secure.yua.setUsername(user);
        Secure.yua.setPassword(new String(pw));

        /* login */
        Secure.yua.logIn();

        ReAuth.log.info("Login successful!");

        /* put together the new Session with the auth-data */
        String username = Secure.yua.getSelectedProfile().getName();
        String uuid = UUIDTypeAdapter.fromUUID(Secure.yua.getSelectedProfile().getId());
        String access = Secure.yua.getAuthenticatedToken();
        String type = Secure.yua.getUserType().getName();
        Sessionutil.set(new Session(username, uuid, access, type));

        /* logout to discard the credentials in the object */
        Secure.yua.logOut();

        /* save username and password to config */
        Secure.accounts.put(user, savePassToConfig ? pw : null);
        Secure.displayNames.put(user, username);

		Config.getInstance().save();
    }

    static void offlineMode(String username) {
        /* Create offline uuid */
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charsets.UTF_8));
        Sessionutil.set(new Session(username, uuid.toString(), "invalid", "legacy"));
        ReAuth.log.info("Offline Username set!");
        Secure.offlineUsername = username;
    }

    /**
     * checks online if the session is valid
     */
    static boolean SessionValid() {
        try {
            GameProfile gp = Sessionutil.get().getProfile();
            String token = Sessionutil.get().getAccessToken();
            String id = UUID.randomUUID().toString();

            Secure.ymss.joinServer(gp, token, id);
            if (Secure.ymss.hasJoinedServer(gp, id, null).isComplete()) {
            	ReAuth.log.info("Session validation successful");
                return true;
            }
        } catch (Exception e) {
        	ReAuth.log.info("Session validation failed: " + e.getMessage());
            return false;
        }
        ReAuth.log.info("Session validation failed!");
        return false;
    }

    static final class Sessionutil {
        static Session get() {
            return Minecraft.getInstance().getSession();
        }

        static void set(Session s) {
            ((ISessionHolder) Minecraft.getInstance()).setSession(s);
            GuiHandler.invalidateStatus();
        }
    }

}
