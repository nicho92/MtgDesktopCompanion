package org.beta;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clavier virtuel Swing réutilisable, disposé en AZERTY ou QWERTY, avec
 * option d'inclure une rangée de chiffres.
 *
 * Utilisation typique :
 * <pre>
 *     VirtualKeyboardPanel keyboard = new VirtualKeyboardPanel(
 *             VirtualKeyboardPanel.Layout.AZERTY, true);
 *     keyboard.setKeyListener(key -> System.out.println("Touche : " + key));
 *     panel.add(keyboard);
 * </pre>
 *
 * Par défaut, une touche cliquée se désactive automatiquement
 * ({@link #setAutoDisableOnPress(boolean)} permet de changer ce comportement),
 * ce qui convient bien à des jeux type "pendu". Le composant reste toutefois
 * neutre sur la logique métier : il se contente de notifier les touches
 * pressées via {@link KeyListener}.
 */
public class VirtualKeyboardPanel extends JPanel {

    /** Disposition du clavier. */
    public enum Layout { AZERTY, QWERTY }

    /** Notifié à chaque touche pressée (si elle est activée). */
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

    // ------------------------------------------------------------------
    // Construction des rangées
    // ------------------------------------------------------------------

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
            default:
                letterRows = new String[] {
                        "QWERTYUIOP",
                        "ASDFGHJKL",
                        "ZXCVBNM"
                };
                break;
        }

        if (!includeDigits) {
            return letterRows;
        }

        String[] rows = new String[letterRows.length + 1];
        rows[0] = "1234567890";
        System.arraycopy(letterRows, 0, rows, 1, letterRows.length);
        return rows;
    }

    private JPanel buildRowPanel(String rowCharacters) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        rowPanel.setOpaque(false);
        for (char c : rowCharacters.toCharArray()) {
            JButton button = new JButton(String.valueOf(c));
            button.setMargin(new Insets(4, 4, 4, 4));
            button.setPreferredSize(new Dimension(40, 36));
            button.setFocusPainted(false);
            button.addActionListener(e -> handleKeyPress(c));
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
        JButton button = keyButtons.get(Character.toUpperCase(key));
        if (button != null) {
            button.setEnabled(enabled);
        }
    }

    /** Indique si une touche est actuellement activée. */
    public boolean isKeyEnabled(char key) {
        JButton button = keyButtons.get(Character.toUpperCase(key));
        return button != null && button.isEnabled();
    }

    /** Active ou désactive toutes les touches d'un coup. */
    public void setAllKeysEnabled(boolean enabled) {
        for (JButton button : keyButtons.values()) {
            button.setEnabled(enabled);
        }
    }

    /** Réinitialise le clavier : toutes les touches redeviennent actives. */
    public void resetKeys() {
        setAllKeysEnabled(true);
    }

    // ------------------------------------------------------------------
    // Démo autonome
    // ------------------------------------------------------------------

    /** Petite démo pour visualiser le composant seul. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Démo VirtualKeyboardPanel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JLabel output = new JLabel("Touche pressée : (aucune)", SwingConstants.CENTER);
            output.setFont(output.getFont().deriveFont(Font.BOLD, 16f));

            VirtualKeyboardPanel keyboard = new VirtualKeyboardPanel(Layout.AZERTY, true);
            keyboard.setAutoDisableOnPress(false);
            keyboard.setKeyListener(key -> output.setText("Touche pressée : " + key));

            JButton resetButton = new JButton("Réinitialiser");
            resetButton.addActionListener(e -> {
                keyboard.resetKeys();
                output.setText("Touche pressée : (aucune)");
            });

            JPanel content = new JPanel(new BorderLayout(10, 10));
            content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
            content.add(output, BorderLayout.NORTH);
            content.add(keyboard, BorderLayout.CENTER);
            content.add(resetButton, BorderLayout.SOUTH);

            frame.setContentPane(content);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
