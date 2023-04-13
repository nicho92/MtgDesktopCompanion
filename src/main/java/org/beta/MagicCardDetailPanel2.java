package org.beta;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.gui.components.MagicTextPane;
import org.magic.gui.components.ManaPanel;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.ImageTools;



public class MagicCardDetailPanel2 extends JPanel{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger logger = MTGLogger.getLogger(MagicCardDetailPanel2.class);
	private JTextField txtName;
	private JTextField txtTypes;
	private JTextField txtPower;
	private JTextPane txtFlavor;
	private JTextField txtRarity;
	private JCheckBox chkReserved;
	private MagicTextPane txtText;
	private ManaPanel manaCostPanel;
	private JPanel panelActions;
	private JButton btnAlert;
	private JButton btnCopy;
	private JButton btnStock;
	private JLabel lblThumbnail;
	private JLabel lblNumber;
	private boolean thumbnail = true;
	private JList<MTGFormat> lstFormats;
	
	public void enableThumbnail(boolean val) {
		thumbnail = val;
	}
	
	public void setEditable(boolean b) {
		
		txtName.setEditable(b);
		txtTypes.setEditable(b);
		txtPower.setEditable(b);
		txtFlavor.setEditable(b);
		txtRarity.setEditable(b);
		chkReserved.setEnabled(b);
		txtText.setEditable(b);
	}
	
	public void init(MagicCard mc)
	{
		
		if(mc==null)
			return;
		
		txtName.setText(mc.getName());
		txtTypes.setText(mc.getFullType());
		txtFlavor.setText(mc.getFlavor());
		txtRarity.setText(mc.getRarity().toPrettyString());
		txtText.setText(mc.getText());
		chkReserved.setSelected(mc.isReserved());
		
		
		if(mc.isCreature())
			txtPower.setText(mc.getPower()+"/"+mc.getToughness());
		else if (mc.isPlaneswalker())
			txtPower.setText(""+mc.getLoyalty());
		else if (mc.isSiege())
			txtPower.setText(""+mc.getDefense());
		else
			txtPower.setText("");
		
		manaCostPanel.setManaCost(mc.getCost());
		
		if (!mc.getEditions().isEmpty())
		{
			int showCount = mc.getCurrentSet().getCardCountOfficial();
			if(showCount==0)
				showCount=mc.getCurrentSet().getCardCount();

			lblNumber.setText(mc.getCurrentSet().getNumber() + "/"+ showCount);
		}
		
		txtText.updateTextWithIcons();
		
		
		((DefaultListModel<MTGFormat>) lstFormats.getModel()).removeAllElements();
		mc.getLegalities().forEach(((DefaultListModel<MTGFormat>) lstFormats.getModel())::addElement);
		
		if(thumbnail)
			loadPics(mc, null);
		
	}
	
	
	protected void loadPics(MagicCard mc,MagicCardNames fn) {

		SwingWorker<ImageIcon, Void> sw = new SwingWorker<>()
		{
			@Override
			protected ImageIcon doInBackground() throws Exception {
				ImageIcon icon;
				try {
					if(fn==null)
						icon = new ImageIcon(getEnabledPlugin(MTGPictureProvider.class).getPicture(mc));
					else
						icon = new ImageIcon(getEnabledPlugin(MTGPictureProvider.class).getForeignNamePicture(fn, mc));
				} catch (Exception e) {
					icon = new ImageIcon(getEnabledPlugin(MTGPictureProvider.class).getBackPicture(mc));
					logger.error("Error loading pics for {}", mc, e);
				}
				return icon;

			}

			@Override
			protected void done() {
				try {
					if(get().getIconHeight()>369)
						lblThumbnail.setIcon(ImageTools.resize(get(),266,369));
					else
						lblThumbnail.setIcon(get());
				}
				catch(InterruptedException ex)
				{
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					logger.error(e);
				}

			}
		};

		ThreadManager.getInstance().runInEdt(sw,"loading " + mc + " picture");
	}

	
	
