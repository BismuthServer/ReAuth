package technicianlp.reauth.authentication;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.server.GameProfileCache;
import net.minecraft.server.MinecraftServer;
import technicianlp.reauth.authentication.dto.yggdrasil.JoinServerRequest;
import technicianlp.reauth.authentication.dto.yggdrasil.JoinServerResponse;
import technicianlp.reauth.authentication.http.HttpUtil;
import technicianlp.reauth.authentication.http.Response;
import technicianlp.reauth.authentication.http.UnreachableServiceException;
import technicianlp.reauth.crypto.Crypto;

import java.io.File;
import java.math.BigInteger;
import java.util.UUID;

/**
 * cut down reimplementation version of {@link YggdrasilUserAuthentication}
 */
public final class YggdrasilAPI {

    private static final String urlJoin = "https://sessionserver.mojang.com/session/minecraft/join";

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
     * checks validity of accessToken by invoking the joinServer endpoint
     * <p>
     * reimplementation of {@link YggdrasilMinecraftSessionService#joinServer(GameProfile, String, String)}
     * Server hash is generated like during standard login sequence
     */
    public static boolean validate(String accessToken, String uuid) throws UnreachableServiceException {
        String hash = new BigInteger(Crypto.randomBytes(20)).toString(16);
        JoinServerRequest request = new JoinServerRequest(accessToken, uuid, hash);
        Response<JoinServerResponse> response = HttpUtil.performWrappedJsonRequest(urlJoin, request);
        return response.isValid();
    }
}
