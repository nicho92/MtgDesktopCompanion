package org.magic.api.beans.layer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.ImageGraphicAttribute;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.SwingConstants;

import org.magic.api.beans.abstracts.AbstractLayer;
import org.magic.api.beans.layer.enums.TextRole;
import org.magic.services.providers.IconsProvider;
import org.magic.services.tools.ImageTools;


public class TextLayer extends AbstractLayer {

    private static final FontRenderContext FRC = new FontRenderContext(null, true, true);
    private static final int DEFAULT_ORACLE_TEXT_WIDTH = 500;
    private static final int DEFAULT_ORACLE_TEXT_HEIGHT = 220;
    private static final float MIN_FONT_SIZE = 18.0f;
    private static final Pattern MANA_SYMBOL_PATTERN = Pattern.compile("\\{([^}]+)}");
    private static final char INLINE_IMAGE_CHARACTER = '\uFFFC';

    private String text;
    private float size;
    private Color color;
    private TextRole role;


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
           setWidth(DEFAULT_ORACLE_TEXT_WIDTH);
           setHeight(DEFAULT_ORACLE_TEXT_HEIGHT);
        } else {
            var metrics = getFont().getLineMetrics("Ag", FRC);
            setWidth( Math.max(1, (int) Math.ceil(getFont().getStringBounds(this.text, FRC).getWidth())));
            setHeight(Math.max(1, (int) Math.ceil(metrics.getHeight())));
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
	if(size>0)
	    this.size = size;
    }

    @Override
    public void setWidth(int width) {
       super.setWidth(requirePositiveDimension(width, "Width"));
    }

    @Override
    public void setHeight(int height) {
	super.setHeight(requirePositiveDimension(height, "Height"));
    }

    @Override
    public void paintLayer(Graphics2D g2) {
        if (text.isEmpty()) {
            return;
        }

        var layout = layoutText();
        var zoneGraphics = (Graphics2D) g2.create();
        try {
            zoneGraphics.clipRect(getX(), getY(), getWidth(), getHeight());
            zoneGraphics.setColor(color);
            zoneGraphics.setFont(layout.font());

            float baseline = getY() + layout.lineHeight();
            for (var line : layout.lines()) {
                float lineX = switch (role.getAlignement()) {
                case SwingConstants.CENTER -> getX() + (getWidth() - line.getAdvance()) / 2;
                case SwingConstants.RIGHT -> getX() + getWidth() - line.getAdvance();
                default -> getX();
                };
                line.draw(zoneGraphics, lineX, baseline);
                baseline += layout.lineHeight();
            }
        } finally {
            zoneGraphics.dispose();
        }
    }

    private TextLayoutData layoutText() {
        for (float candidateSize = size; candidateSize >= MIN_FONT_SIZE; candidateSize -= 0.5f) {
            var font = role.getFont().deriveFont(candidateSize);
            var layout = createLayout(font);
            if (layout.height() <= getHeight() && layout.maxWidth() <= getWidth()) {
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
        var attributedText = createAttributedText(paragraph, font);
        attributedText.addAttribute(TextAttribute.FONT, font);
        var iterator = attributedText.getIterator();
        var measurer = new LineBreakMeasurer(iterator, BreakIterator.getLineInstance(), FRC);
        while (measurer.getPosition() < iterator.getEndIndex()) {
            lines.add(measurer.nextLayout(getWidth()));
        }
    }

    /**
     * Replaces mana tokens such as {@code {U}} and {@code {T}} with inline images.
     * TextLayout treats the replacement character as a regular glyph, so wrapping and
     * alignment continue to work for text that contains Magic symbols.
     */
    private AttributedString createAttributedText(String paragraph, Font font) {
        var matcher = MANA_SYMBOL_PATTERN.matcher(paragraph);
        var renderedText = new StringBuilder();
        var symbolPositions = new ArrayList<Integer>();
        var symbolImages = new ArrayList<BufferedImage>();
        var previousEnd = 0;

        while (matcher.find()) {
            var symbolImage = getSymbolImage(matcher.group(1), font);
            if (symbolImage == null) {
                continue;
            }

            renderedText.append(paragraph, previousEnd, matcher.start());
            symbolPositions.add(renderedText.length());
            symbolImages.add(symbolImage);
            renderedText.append(INLINE_IMAGE_CHARACTER);
            previousEnd = matcher.end();
        }
        renderedText.append(paragraph, previousEnd, paragraph.length());

        var attributedText = new AttributedString(renderedText.toString());
        var symbolBaseline = font.getLineMetrics("Ag", FRC).getAscent();
        for (var index = 0; index < symbolPositions.size(); index++) {
            var position = symbolPositions.get(index);
            attributedText.addAttribute(TextAttribute.CHAR_REPLACEMENT,
                    new ImageGraphicAttribute(symbolImages.get(index), ImageGraphicAttribute.ROMAN_BASELINE, 0,
                            symbolBaseline),
                    position, position + 1);
        for (var index = 0; index < symbolPositions.size(); index++) {
            var position = symbolPositions.get(index);
            attributedText.addAttribute(TextAttribute.CHAR_REPLACEMENT,
                    new ImageGraphicAttribute(symbolImages.get(index), ImageGraphicAttribute.ROMAN_BASELINE), position,
                    position + 1);
        }
        return attributedText;
    }

    private BufferedImage getSymbolImage(String symbol, Font font) {
        var image = IconsProvider.getInstance().getManaSymbol(symbol);
        if (image == null) {
            return null;
        }

        var symbolSize = Math.max(1, Math.round(font.getSize2D()));
        return ImageTools.resize(image, symbolSize, symbolSize);
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
