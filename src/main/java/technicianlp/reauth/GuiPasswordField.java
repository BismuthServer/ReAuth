package technicianlp.reauth;

import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.TextRenderer;

final class GuiPasswordField extends TextFieldWidget {

    GuiPasswordField(TextRenderer renderer, int posx, int posy, int x, int y) {
        super(1, renderer, posx, posy, x, y);
        this.setMaxLength(512);
    }

    private char[] password = new char[0];

    final char[] getPW() {
        char[] pw = new char[password.length];
        System.arraycopy(password,0,pw,0,password.length);
        return pw;
    }

    public final boolean keyPressed(char typedChar, int keyCode) {
        if (!this.isFocused() || Screen.isCopy(keyCode) || Screen.isCut(keyCode))
            return false; // Prevent Cut/Copy
        if (Screen.isSelectAll(keyCode) || Screen.isPaste(keyCode))
            return super.keyPressed(typedChar, keyCode); // combos handled by super

        switch (keyCode) {
            case Keyboard.KEY_BACK: // backspace
            case Keyboard.KEY_DELETE:
            case Keyboard.KEY_HOME: // jump keys?
            case Keyboard.KEY_END:
            case Keyboard.KEY_LEFT: // arrowkey
            case Keyboard.KEY_RIGHT:
                return super.keyPressed(typedChar, keyCode); // special keys handled by super
            default:
                if (isAllowedCharacter(typedChar)) {
                    this.write(Character.toString(typedChar));
                    return true;
                }
                return false;
        }
    }

    public final void write(String rawInput) {
        int selStart = this.getCursor() < this.getSelectionEnd() ? this.getCursor() : this.getSelectionEnd();
        int selEnd = this.getCursor() < this.getSelectionEnd() ? this.getSelectionEnd() : this.getCursor();

        char[] input = filterAllowedCharacters(rawInput).toCharArray();
        char[] newPW = new char[selStart + password.length - selEnd + input.length];

        if (password.length != 0 && selStart > 0)
            System.arraycopy(password, 0, newPW, 0, Math.min(selStart, password.length));

        System.arraycopy(input, 0, newPW, selStart, input.length);
        int l = input.length;


        if (password.length != 0 && selEnd < password.length)
            System.arraycopy(password, selEnd, newPW, selStart + input.length, password.length - selEnd);

        setPassword(newPW);
        Arrays.fill(newPW, 'f');
        this.moveCursor(selStart - this.getSelectionEnd() + l);
    }

    @Override
    public final void eraseCharacters(int num) {
        if (password.length == 0)
            return;
        if (this.getSelectionEnd() != this.getCursor()) {
            this.write("");
        } else {
            boolean direction = num < 0;
            int start = direction ? Math.max(this.getCursor() + num, 0) : this.getCursor();
            int end = direction ? this.getCursor() : Math.min(this.getCursor() + num, password.length);

            char[] newPW = new char[start + password.length - end];


            if (start >= 0)
                System.arraycopy(password, 0, newPW, 0, start);

            if (end < password.length)
                System.arraycopy(password, end, newPW, start, password.length - end);

            setPassword(newPW);
            Arrays.fill(newPW,'f');
            if (direction)
                this.moveCursor(num);
        }
    }

    final void setPassword(char[] password) {
    	if (password == null)
    		password = new char[0];
        Arrays.fill(this.password, 'f');
        this.password = new char[password.length];
        System.arraycopy(password, 0, this.password, 0, password.length);
        updateText();
    }

    @Override
    public final void setText(String textIn) {
        setPassword(textIn.toCharArray());
        updateText();
    }

    private void updateText() {
        char[] chars = new char[password.length];
        Arrays.fill(chars, '\u25CF');
        super.setText(new String(chars));
    }

    /**
     * Allow SectionSign to be input into the field
     */
    private boolean isAllowedCharacter(int character) {
        return character == 0xa7 || SharedConstants.isValidChatChar((char) character);
    }

    /**
     * Modified version of {@link SharedConstants#stripInvalidChars(String)}
     */
    private String filterAllowedCharacters(String input) {
        StringBuilder stringbuilder = new StringBuilder();
        input.chars().filter(this::isAllowedCharacter).forEach(i -> stringbuilder.append((char) i));
        return stringbuilder.toString();
    }
}
