package technicianlp.reauth.configuration;

import net.ornithemc.osl.config.api.ConfigManager;
import net.ornithemc.osl.config.api.ConfigScope;
import net.ornithemc.osl.config.api.LoadingPhase;
import net.ornithemc.osl.config.api.config.BaseConfig;
import net.ornithemc.osl.config.api.config.option.BooleanOption;
import net.ornithemc.osl.config.api.serdes.FileSerializerType;
import net.ornithemc.osl.config.api.serdes.SerializerTypes;

import java.util.ArrayList;
import java.util.Map;

public final class Config extends BaseConfig {

	static final String GROUP = "ReAuth";
	public static final BooleanOption OFFLINE_MODE = new BooleanOption("offlineMode", "Offline mode enabled", false);
	public static final BooleanOption SAVE_TO_CONFIG = new BooleanOption("Save Password to Config", "Save Password to Config (WARNING: SECURITY RISK!)", false);
	public static final ProfileListOption PROFILE_LIST = new ProfileListOption("profiles", "Stored profiles", new ArrayList<>());

    public ArrayList<Profile> getProfiles() {return PROFILE_LIST.get();}

    public void storeProfile(Profile profile) {
        if (profile.isLoaded()) return;

        String uuid = profile.getValue(ProfileConstants.UUID, "");
		ArrayList<Profile> profiles = getProfiles();
        for (Profile profile2: profiles) {
            if (profile2.equals(profile)) return;

            if (uuid.equals(profile2.getValue(ProfileConstants.UUID))) {
                profiles.remove(profile2);
                break;
            }
        }

        profiles.add(profile);
        this.save();
    }

    public Profile getProfile() {
        return getProfile(0);
    }

    public Profile getProfile(int index) {
		ArrayList<Profile> profiles = getProfiles();
        if (!profiles.isEmpty()) {
            return profiles.get(index);
        }
        return null;
    }

    public Profile createProfile(Map<String, String> data) {
        return new Profile(data, false);
    }

    public void save() {
		// TODO work on saving
    }

	@Override
	public String getNamespace() {
		return "reauth";
	}

	@Override
	public String getName() {
		return "ReAuth";
	}

	@Override
	public String getSaveName() {
		return "reauth.json";
	}

	@Override
	public ConfigScope getScope() {
		return ConfigScope.GLOBAL;
	}

	@Override
	public LoadingPhase getLoadingPhase() {
		return LoadingPhase.READY;
	}

	@Override
	public FileSerializerType<?> getType() {
		return SerializerTypes.JSON;
	}

	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	public void init() {
		this.registerOptions(
			GROUP,
			OFFLINE_MODE,
			SAVE_TO_CONFIG,
			PROFILE_LIST
		);
	}

	private static Config instance;

	public static void register() {
		if (instance == null) {
			ConfigManager.register(instance = new Config());
		}
	}

	public static Config getInstance() {
		return instance;
	}
}