	public MagicCardDetailPanel2() {
		
		
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 487, 0, 100, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 177, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		txtName = new JTextField();
		txtName.setToolTipText("Name");
		GridBagConstraints gbctxtName = new GridBagConstraints();
		gbctxtName.insets = new Insets(0, 0, 5, 5);
		gbctxtName.fill = GridBagConstraints.HORIZONTAL;
		gbctxtName.gridx = 1;
		gbctxtName.gridy = 0;
		add(txtName, gbctxtName);
		txtName.setColumns(10);
		
		manaCostPanel = new ManaPanel();
		manaCostPanel.setToolTipText("Mana");
		GridBagConstraints gbcmanaCostPanel = new GridBagConstraints();
		gbcmanaCostPanel.insets = new Insets(0, 0, 5, 5);
		gbcmanaCostPanel.gridx = 3;
		gbcmanaCostPanel.gridy = 0;
		add(manaCostPanel, gbcmanaCostPanel);
		
		panelActions = new JPanel();
		GridBagConstraints gbcpanelActions = new GridBagConstraints();
		gbcpanelActions.insets = new Insets(0, 0, 5, 0);
		gbcpanelActions.fill = GridBagConstraints.BOTH;
		gbcpanelActions.gridx = 4;
		gbcpanelActions.gridy = 0;
		add(panelActions, gbcpanelActions);
		
		btnAlert = new JButton(MTGConstants.ICON_ALERT);
		panelActions.add(btnAlert);
		
		btnCopy = new JButton(MTGConstants.ICON_COPY);
		panelActions.add(btnCopy);
		
		btnStock = new JButton(MTGConstants.ICON_STOCK);
		panelActions.add(btnStock);
		
		txtTypes = new JTextField();
		txtTypes.setToolTipText("Type");
		GridBagConstraints gbctxtTypes = new GridBagConstraints();
		gbctxtTypes.insets = new Insets(0, 0, 5, 5);
		gbctxtTypes.fill = GridBagConstraints.HORIZONTAL;
		gbctxtTypes.gridx = 1;
		gbctxtTypes.gridy = 1;
		add(txtTypes, gbctxtTypes);
		txtTypes.setColumns(10);
		
		txtRarity = new JTextField();
		txtRarity.setToolTipText("Rarity");
		txtRarity.setColumns(10);
		GridBagConstraints gbctxtRarity = new GridBagConstraints();
		gbctxtRarity.insets = new Insets(0, 0, 5, 5);
		gbctxtRarity.fill = GridBagConstraints.HORIZONTAL;
		gbctxtRarity.gridx = 3;
		gbctxtRarity.gridy = 1;
		add(txtRarity, gbctxtRarity);
		
		lblThumbnail = new JLabel("");
		GridBagConstraints gbclblThumbnail = new GridBagConstraints();
		gbclblThumbnail.gridheight = 2;
		gbclblThumbnail.insets = new Insets(0, 0, 5, 0);
		gbclblThumbnail.gridx = 4;
		gbclblThumbnail.gridy = 1;
		add(lblThumbnail, gbclblThumbnail);
		
		txtText = new MagicTextPane();
		GridBagConstraints gbctxtCardText = new GridBagConstraints();
		gbctxtCardText.gridwidth = 3;
		gbctxtCardText.insets = new Insets(0, 0, 5, 5);
		gbctxtCardText.fill = GridBagConstraints.BOTH;
		gbctxtCardText.gridx = 1;
		gbctxtCardText.gridy = 2;
		add(txtText, gbctxtCardText);
		
		txtFlavor = new JTextPane();
		txtFlavor.setFont(txtFlavor.getFont().deriveFont(Font.ITALIC));
		GridBagConstraints gbctxtFlavour = new GridBagConstraints();
		gbctxtFlavour.gridwidth = 3;
		gbctxtFlavour.insets = new Insets(0, 0, 5, 5);
		gbctxtFlavour.fill = GridBagConstraints.HORIZONTAL;
		gbctxtFlavour.gridx = 1;
		gbctxtFlavour.gridy = 3;
		add(txtFlavor, gbctxtFlavour);
		
		chkReserved = new JCheckBox("(R)");
		GridBagConstraints gbcchkReserved = new GridBagConstraints();
		gbcchkReserved.anchor = GridBagConstraints.WEST;
		gbcchkReserved.insets = new Insets(0, 0, 5, 5);
		gbcchkReserved.gridx = 1;
		gbcchkReserved.gridy = 4;
		add(chkReserved, gbcchkReserved);
		
		txtPower = new JTextField();
		txtPower.setHorizontalAlignment(SwingConstants.CENTER);
		txtPower.setToolTipText("Power");
		GridBagConstraints gbctxtPower = new GridBagConstraints();
		gbctxtPower.insets = new Insets(0, 0, 5, 5);
		gbctxtPower.fill = GridBagConstraints.HORIZONTAL;
		gbctxtPower.gridx = 3;
		gbctxtPower.gridy = 4;
		add(txtPower, gbctxtPower);
		txtPower.setColumns(10);
		
		lblNumber = new JLabel("");
		GridBagConstraints gbclblNumber = new GridBagConstraints();
		gbclblNumber.insets = new Insets(0, 0, 5, 0);
		gbclblNumber.gridx = 4;
		gbclblNumber.gridy = 4;
		add(lblNumber, gbclblNumber);
		
		lstFormats = new JList<>(new DefaultListModel<>());
		lstFormats.setVisibleRowCount(4);
		lstFormats.setCellRenderer((JList<? extends MTGFormat> list, MTGFormat obj, int arg2,boolean arg3, boolean arg4)->{
		var l = new JLabel(obj.getFormat());
					if(obj.getFormatLegality()!=null)
					{
						switch (obj.getFormatLegality()) 
						{
							case BANNED: l.setIcon(MTGConstants.ICON_SMALL_DELETE);break;
							case LEGAL:l.setIcon(MTGConstants.ICON_SMALL_CHECK);break;
							case NOT_LEGAL:l.setIcon(MTGConstants.ICON_SMALL_DELETE);break;
							case RESTRICTED:l.setIcon(MTGConstants.ICON_SMALL_EQUALS);break;
							default: break;
						}
						l.setToolTipText(obj.getFormat() + ":" + obj.getFormatLegality().name());
					}
				return l;
		});
		
		
		
		
		
		GridBagConstraints gbclstFormats = new GridBagConstraints();
		gbclstFormats.insets = new Insets(0, 0, 0, 5);
		gbclstFormats.fill = GridBagConstraints.BOTH;
		gbclstFormats.gridx = 1;
		gbclstFormats.gridy = 5;
		add(new JScrollPane(lstFormats), gbclstFormats);
		
		
		
		
		setEditable(false);
	}

}
