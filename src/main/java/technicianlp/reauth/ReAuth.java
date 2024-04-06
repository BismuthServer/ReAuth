package technicianlp.reauth;

import net.minecraft.client.resource.language.I18n;
import net.ornithemc.osl.config.api.serdes.SerializationSettings;
import net.ornithemc.osl.config.api.serdes.config.option.JsonOptionSerializer;
import net.ornithemc.osl.config.api.serdes.config.option.JsonOptionSerializers;
import net.ornithemc.osl.core.api.json.JsonFile;
import net.ornithemc.osl.entrypoints.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import technicianlp.reauth.configuration.Config;
import technicianlp.reauth.configuration.Profile;
import technicianlp.reauth.configuration.ProfileListOption;
import technicianlp.reauth.mojangfix.MojangJavaFix;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

public class ReAuth implements ModInitializer {
	public static final ExecutorService executor;
	static final Logger log = LogManager.getLogger("ReAuth");
	public static final BiFunction<String, Object[], String> i18n = I18n::translate;

	static {
		MojangJavaFix.fixMojangJava();

		executor = Executors.newCachedThreadPool(new ReAuthThreadFactory());
	}

	public static Logger getLog() {return log;}

	@Override
	public void init() {
		Config.register();

		JsonOptionSerializers.register(ProfileListOption.class, new JsonOptionSerializer<ProfileListOption>() {
			@Override
			public void serialize(ProfileListOption option, SerializationSettings settings, JsonFile json) throws IOException {
				List<Profile> profileList = option.get();
				json.writeArray(jsonArray -> {
					for (Profile profile : profileList) {
						jsonArray.writeObject(jsonObject -> {
							for (Map.Entry<String, String> entry : profile.getConfig().entrySet()) {
								jsonObject.writeString(entry.getKey(), entry.getValue());
							}
						});
					}
				});
			}

			@Override
			public void deserialize(ProfileListOption option, SerializationSettings settings, JsonFile json) throws IOException {
				List<Profile> profileList = option.get();

				json.readArray(jsonArray -> {
					if (jsonArray.hasNext()) {
						jsonArray.readObject(jsonObject -> {
							Map<String, String> data = new HashMap<>();

							while (jsonObject.hasNext()) {
								String key = jsonObject.readName();
								String value = jsonObject.readString();

								data.put(key, value);
							}

							profileList.add(Config.getInstance().createProfile(data));
						});
					}
				});
			}
		});
	}

	private static final class ReAuthThreadFactory implements ThreadFactory {
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final ThreadGroup group = new ThreadGroup("ReAuth");

		@Override
		public Thread newThread(@NotNull Runnable runnable) {
			Thread t = new Thread(this.group, runnable, "ReAuth-" + this.threadNumber.getAndIncrement());
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}
}
