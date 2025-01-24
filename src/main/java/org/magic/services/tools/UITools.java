package org.magic.services.tools;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;
import static org.magic.services.tools.MTG.listEnabledPlugins;
import static org.magic.services.tools.MTG.listPlugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.JXSearchField.SearchMode;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.table.TableColumnExt;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumCardVariation;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.EnumPaymentProvider;
import org.magic.api.beans.enums.EnumTransactionDirection;
import org.magic.api.beans.enums.EnumTransactionStatus;
import org.magic.api.beans.game.Player;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.technical.MoneyValue;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.api.interfaces.extra.MTGIconable;
import org.magic.api.sorters.NumberSorter;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.card.MagicCardMainDetailPanel;
import org.magic.gui.renderer.ContactRenderer;
import org.magic.gui.renderer.MTGPluginCellRenderer;
import org.magic.gui.renderer.MagicEditionIconListRenderer;
import org.magic.gui.renderer.MagicEditionIconListRenderer.SIZE;
import org.magic.gui.renderer.MagicEditionJLabelRenderer;
import org.magic.gui.renderer.MoneyCellRenderer;
import org.magic.gui.renderer.PlayerRenderer;
import org.magic.gui.renderer.PluginIconListRenderer;
import org.magic.gui.renderer.standard.BooleanCellEditorRenderer;
import org.magic.gui.renderer.standard.ComboBoxEditor;
import org.magic.gui.renderer.standard.DateTableCellEditorRenderer;
import org.magic.gui.renderer.standard.DoubleCellEditorRenderer;
import org.magic.gui.renderer.standard.NumberCellEditorRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ShortKeyManager;
import org.magic.services.adapters.TableColumnModelExtListenerAdapter;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.panda_lang.pandomium.Pandomium;

import com.github.sarxos.webcam.Webcam;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.FilterSettings;
import net.coderazzi.filters.gui.TableFilterHeader;



@SuppressWarnings("unchecked")
public class UITools {

	private static final String DATE_FORMAT = "DATE_FORMAT";
	private static Pandomium instance;
	protected static Logger logger = MTGLogger.getLogger(UITools.class);



	private UITools() {}


	public static final int getComponentIndex(Component component) {
	    if (component != null && component.getParent() != null) {
	      var c = component.getParent();
	      for (var i = 0; i < c.getComponentCount(); i++) {
	        if (c.getComponent(i) == component)
	          return i;
	      }
	    }

	    return -1;
	  }
	
