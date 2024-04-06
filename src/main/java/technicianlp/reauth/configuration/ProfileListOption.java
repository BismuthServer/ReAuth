package technicianlp.reauth.configuration;

import java.util.ArrayList;
import java.util.function.Predicate;

import net.ornithemc.osl.config.api.config.option.BaseOption;

public class ProfileListOption extends BaseOption<ArrayList<Profile>> {
	protected ProfileListOption(String name, String description, ArrayList<Profile> defaultValue) {
		super(name, description, defaultValue);
	}

	protected ProfileListOption(String name, String description, ArrayList<Profile> defaultValue, Predicate<ArrayList<Profile>> validator) {
		super(name, description, defaultValue, validator);
	}
}
