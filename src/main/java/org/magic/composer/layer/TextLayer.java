package org.magic.composer.layer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import org.magic.composer.models.TextRole;


public class TextLayer extends AbstractLayer {

    private static final FontRenderContext FRC = new FontRenderContext(null, true, true);
    private static final int DEFAULT_ORACLE_TEXT_WIDTH = 500;
    private static final int DEFAULT_ORACLE_TEXT_HEIGHT = 220;
    private static final float MIN_FONT_SIZE = 18.0f;

    private String text;
    private float size;
    private Color color;
    private final TextRole role;


    public TextLayer(String text, TextRole role) {
        this.role = role;
        this.text = text == null ? "" : text;
        this.color = role.getColor();
        this.size = role.getFont().getSize2D();
        setName(role.name().toLowerCase());

        if (role == TextRole.SEPARATOR) {
            setText("a");
        }

        if (role == TextRole.TEXT) {
            width = DEFAULT_ORACLE_TEXT_WIDTH;
            height = DEFAULT_ORACLE_TEXT_HEIGHT;
        } else {
            var metrics = getFont().getLineMetrics("Ag", FRC);
            width = Math.max(1, (int) Math.ceil(getFont().getStringBounds(this.text, FRC).getWidth()));
            height = Math.max(1, (int) Math.ceil(metrics.getHeight()));
        }
    }


    public TextRole getRole() {
        return role;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text == null ? "" : text;
    }

    public Font getFont() {
        return role.getFont().deriveFont(size);
    }

    public void setFontSize(float size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Font size must be greater than 0");
        }
        this.size = size;
    }

    public void setWidth(int width) {
        this.width = requirePositiveDimension(width, "Width");
    }

    public void setHeight(int height) {
        this.height = requirePositiveDimension(height, "Height");
    }

    @Override
    public void paint(Graphics2D g2) {
        if (text.isEmpty()) {
            return;
        }

        var layout = layoutText();
        var zoneGraphics = (Graphics2D) g2.create();
        try {
            zoneGraphics.clipRect(x, y, width, height);
            zoneGraphics.setColor(color);
            zoneGraphics.setFont(layout.font());

            float baseline = y + layout.lineHeight();
            for (var line : layout.lines()) {
                float lineX = switch (role.getAlignement()) {
                case SwingConstants.CENTER -> x + (width - line.getAdvance()) / 2;
                case SwingConstants.RIGHT -> x + width - line.getAdvance();
                default -> x;
                };
                line.draw(zoneGraphics, lineX, baseline);
                baseline += layout.lineHeight();
            }
        } finally {
            zoneGraphics.dispose();
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    private TextLayoutData layoutText() {
        for (float candidateSize = size; candidateSize >= MIN_FONT_SIZE; candidateSize -= 0.5f) {
            var font = role.getFont().deriveFont(candidateSize);
            var layout = createLayout(font);
            if (layout.height() <= height && layout.maxWidth() <= width) {
                return layout;
            }
        }
        return createLayout(role.getFont().deriveFont(MIN_FONT_SIZE));
    }

    private TextLayoutData createLayout(Font font) {
        var lines = new ArrayList<TextLayout>();
        float maxWidth = 0;
        for (var paragraph : text.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1)) {
            if (paragraph.isEmpty()) {
                var emptyLine = new TextLayout(" ", font, FRC);
                lines.add(emptyLine);
                continue;
            }

            addWrappedLines(paragraph, font, lines);
        }

        for (var line : lines) {
            maxWidth = Math.max(maxWidth, line.getAdvance());
        }
        var lineHeight = font.getLineMetrics("Ag", FRC).getHeight();
        return new TextLayoutData(font, lines, lineHeight, maxWidth);
    }

    private void addWrappedLines(String paragraph, Font font, List<TextLayout> lines) {
        var attributedText = new AttributedString(paragraph);
        attributedText.addAttribute(TextAttribute.FONT, font);
        var measurer = new LineBreakMeasurer(attributedText.getIterator(), BreakIterator.getLineInstance(), FRC);
        while (measurer.getPosition() < paragraph.length()) {
            lines.add(measurer.nextLayout(width));
        }
    }

    private int requirePositiveDimension(int dimension, String name) {
        if (dimension <= 0) {
            throw new IllegalArgumentException(name + " must be greater than 0");
        }
        return dimension;
    }

    private record TextLayoutData(Font font, List<TextLayout> lines, float lineHeight, float maxWidth) {
        float height() {
            return lines.size() * lineHeight;
        }
    }
}