	public static String humanReadableSize(long bytes) {
	    var absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
	    if (absB < 1024) {
	        return bytes + " B";
	    }
	    var value = absB;
	    var ci = new StringCharacterIterator("KMGTPE");
	    for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
	        value >>= 10;
	        ci.next();
	    }
	    value *= Long.signum(bytes);
	    return String.format("%.1f %ciB", value / 1024.0, ci.current());
	}

	public static void setDefaultRenderer(JTable table, TableCellRenderer render) {

		for (var i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(render);
		}
	}


	public static Pandomium getPandomiumInstance()
	{
		if(instance==null)
		{
			instance = Pandomium.builder()
								.nativeDirectory(MTGConstants.NATIVE_DIR.getAbsolutePath())
								.build();

			logger.debug("loading pandomium");

		}

		return instance;
	}


	public static void buildCategorizedMenu(JPopupMenu menu, JMenuItem it, MTGCardsExport exp)
	{
		var foundCateg=false;
		for(var m : menu.getSubElements())
		{
			if(m instanceof JMenuItem subMenuCategory && subMenuCategory.getText().equals(exp.getCategory().name()))
				{
						foundCateg=true;
						subMenuCategory.add(it);
						break;
				}
		}
		if(!foundCateg)
		{
			var itCateg = new JMenu(exp.getCategory().name());
			itCateg.setIcon(exp.getCategory().getIcon());
			itCateg.add(it);
			menu.add(itCateg);
		}
	}



	public static void browse(String uri)
	{
		try {
			logger.debug("Opening browser to {}", uri);
			Desktop.getDesktop().browse(new URI(uri));
		} catch (Exception e) {
			logger.error(e);
		}
	}


	public static JXTable createNewTable(TableModel mod, boolean enableFilter)
	{
		var table = new JXTable();
				if(mod!=null)
					table.setModel(mod);
		

				table.setDefaultRenderer(Boolean.class, new BooleanCellEditorRenderer());
				table.setDefaultRenderer(Double.class, new DoubleCellEditorRenderer());
				table.setDefaultRenderer(Integer.class, new NumberCellEditorRenderer());
				table.setDefaultRenderer(Long.class, new NumberCellEditorRenderer());
				table.setDefaultRenderer(boolean.class, new BooleanCellEditorRenderer());
				table.setDefaultRenderer(double.class, new DoubleCellEditorRenderer());
				table.setDefaultRenderer(int.class, new NumberCellEditorRenderer());

				table.setDefaultRenderer(Date.class, new DateTableCellEditorRenderer());
				table.setDefaultRenderer(Instant.class, new DateTableCellEditorRenderer());
				table.setDefaultRenderer(MTGEdition.class, new MagicEditionJLabelRenderer());
				table.setDefaultRenderer(MTGPlugin.class, new MTGPluginCellRenderer());
				table.setDefaultRenderer(Player.class, new PlayerRenderer());
				table.setDefaultRenderer(Contact.class, new ContactRenderer());
				table.setDefaultRenderer(MoneyValue.class, new MoneyCellRenderer());
				
				table.setDefaultEditor(Double.class, new DoubleCellEditorRenderer());
				table.setDefaultEditor(Integer.class, new NumberCellEditorRenderer());
				table.setDefaultEditor(Long.class, new NumberCellEditorRenderer());
				table.setDefaultEditor(Boolean.class, new BooleanCellEditorRenderer());
				table.setDefaultEditor(boolean.class, new BooleanCellEditorRenderer());
				table.setDefaultEditor(int.class, new NumberCellEditorRenderer());
				table.setDefaultEditor(double.class, new DoubleCellEditorRenderer());
				table.setDefaultEditor(Date.class, new DateTableCellEditorRenderer());
				table.setDefaultEditor(EnumItems.class, new ComboBoxEditor<>(EnumItems.values()));
				table.setDefaultEditor(EnumCondition.class, new ComboBoxEditor<>(EnumCondition.values()));
				table.setDefaultEditor(EnumTransactionStatus.class, new ComboBoxEditor<>(EnumTransactionStatus.values()));
				table.setDefaultEditor(EnumPaymentProvider.class, new ComboBoxEditor<>(EnumPaymentProvider.values()));
				table.setDefaultEditor(EnumTransactionDirection.class, new ComboBoxEditor<>(EnumTransactionDirection.values()));
				table.setDefaultEditor(EnumExtra.class, new ComboBoxEditor<>(EnumExtra.values()));
				table.setDefaultEditor(Level.class,  new ComboBoxEditor<>(MTGLogger.getLevels()) );
				try {
					table.setDefaultEditor(MTGEdition.class, new ComboBoxEditor<>(getEnabledPlugin(MTGCardsProvider.class).listEditions().stream().sorted().toList() ));
				} catch (IOException e2) {
					logger.error(e2);
				}
				
				try {
					table.setDefaultEditor(MTGCollection.class, new ComboBoxEditor<>(getEnabledPlugin(MTGDao.class).listCollections()));
				} catch (Exception e1) {
					logger.error(e1);
				}

				table.setColumnControlVisible(true);
				table.putClientProperty("terminateEditOnFocusLost", true);
				table.setRowHeight(MTGConstants.TABLE_ROW_HEIGHT);
				table.setPreferredScrollableViewportSize(new java.awt.Dimension(800,600));
				
				
				
				try {
					table.packAll();
				}
				catch(Exception e)
				{
					//do nothing
				}

				if(enableFilter)
					initTableFilter(table);
				
				//TODO dev for columns state records
				table.getColumnModel().addColumnModelListener(new TableColumnModelExtListenerAdapter() {
					@Override
					public void columnPropertyChange(PropertyChangeEvent event) {
						if(event.getPropertyName().equals("visible") && MTGControler.getInstance().isLoaded()){
							var tce = (TableColumnExt)event.getSource();
							logger.trace("{} {} {} {}",table.getModel().getClass(),tce.getModelIndex(),tce.getIdentifier(),Boolean.valueOf(event.getNewValue().toString()));
						}
					}
				});
				
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
		var cons = new GridBagConstraints();

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
		var cons = createGridBagConstraints(anchor,fill,col,line);

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
		  txtSearch.setBackground(Color.WHITE);
		}


		if(MTG.readPropertyAsBoolean("autocompletion")) {
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
				if(!txtSearch.getText().isEmpty() && txtSearch.getText().length()>2)
					res.addAll(getEnabledPlugin(MTGCardsIndexer.class).suggestCardName(txtSearch.getText()));

			}
		});
		AutoCompleteDecorator.decorate(txtSearch,res,false);

	}

	public static <T extends MTGPlugin> JComboBox<T> createComboboxPlugins(Class<T> classe,boolean all)
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
		var b = new JButton(text, ic);
			 b.setName(name);
		ShortKeyManager.inst().setShortCutTo(key, b);
		return b;
	}

	public static void bindJButton(JButton b, int key, String name)
	{
		b.setName(name);
		ShortKeyManager.inst().setShortCutTo(key, b);
	}

	public static JComboBox<MTGEdition> createComboboxEditions(List<MTGEdition> value,SIZE s) {
		var  combo = UITools.createCombobox(value);
		combo.setRenderer(new MagicEditionIconListRenderer(s));
		return combo;
	}

	public static JComboBox<MTGEdition> createComboboxEditions()
	{
		try {
			List<MTGEdition> list = getEnabledPlugin(MTGCardsProvider.class).listEditions();
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
		var model = new DefaultComboBoxModel<T>();
		var combo = new JComboBox<T>(model);

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
						if(value instanceof LookAndFeelInfo lafi)
						{
							l=new JLabel(lafi.getName());
							l.setIcon(MTGConstants.ICON_MANA_INCOLOR);
						}
						else if (value instanceof MTGIconable qa)
						{
							l.setIcon(qa.getIcon());
						}
						else if  (value instanceof Webcam)
						{
							l.setIcon(MTGConstants.ICON_WEBCAM);
						}
						else
						{
							l.setIcon(MTGConstants.ICON_MANA_INCOLOR);
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

			//AutoCompleteDecorator.decorate(combo)
			

		return combo;
	}





	public static JComboBox<MTGCollection> createComboboxCollection()
	{
		
		try {
			return createCombobox(getEnabledPlugin(MTGDao.class).listCollections());
		} catch (Exception e) {
			logger.error(e);
			return new JComboBox<>();
		}
		
		

	}

	public static Double parseDouble(String text) {
		try {


			if(text.isBlank())
				return 0.0;

			text=text.replace(",", ".").replaceAll("[€,]","").replaceAll("[$,]","").replaceAll("[£,]","").replaceAll("[%,]", "").replace('\u00A0',' ').replace('\uFFFD', ' ').replace("\u0080", "").trim();
			
			if(StringUtils.countMatches(text, '.')>1)
				text=text.replaceFirst("\\.", "");

			return Double.parseDouble(text);
		} catch (Exception e) {
			logger.error("error parsing '{}':{}",text,e);
			return 0.0;
		}
	}
	
	public static double roundDouble(double d)
	{
		return BigDecimal.valueOf(d).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
	}

	public static String formatDouble(Object f)
	{
		return formatDouble(f, "#0.0#",null);
	}

	public static String formatDouble(Object f,Character separator)
	{
		return formatDouble(f, "#0.0#",separator);
	}
	
	public static String formatDouble(Object f,String format,Character separator)
	{
		if(f==null)
			return "";

		var otherSymbols = new DecimalFormatSymbols(MTGControler.getInstance().getLocale());
		
		if(separator!=null)
			otherSymbols.setDecimalSeparator(separator);
		
		return new DecimalFormat(format,otherSymbols).format(f);
	}

	public static void initTableFilter(JXTable table)
	{
			try {
				FilterSettings.ignoreCase=true;
				var filterHeader = new TableFilterHeader(table, AutoChoices.ENABLED);
				filterHeader.setSelectionBackground(Color.LIGHT_GRAY);
			}
			catch(Exception e)
			{
				logger.error("error setting TableFilter of {}",table,e);
			}
		
		
	}


	public static <T> void initTableVisibility(JXTable table, GenericTableModel<T> model) {
		for(int i : model.defaultHiddenColumns())
			table.getColumnExt(model.getColumnName(i)).setVisible(false);
		
	}

	public static <V> void initCardToolTipTable(final JTable table, final Integer cardNamePos, final Integer edPos, final Integer extraPos, Callable<V> dblClick) {
		final var popUp = new JPopupMenu();
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e)
			{
					e.consume();
					if(e.getClickCount()==2 && dblClick!=null)
					{

						ThreadManager.getInstance().submitCallable(dblClick);
					}
					else
					{
						
						var row = table.rowAtPoint(e.getPoint());
						
						if(row<0)
							return;
						
						var pane = new MagicCardMainDetailPanel();
						pane.enableThumbnail(true);
						table.setRowSelectionInterval(row, row);

						var cardName = getModelValueAt(table,row, cardNamePos.intValue()).toString();

						if (cardName.indexOf('(') >= 0)
							cardName = cardName.substring(0, cardName.indexOf('(')).trim();

						MTGEdition ed = null;
						try {
							if (edPos != null) {
								
								if(getModelValueAt(table,row, edPos) instanceof MTGEdition se)
									ed=se;
								else
									ed = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(getModelValueAt(table,row, edPos).toString());
							}
						}catch(Exception ex)
						{
							logger.error("no edition defined");
						}

						MTGCard mc =null;
						EnumCardVariation extraVariations=null;

						try {
							
							if (extraPos != null)
							{
								var key = getModelValueAt(table,row, extraPos);
								if(key!=null) {
									extraVariations = EnumCardVariation.valueOf(key.toString());
									mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cardName, ed, true,extraVariations).get(0);
								}
								else
								{
									mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cardName, ed, true,null).get(0);
								}
							}
							else
							{
								mc = getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cardName, ed, true).get(0);
							}
								pane.init(mc);

								popUp.setBorder(new LineBorder(Color.black));
								popUp.setVisible(false);
								popUp.removeAll();
								popUp.setLayout(new BorderLayout());
								popUp.add(pane, BorderLayout.CENTER);
								popUp.show(table, e.getX()+5, e.getY()+5);
								popUp.setVisible(true);

							} catch (IndexOutOfBoundsException ex) {
								logger.error("{} is not found with extra={}",cardName,extraVariations);
							} catch (IOException e1) {
								logger.error("error loading {}",cardName,e1);
							}

				}
			}
		});
	}

	public static <T> List<T> getTableSelections(JTable tableCards,int columnID) {
		int[] viewRow = tableCards.getSelectedRows();
		List<T> listCards = new ArrayList<>();
		for (int i : viewRow) {
			listCards.add(getModelValueAt(tableCards,i,columnID));
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

	public static <T> T getModelValueAt(JTable tableCards, int row, int column)
	{
		return (T) tableCards.getModel().getValueAt(tableCards.convertRowIndexToModel(row), column);
	}


	public static void applyDefaultSelection(Component pane) {
			pane.setForeground(SystemColor.textHighlightText);
			pane.setBackground(SystemColor.inactiveCaption);
	}

	public static Date parseGMTDate(String gmtDate) {
		gmtDate=gmtDate.replace("\"", "");
		return DatatypeConverter.parseDateTime(gmtDate).getTime();
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

	public static Date parseDate(String indexDate,String format, Locale local) {
		if(indexDate==null)
			return new Date();

		try {
			return new SimpleDateFormat(format,local).parse(indexDate);
		} catch (ParseException e) {
			logger.error(e);
			return new Date();
		}
	}
	
	
	public static String formatDate(Date indexDate) {

		return formatDate(indexDate,MTGControler.getInstance().getLangService().get(DATE_FORMAT));
	}

	public static String formatDate(Date indexDate, String format) {

		if(indexDate==null)
			return "";

		return new SimpleDateFormat(format).format(indexDate);
	}


	public static String formatDate(Instant date) {
		return new SimpleDateFormat(MTGControler.getInstance().getLangService().get(DATE_FORMAT)+" HH:mm:ss.S").format(Date.from(date));
	}


	public static String formatDateTime(Date indexDate) {
		if(indexDate==null)
			return "";

		return new SimpleDateFormat(MTGControler.getInstance().getLangService().get(DATE_FORMAT) +" HH:mm:ss").format(indexDate);
	}


	public static void addTab(JTabbedPane pane, MTGUIComponent comp) {
		pane.addTab(capitalize(comp.getTitle()), ImageTools.resize(comp.getIcon(), 15, 15),comp);

	}
	
	public static int daysBetween(Instant d1, Instant d2)
	{
		return (int) ChronoUnit.DAYS.between(d1, d2);
	}

	public static List<Integer> getSelectedRows(JXTable table) {
		int[] viewRow = table.getSelectedRows();

		var ret = new ArrayList<Integer>();
		for(int i : viewRow)
			ret.add(table.convertRowIndexToModel(i));

		return ret;
		}
	


	public static void setSorter(JTable table, int i, NumberSorter numberSorter) {
			var sorter = new TableRowSorter<TableModel>(table.getModel());
			sorter.setComparator(i, numberSorter);
			table.setRowSorter(sorter);
	}
	
	public static void sort(JTable table, int index, SortOrder order) {
		var sorter = new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);
		var sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(index, order));
		sorter.setSortKeys(sortKeys);
	}

	public static String replaceSpecialCharacters(String str,String with) {
		return str.replaceAll("[^a-zA-Z0-9]", with);
	}


	public static JPanel createFlowPanel(JComponent... of) {
		var tempPanel = new JPanel();
		((FlowLayout)tempPanel.getLayout()).setAlignment(FlowLayout.LEFT);
		for(var c : of)
			tempPanel.add(c);
		
		return tempPanel;
	}
	
	public static JPanel createFlowCenterPanel(JComponent... of) {
		var tempPanel = new JPanel();
		((FlowLayout)tempPanel.getLayout()).setAlignment(FlowLayout.CENTER);
		for(var c : of)
			tempPanel.add(c);
		
		return tempPanel;
		
		
	}
	



}
