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
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.SwingConstants;

import org.magic.api.beans.abstracts.AbstractLayer;
import org.magic.api.beans.enums.EnumCardsPatterns;
import org.magic.api.beans.layer.enums.TextRole;
import org.magic.services.providers.IconsProvider;
import org.magic.services.tools.ImageTools;


public class TextLayer extends AbstractLayer {
    private static final FontRenderContext FRC = new FontRenderContext(null, true, true);
    private static final int DEFAULT_ORACLE_TEXT_WIDTH = 500;
    private static final int DEFAULT_ORACLE_TEXT_HEIGHT = 220;
    private static final float MIN_FONT_SIZE = 18.0f;
    private static final float FLAVOR_GAP_RATIO = 0.4f; // gap between rules and flavor, in line heights
    private static final Pattern MANA_SYMBOL_PATTERN = Pattern.compile(EnumCardsPatterns.MANA_PATTERN.getPattern());
    private static final Pattern REMINDER_PATTERN = Pattern.compile(EnumCardsPatterns.REMINDER.getPattern());
    
    private static final char INLINE_IMAGE_CHARACTER = '\uFFFC';

    private String text;
    private String flavor;
    private int flavorOffsetY ;
    private float size;
    private Color color;
    private TextRole role;
    private double spaceTracking;
    
    public TextLayer() {
	this.flavor="";
	flavorOffsetY=0;
	spaceTracking=0f;
    }
    

