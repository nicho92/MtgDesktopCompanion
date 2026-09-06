package org.beta.composer.gui.listeners;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SimpleDocumentListener
        implements DocumentListener {

    private final Runnable runnable;

    public SimpleDocumentListener(
            Runnable runnable
    ) {
        this.runnable = runnable;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        runnable.run();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        runnable.run();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        runnable.run();
    }
}