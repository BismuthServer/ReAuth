package technicianlp.reauth.gui;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.osl.config.api.config.option.BooleanOption;

public class ConfigToggleWidget extends ButtonWidget {

	private final BooleanOption option;

	public ConfigToggleWidget(int id, int x, int y) {
		this(id, x, y, null);
	}

	public ConfigToggleWidget(int id, int x, int y, BooleanOption option) {
		super(id, x, y, 150, 20, "");

		if (option.get()) {
			this.message = option.getName() + ": " + I18n.translate("options.on");
		} else {
			this.message = option.getName() + ": " + I18n.translate("options.off");
		}
		this.option = option;
	}

	public BooleanOption getOption() {
		return this.option;
	}

	public void toggle() {
		this.option.set(!this.option.get());

		if (this.option.get()) {
			this.message = this.option.getName() + ": " + I18n.translate("options.on");
		} else {
			this.message = this.option.getName() + ": " + I18n.translate("options.off");
		}
	}
}
