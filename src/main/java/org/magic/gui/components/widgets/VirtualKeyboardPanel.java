package org.magic.gui.components.widgets;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class VirtualKeyboardPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public enum Layout { AZERTY, QWERTY, ORDERED }

    public interface KeyListener {
        void onKeyPressed(char key);
    }

    private final Map<Character, JButton> keyButtons = new LinkedHashMap<>();
    private KeyListener listener;
    private boolean autoDisableOnPress = false;

    public VirtualKeyboardPanel(Layout layout) {
        this(layout, true);
    }

    /**
     * @param layout        disposition AZERTY ou QWERTY
     * @param includeDigits si true, ajoute une rangée de chiffres 1 à 0 au-dessus des lettres
     */
    public VirtualKeyboardPanel(Layout layout, boolean includeDigits) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        for (String row : buildRows(layout, includeDigits)) {
            add(buildRowPanel(row));
        }
    }

    private String[] buildRows(Layout layout, boolean includeDigits) {
        String[] letterRows;
        switch (layout) {
            case AZERTY:
                letterRows = new String[] {
                        "AZERTYUIOP",
                        "QSDFGHJKLM",
                        "WXCVBN"
                };
                break;
            case QWERTY:
                letterRows = new String[] {
                        "QWERTYUIOP",
                        "ASDFGHJKL",
                        "ZXCVBNM"
                };
                break;
            default:
        	 letterRows = new String[] {
                         "ABCDEFGHI",
                         "JKLMNOPQR",
                         "STUVWXZ"
                 };
                
                
        }

        if (!includeDigits) {
            return letterRows;
        }

        var rows = new String[letterRows.length + 1];
        rows[0] = "1234567890";
        System.arraycopy(letterRows, 0, rows, 1, letterRows.length);
        return rows;
    }

    private JPanel buildRowPanel(String rowCharacters) {
        var rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        rowPanel.setOpaque(false);
        for (var c : rowCharacters.toCharArray()) {
            var button = new JButton(String.valueOf(c));
            button.setMargin(new Insets(4, 4, 4, 4));
            button.setPreferredSize(new Dimension(40, 36));
            button.setFocusPainted(false);
            button.addActionListener(_ -> handleKeyPress(c));
            keyButtons.put(c, button);
            rowPanel.add(button);
        }
        return rowPanel;
    }

    private void handleKeyPress(char key) {
        if (autoDisableOnPress) {
            keyButtons.get(key).setEnabled(false);
        }
        if (listener != null) {
            listener.onKeyPressed(key);
        }
    }

    // ------------------------------------------------------------------
    // API publique
    // ------------------------------------------------------------------

    /** Définit le callback appelé à chaque touche pressée. */
    public void setKeyListener(KeyListener listener) {
        this.listener = listener;
    }

    /** Si true (par défaut), une touche se désactive automatiquement une fois pressée. */
    public void setAutoDisableOnPress(boolean autoDisableOnPress) {
        this.autoDisableOnPress = autoDisableOnPress;
    }

    /** Active ou désactive une touche précise (lettre ou chiffre). */
    public void setKeyEnabled(char key, boolean enabled) {
        var button = keyButtons.get(Character.toUpperCase(key));
        if (button != null) {
            button.setEnabled(enabled);
        }
    }

    /** Indique si une touche est actuellement activée. */
    public boolean isKeyEnabled(char key) {
        var button = keyButtons.get(Character.toUpperCase(key));
        return button != null && button.isEnabled();
    }

    /** Active ou désactive toutes les touches d'un coup. */
    public void setAllKeysEnabled(boolean enabled) {
        for (var button : keyButtons.values()) {
            button.setEnabled(enabled);
        }
    }

    /** Réinitialise le clavier : toutes les touches redeviennent actives. */
    public void resetKeys() {
        setAllKeysEnabled(true);
    }
}
