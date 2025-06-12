package org.magic.gui.components.wallpaper;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

public class WrapLayout extends FlowLayout {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return layoutSize(target, false);
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int maxWidth = target.getWidth();

            if (maxWidth <= 0 && target.getParent() != null) {
                maxWidth = target.getParent().getWidth();
            }

            Insets insets = target.getInsets();
            int hgap = getHgap();
            int vgap = getVgap();
            int maxContainerWidth = maxWidth - (insets.left + insets.right + hgap * 2);

            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);

                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                    if (rowWidth + d.width > maxContainerWidth && rowWidth > 0) {
                        dim.width = Math.max(dim.width, rowWidth);
                        dim.height += rowHeight + vgap;
                        rowWidth = 0;
                        rowHeight = 0;
                    }

                    rowWidth += d.width + hgap;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }

            dim.width = Math.max(dim.width, rowWidth);
            dim.height += rowHeight;
            dim.width += insets.left + insets.right + hgap * 2;
            dim.height += insets.top + insets.bottom + vgap * 2;

            return dim;
        }
    }
}