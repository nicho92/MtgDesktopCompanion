package org.magic.tests;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

public class TestTree {

    public static class MyTreeNode extends DefaultMutableTreeNode {

        private boolean loaded = false;

        private int depth;

        private final int index;

        public MyTreeNode(int index, int depth) {
            this.index = index;
            this.depth = depth;
            add(new DefaultMutableTreeNode("Loading...", false));
            setAllowsChildren(true);
            setUserObject("Child " + index + " at level " + depth);
        }

        private void setChildren(List<MyTreeNode> children) {
            removeAllChildren();
            setAllowsChildren(children.size() > 0);
            for (MutableTreeNode node : children) {
                add(node);
            }
            loaded = true;
        }

        @Override
        public boolean isLeaf() {
            return false;
        }

        public void loadChildren(final DefaultTreeModel model, final PropertyChangeListener progressListener) {
            if (loaded) {
                return;
            }
            SwingWorker<List<MyTreeNode>, Void> worker = new SwingWorker<List<MyTreeNode>, Void>() {
                @Override
                protected List<MyTreeNode> doInBackground() throws Exception {
                    // Here access database if needed
                    setProgress(0);
                    List<MyTreeNode> children = new ArrayList<TestTree.MyTreeNode>();
                    if (depth < 5) {
                        for (int i = 0; i < 5; i++) {
                            // Simulate DB access time
                            Thread.sleep(300);
                            children.add(new MyTreeNode(i + 1, depth + 1));
                            setProgress((i + 1) * 20);
                        }
                    } else {
                        Thread.sleep(1000);
                    }
                    setProgress(0);
                    return children;
                }

                @Override
                protected void done() {
                    try {
                        setChildren(get());
                        model.nodeStructureChanged(MyTreeNode.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Notify user of error.
                    }
                    super.done();
                }
            };
            if (progressListener != null) {
                worker.getPropertyChangeSupport().addPropertyChangeListener("progress", progressListener);
            }
            worker.execute();
        }

    }

    protected void initUI() {
        JFrame frame = new JFrame(TestTree.class.getSimpleName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MyTreeNode root = new MyTreeNode(1, 0);
        final DefaultTreeModel model = new DefaultTreeModel(root);
        final JProgressBar bar = new JProgressBar();
        final PropertyChangeListener progressListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                bar.setValue((Integer) evt.getNewValue());
            }
        };
        JTree tree = new JTree() {
            @Override
            @Transient
            public Dimension getPreferredSize() {
                Dimension preferredSize = super.getPreferredSize();
                preferredSize.width = Math.max(400, preferredSize.width);
                preferredSize.height = Math.max(400, preferredSize.height);
                return preferredSize;
            }
        };
        tree.setShowsRootHandles(true);
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {

            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                TreePath path = event.getPath();
                if (path.getLastPathComponent() instanceof MyTreeNode) {
                    MyTreeNode node = (MyTreeNode) path.getLastPathComponent();
                    node.loadChildren(model, progressListener);
                }
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

            }
        });
        tree.setModel(model);
        root.loadChildren(model, progressListener);
        frame.add(new JScrollPane(tree));
        frame.add(bar, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new TestTree().initUI();
            }
        });
    }
}