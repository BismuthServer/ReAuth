package technicianlp.reauth.gui;

import java.net.URI;
import net.minecraft.client.gui.widget.ButtonWidget;

public interface IGuiScreen {

	<T extends ButtonWidget> T reAuth$doAddButton(T button);

	void reAuth$doOpenWebLink(URI url);
}
