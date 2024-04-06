package technicianlp.reauth.configuration;

import technicianlp.reauth.authentication.SessionData;
import technicianlp.reauth.authentication.flows.Tokens;
import technicianlp.reauth.crypto.ProfileEncryption;

import java.util.HashMap;
import java.util.Map;

public final class ProfileBuilder {

    private final Map<String, String> profile = new HashMap<>();
    private final ProfileEncryption encryption;

    public ProfileBuilder(SessionData session, ProfileEncryption encryption) {
        this.encryption = encryption;
        this.profile.put(ProfileConstants.NAME, session.username);
        this.profile.put(ProfileConstants.UUID, session.uuid);
        encryption.saveToProfile(this.profile);
    }

    public final Profile buildMicrosoft(Tokens tokens) {
        this.profile.put(ProfileConstants.PROFILE_TYPE, ProfileConstants.PROFILE_TYPE_MICROSOFT);
        this.profile.put(ProfileConstants.XBL_TOKEN, this.encryption.encryptFieldOne(tokens.getXblToken()));
        this.profile.put(ProfileConstants.REFRESH_TOKEN, this.encryption.encryptFieldTwo(tokens.getRefreshToken()));

        return Config.getInstance().createProfile(this.profile);
    }
}
