package org.magic.tools;

import static org.magic.tools.MTG.capitalize;
import static org.magic.tools.MTG.getEnabledPlugin;
import static org.magic.tools.MTG.listEnabledPlugins;
import static org.magic.tools.MTG.listPlugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.JXSearchField.SearchMode;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumStock;
import org.magic.api.criterias.QueryAttribute;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.renderer.MagicCollectionIconListRenderer;
import org.magic.gui.renderer.MagicEditionIconListRenderer;
import org.magic.gui.renderer.MagicEditionIconListRenderer.SIZE;
import org.magic.gui.renderer.MagicEditionJLabelRenderer;
import org.magic.gui.renderer.PluginIconListRenderer;
import org.magic.gui.renderer.standard.BooleanCellEditorRenderer;
import org.magic.gui.renderer.standard.ComboBoxEditor;
import org.magic.gui.renderer.standard.DateTableCellEditorRenderer;
import org.magic.gui.renderer.standard.DoubleCellEditorRenderer;
import org.magic.gui.renderer.standard.IntegerCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.threads.ThreadManager;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.FilterSettings;
import net.coderazzi.filters.gui.TableFilterHeader;
@SuppressWarnings("unchecked")
public class UITools {

	private static final String DATE_FORMAT = "DATE_FORMAT";

	private UITools() {}
	
	protected static Logger logger = MTGLogger.getLogger(UITools.class);
	
	public static final int getComponentIndex(Component component) {
	    if (component != null && component.getParent() != null) {
	      Container c = component.getParent();
	      for (int i = 0; i < c.getComponentCount(); i++) {
	        if (c.getComponent(i) == component)
	          return i;
	      }
	    }

	    return -1;
	  }
	
