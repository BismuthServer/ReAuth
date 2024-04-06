package technicianlp.reauth;

import net.minecraft.client.gui.widget.ButtonWidget;

public interface IGuiScreen {

	<T extends ButtonWidget> T doAddButton(T button);
	
}