    public TextLayer(String text, TextRole role) {
	this();
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
    
    public double getSpaceTracking() {
	return spaceTracking;
    }
    
    public void setSpaceTracking(double spaceTracking) {
	this.spaceTracking = spaceTracking;
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

    public String getFlavor() {
        return flavor;
    }

    /** Flavor text, drawn in italic below (or at the bottom of) the rules text. Only used by {@link TextRole#TEXT}. */
    public void setFlavor(String flavor) {
        this.flavor = flavor == null ? "" : flavor;
    }

    public int getFlavorOffsetY() {
        return flavorOffsetY;
    }

    /** Additional vertical shift (pixels, positive = down) applied to the flavor block. */
    public void setFlavorOffsetY(int flavorOffsetY) {
        this.flavorOffsetY = flavorOffsetY;
    }
    
    public Font getFont() {
	
	if(spaceTracking>0)
	{
	    var attrs = new HashMap<TextAttribute, Object>();
		attrs.put(TextAttribute.TRACKING,  spaceTracking);         
		attrs.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
		return role.getFont().deriveFont(attrs).deriveFont(size);
	}
	
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

    private boolean hasFlavor() {
        return role == TextRole.TEXT && flavor!=null && !flavor.isEmpty();
    }

    @Override
    public void paintLayer(Graphics2D g2) {
        if (text.isEmpty() && !hasFlavor()) {
            return;
        }

        var layout = layoutText();
        var zoneGraphics = (Graphics2D) g2.create();
        try {
            zoneGraphics.clipRect(getX(), getY(), getWidth(), getHeight());
            zoneGraphics.setColor(color);
            zoneGraphics.setFont(layout.font());

            float baseline = getY() + layout.ascent();
            baseline = drawLines(zoneGraphics, layout.lines(), baseline, layout.lineHeight());

            if (!layout.flavorLines().isEmpty()) {
                float flavorBaseline = getY() + getHeight() - layout.flavorHeight() + layout.ascent();
                flavorBaseline += flavorOffsetY;
                drawLines(zoneGraphics, layout.flavorLines(), flavorBaseline, layout.lineHeight());
            }
        } finally {
            zoneGraphics.dispose();
        }
    }

    /** Draws the lines starting at the given baseline and returns the baseline of the next line. */
    private float drawLines(Graphics2D g, List<TextLayout> lines, float baseline, float lineHeight) {
        for (var line : lines) {
            float lineX = switch (role.getAlignement()) {
            case SwingConstants.CENTER -> getX() + (getWidth() - line.getAdvance()) / 2;
            case SwingConstants.RIGHT -> getX() + getWidth() - line.getAdvance();
            default -> getX();
            };
            line.draw(g, lineX, baseline);
            baseline += lineHeight;
        }
        return baseline;
    }

    private TextLayoutData layoutText() {
        for (float candidateSize = size; candidateSize >= MIN_FONT_SIZE; candidateSize -= 0.5f) {
            var font = getFont().deriveFont(candidateSize);
            var layout = createLayout(font);
            
            if (layout.height() <= getHeight() && layout.maxWidth() <= getWidth()) {
                return layout;
            }
        }
        return createLayout(getFont().deriveFont(MIN_FONT_SIZE));
    }

    private TextLayoutData createLayout(Font font) {
        var lines = new ArrayList<TextLayout>();
        var flavorLines = new ArrayList<TextLayout>();

        if (!text.isEmpty()) {
            addParagraphs(text, font, lines, true);
        }
        if (hasFlavor()) {
            addParagraphs(flavor, getFlavorFont(font), flavorLines, false);
        }

        float maxWidth = 0;
        for (var line : lines) {
            maxWidth = Math.max(maxWidth, line.getAdvance());
        }
        for (var line : flavorLines) {
            maxWidth = Math.max(maxWidth, line.getAdvance());
        }
        
        var metrics = font.getLineMetrics("Ag", FRC);
        var lineHeight = metrics.getHeight();
        var gap = (lines.isEmpty() || flavorLines.isEmpty()) ? 0f : lineHeight * FLAVOR_GAP_RATIO;
        return new TextLayoutData(font, lines, flavorLines, metrics.getAscent(), lineHeight, gap, maxWidth);
    }

    private void addParagraphs(String content, Font font, List<TextLayout> target, boolean reminderStyle) {
        for (var paragraph : content.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1)) {
            if (paragraph.isEmpty()) {
                target.add(new TextLayout(" ", font, FRC));
                continue;
            }
            addWrappedLines(paragraph, font, target, reminderStyle);
        }
    }

    /** Italic font used for flavor text, same size as the rules text. */
    private Font getFlavorFont(Font rulesFont) {
        var base = TextRole.REMINDER.getFont();
        if (!base.isItalic()) {
            base = base.deriveFont(Font.ITALIC);
        }
        return base.deriveFont(rulesFont.getSize2D());
    }

    private void addWrappedLines(String paragraph, Font font, List<TextLayout> lines, boolean reminderStyle) {
        var attributedText = createAttributedText(paragraph, font, reminderStyle);
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
    private AttributedString createAttributedText(String paragraph, Font font, boolean reminderStyle) {
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
        attributedText.addAttribute(TextAttribute.FONT, font);
        if (reminderStyle) {
            applyReminderStyle(renderedText.toString(), font, attributedText);
        }
        var symbolBaseline = font.getLineMetrics("Ag", FRC).getAscent();
        for (var index = 0; index < symbolPositions.size(); index++) {
            var position = symbolPositions.get(index);
            attributedText.addAttribute(TextAttribute.CHAR_REPLACEMENT,
                    new ImageGraphicAttribute(symbolImages.get(index), ImageGraphicAttribute.ROMAN_BASELINE, 0,
                            symbolBaseline),
                    position, position + 1);
        }
        return attributedText;
    }

    /**
     * Renders reminder text enclosed in parentheses with the card's italic reminder font.
     */
    private void applyReminderStyle(String renderedText, Font font, AttributedString attributedText) {
        if (role != TextRole.TEXT) {
            return;
        }

        var reminderFont = TextRole.REMINDER.getFont().deriveFont(font.getSize2D());
        var reminderMatcher = REMINDER_PATTERN.matcher(renderedText);
        while (reminderMatcher.find()) {
            attributedText.addAttribute(TextAttribute.FONT, reminderFont, reminderMatcher.start(), reminderMatcher.end());
        }
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

    private record TextLayoutData(Font font, List<TextLayout> lines, List<TextLayout> flavorLines,float ascent, float lineHeight, float gap, float maxWidth) {
	 float flavorHeight() {
	        return flavorLines.size() * lineHeight;
	    }

	    float height() {
	        return (lines.size() + flavorLines.size()) * lineHeight + gap;
	    }
    }

}