	public static void setDefaultRenderer(JTable table, TableCellRenderer render) {

		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(render);
		}
	}
	
	public static void browse(String uri)
	{
		try {
			Desktop.getDesktop().browse(new URI(uri));
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	
	public static JXTable createNewTable(TableModel mod)
	{
		JXTable table = new JXTable();
				if(mod!=null)
					table.setModel(mod);
		
				table.setDefaultRenderer(Boolean.class, new BooleanCellEditorRenderer());
				table.setDefaultRenderer(Double.class, new DoubleCellEditorRenderer());
				table.setDefaultRenderer(Integer.class, new IntegerCellEditorRenderer());
				table.setDefaultRenderer(boolean.class, new BooleanCellEditorRenderer());
				table.setDefaultRenderer(double.class, new DoubleCellEditorRenderer());
				table.setDefaultRenderer(int.class, new IntegerCellEditorRenderer());
				
				table.setDefaultRenderer(Date.class, new DateTableCellEditorRenderer());
				table.setDefaultRenderer(MagicEdition.class, new MagicEditionJLabelRenderer());
	
				table.setDefaultEditor(Double.class, new DoubleCellEditorRenderer());
				table.setDefaultEditor(Integer.class, new IntegerCellEditorRenderer());
				table.setDefaultEditor(Boolean.class, new BooleanCellEditorRenderer());
				table.setDefaultEditor(boolean.class, new BooleanCellEditorRenderer());
				table.setDefaultEditor(int.class, new IntegerCellEditorRenderer());
				table.setDefaultEditor(double.class, new DoubleCellEditorRenderer());
				table.setDefaultEditor(Date.class, new DateTableCellEditorRenderer());
				table.setDefaultEditor(EnumCondition.class, new ComboBoxEditor<>(EnumCondition.values()));
				table.setDefaultEditor(EnumStock.class, new ComboBoxEditor<>(EnumStock.values()));
				
				try {
					table.setDefaultEditor(MagicCollection.class, new ComboBoxEditor<>(getEnabledPlugin(MTGDao.class).listCollections()));
				} catch (SQLException e1) {
					logger.error(e1);
				}
				
				table.setColumnControlVisible(true);
				table.setRowHeight(MTGConstants.TABLE_ROW_HEIGHT);
				
		return table;
	}
	
	
	public static String[] stringLineSplit(String s,boolean removeBlank)
	{
		if(removeBlank)
			return s.split("["+System.lineSeparator()+"]+");
		
		return s.lines().toArray(String[]::new);
	}
	
	public static GridBagConstraints createGridBagConstraints(Integer anchor,Integer fill,int col,int line)
	{
		GridBagConstraints cons = new GridBagConstraints();
		
		if(anchor!=null)
			cons.anchor = anchor;
		
		if(fill!=null)
			cons.fill = fill;
		
		cons.insets = new Insets(0, 0, 5, 5);
		cons.gridx = col;
		cons.gridy = line;
		
		return cons;
	}
	
	public static GridBagConstraints createGridBagConstraints(Integer anchor,Integer fill,int col,int line,Integer gridW, Integer gridH)
	{
		GridBagConstraints cons = createGridBagConstraints(anchor,fill,col,line);
		
		if(gridW!=null)
			cons.gridwidth = gridW;
		
		if(gridH!=null)
			cons.gridheight = gridH;
		
		return cons;
	}
	
	public static JTextField createSearchField()
	{
		JTextField txtSearch;
		if(SystemUtils.IS_OS_MAC_OSX)
		{
		  txtSearch= new JTextField(capitalize("SEARCH_MODULE"));
		}
		else
		{	
		  txtSearch = new JXSearchField(capitalize("SEARCH_MODULE"));
		  ((JXSearchField)txtSearch).setSearchMode(SearchMode.REGULAR);
		  ((JXSearchField)txtSearch).setRecentSearchesSaveKey("K");
		  ((JXSearchField)txtSearch).setBackground(Color.WHITE);
		}
		
		
		if(MTGControler.getInstance().get("autocompletion").equals("true")) {
			autocomplete(txtSearch);
		}
		
		
		return txtSearch;
	}
	
	
	public static void autocomplete(JTextField txtSearch) {

		  final List<String> res = new ArrayList<>();
		  txtSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				res.clear();
				if(!txtSearch.getText().isEmpty())
					res.addAll(getEnabledPlugin(MTGCardsIndexer.class).suggestCardName(txtSearch.getText()));
				
			}
		});
		AutoCompleteDecorator.decorate(txtSearch,res,false);
		
	}

	public static <T extends MTGPlugin> JComboBox<T> createCombobox(Class<T> classe,boolean all)
	{
		DefaultComboBoxModel<T> model = new DefaultComboBoxModel<>();
		JComboBox<T> combo = new JComboBox<>(model);
		if(all)
			listPlugins(classe).stream().forEach(model::addElement);
		else
			listEnabledPlugins(classe).stream().forEach(model::addElement);
		combo.setRenderer(new PluginIconListRenderer());
		return combo;
	}
	
	public static JButton createBindableJButton(String text, Icon ic, int key, String name)
	{
		JButton b = new JButton(text, ic);
				b.setName(name);
		ShortKeyManager.inst().setShortCutTo(key, b);
		return b;
	}
	
	public static void bindJButton(JButton b, int key, String name)
	{
		b.setName(name);
		ShortKeyManager.inst().setShortCutTo(key, b);
	}

	public static JComboBox<MagicEdition> createComboboxEditions(List<MagicEdition> value,SIZE s) {
		DefaultComboBoxModel<MagicEdition> model = new DefaultComboBoxModel<>();
		JComboBox<MagicEdition> combo = new JComboBox<>(model);
		value.forEach(model::addElement);
		combo.setRenderer(new MagicEditionIconListRenderer(s));
		return combo;
	}
	
	public static JComboBox<MagicEdition> createComboboxEditions()
	{
		try {
			List<MagicEdition> list = getEnabledPlugin(MTGCardsProvider.class).listEditions();
			Collections.sort(list);
			return createComboboxEditions(list,SIZE.MEDIUM);
		} catch (IOException e) {
			logger.error(e);
			return new JComboBox<>();
		}
	}
	
	public static <T> JComboBox<T> createCombobox(T[] items)
	{
		return createCombobox(Arrays.asList(items));
	}
	
	public static <T> JComboBox<T> createCombobox(List<T> items)
	{
		return createCombobox(items, MTGConstants.ICON_MANA_INCOLOR);
	}
	
	public static <T> JComboBox<T> createCombobox(List<T> items,ImageIcon i)
	{
		DefaultComboBoxModel<T> model = new DefaultComboBoxModel<>();
		JComboBox<T> combo = new JComboBox<>(model);
		
		items.stream().forEach(model::addElement);
			
			combo.setRenderer((list,value, index,isSelected,cellHasFocus)->{
					JLabel l ;
					if(value==null)
					{
						l= new JLabel();
					}
					else
					{
						l=new JLabel(value.toString());
						if(value instanceof LookAndFeelInfo)
						{
							l=new JLabel(((LookAndFeelInfo)value).getName());
							l.setIcon(i);
						}
						else if (value instanceof QueryAttribute)
						{
							l.setIcon(MTGConstants.getIconFor((QueryAttribute)value));
						}
						else
						{
							l.setIcon(i);	
						}
						
					}
					
					l.setOpaque(true);
					if (isSelected) {
						l.setBackground(list.getSelectionBackground());
						l.setForeground(list.getSelectionForeground());
					} else {
						l.setBackground(list.getBackground());
						l.setForeground(list.getForeground());
					}
					
					return l;
			});
			
			
		return combo;
	}
	
	
	
	

	public static JComboBox<MagicCollection> createComboboxCollection()
	{
		DefaultComboBoxModel<MagicCollection> model = new DefaultComboBoxModel<>();
		JComboBox<MagicCollection> combo = new JComboBox<>(model);
	
		try {
			getEnabledPlugin(MTGDao.class).listCollections().stream().forEach(model::addElement);
			combo.setRenderer(new MagicCollectionIconListRenderer());
		return combo;
		} catch (Exception e) {
			logger.error(e);
			return combo;
		}

	}
	
	public static Double parseDouble(String text) {
		try {
			text=text.replace(",", ".").trim();
			return Double.parseDouble(text);
		} catch (Exception e) {
			logger.error("error parsing '" + text +"' :" + e);
			return 0.0;
		}
	}

	public static double roundDouble(double d)
	{
		return BigDecimal.valueOf(d).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
	}
	
	public static String formatDouble(Object f)
	{
		return new DecimalFormat("#0.0#").format(f);
	}
	
	public static void initTableFilter(JTable table)
	{
			try {
				FilterSettings.ignoreCase=true;
				TableFilterHeader filterHeader = new TableFilterHeader(table, AutoChoices.ENABLED);
				filterHeader.setSelectionBackground(Color.LIGHT_GRAY);
			}
			catch(Exception e)
			{
				logger.error("error setting TableFilter of " + table,e);
			}
	}
	
	public static <V> void initCardToolTipTable(final JTable table, final int cardPos, Callable<V> dblClick) {
		final JPopupMenu popUp = new JPopupMenu();
		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) 
			{
					e.consume();
					if(e.getClickCount()==2 && dblClick!=null)
					{
					
						ThreadManager.getInstance().submitCallable(dblClick,"initTooltip");
					}
					else 
					{
						
						int row = table.rowAtPoint(e.getPoint());
						
						
						if(row<0)
							return;
						
						MagicCardDetailPanel pane = new MagicCardDetailPanel();
						pane.enableThumbnail(true);
						table.setRowSelectionInterval(row, row);
						
						try {
								MagicCard mc = UITools.getTableSelection(table,cardPos);
								pane.setMagicCard(mc);
								
								popUp.setBorder(new LineBorder(Color.black));
								popUp.setVisible(false);
								popUp.removeAll();
								popUp.setLayout(new BorderLayout());
								popUp.add(pane, BorderLayout.CENTER);
								popUp.show(table, e.getX()+5, e.getY()+5);
								popUp.setVisible(true);
		
							} catch (IndexOutOfBoundsException ex) {
								logger.error(ex);
							} 
				}
			}
		});
	}
	
	
	public static <V> void initCardToolTipTable(final JTable table, final Integer cardPos, final Integer edPos, Callable<V> dblClick) {
		final JPopupMenu popUp = new JPopupMenu();
		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) 
			{
					e.consume();
					if(e.getClickCount()==2 && dblClick!=null)
					{
					
						ThreadManager.getInstance().submitCallable(dblClick,"initTooltip");
					}
					else 
					{
						
						int row = table.rowAtPoint(e.getPoint());
						MagicCardDetailPanel pane = new MagicCardDetailPanel();
						pane.enableThumbnail(true);
						table.setRowSelectionInterval(row, row);
						String cardName = table.getValueAt(row, cardPos.intValue()).toString();
	
						if (cardName.indexOf('(') >= 0)
							cardName = cardName.substring(0, cardName.indexOf('(')).trim();
	
						MagicEdition ed = null;
						try {
							if (edPos != null) {
								String edID = table.getValueAt(row, edPos).toString();
								ed = new MagicEdition();
								ed.setId(edID);
							}
						}catch(Exception ex)
						{
							logger.error("no edition defined");
						}
						
						
						
						try {
								MagicCard mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cardName, ed, false).get(0);
								pane.setMagicCard(mc);
								
								popUp.setBorder(new LineBorder(Color.black));
								popUp.setVisible(false);
								popUp.removeAll();
								popUp.setLayout(new BorderLayout());
								popUp.add(pane, BorderLayout.CENTER);
								popUp.show(table, e.getX()+5, e.getY()+5);
								popUp.setVisible(true);
		
							} catch (IndexOutOfBoundsException ex) {
								logger.error(cardName + "is not found");
							} catch (IOException e1) {
								logger.error("error loading " + cardName,e1);
							}
						
				}
			}
		});
	}
	
	public static <T> List<T> getTableSelections(JTable tableCards,int columnID) {
		int[] viewRow = tableCards.getSelectedRows();
		List<T> listCards = new ArrayList<>();
		for (int i : viewRow) {
			int modelRow = tableCards.convertRowIndexToModel(i);
			T mc = (T) tableCards.getModel().getValueAt(modelRow, columnID);
			listCards.add(mc);
		}
		return listCards;
	}

	public static <T> T getTableSelection(JTable tableCards,int columnID) {
		try{ 
			return (T) getTableSelections(tableCards, columnID).get(0);
		}
		catch(IndexOutOfBoundsException e)
		{
			return null;
		}
	}

	
	public static <T> List<T> getTablesItems(JTable tableCards, int columnID)
	{
		List<T> listCards = new ArrayList<>();
		for (int count = 0; count < tableCards.getModel().getRowCount(); count++){
			listCards.add((T) tableCards.getValueAt(count, columnID));
		}
		
		return listCards;
	}

	public static void applyDefaultSelection(Component pane) {
			pane.setForeground(SystemColor.textHighlightText);
			pane.setBackground(SystemColor.inactiveCaption);
	}

	public static Date parseDate(String indexDate) {
		
		return parseDate(indexDate, MTGControler.getInstance().getLangService().get(DATE_FORMAT));
	}
	
	public static Date parseDate(String indexDate,String format) {
		if(indexDate==null)
			return new Date();
		
		try {
			return new SimpleDateFormat(format).parse(indexDate);
		} catch (ParseException e) {
			logger.error(e);
			return new Date();
		}
	}

	public static String formatDate(Date indexDate) {
		
		if(indexDate==null)
			return "";
		
		return new SimpleDateFormat(MTGControler.getInstance().getLangService().get(DATE_FORMAT)).format(indexDate);
	}
	
	public static String formatDateTime(Date indexDate) {
		if(indexDate==null)
			return "";
		
		return new SimpleDateFormat(MTGControler.getInstance().getLangService().get(DATE_FORMAT) +" HH:mm:ss").format(indexDate);
	}


	public static void addTab(JTabbedPane pane, MTGUIComponent comp) {
		pane.addTab(capitalize(comp.getTitle()), comp.getIcon(),comp);
		
	}


}
