package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.VersionChecker;
import org.magic.services.extra.GithubUtils;
import org.magic.tools.ImageTools;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class AboutDialog extends MTGUIComponent {

	private static final String NEW_VERSION = "NEW_VERSION";
	private static final long serialVersionUID = 1L;

	public AboutDialog() {
		VersionChecker check = new VersionChecker();
		setLayout(new BorderLayout(0, 0));
		setPreferredSize(new Dimension(600, 400));
		StringBuilder developper = new StringBuilder("<html><center>"); 
			developper.append(MTGControler.getInstance().getLangService().getCapitalize("DEVELOPPERS_ABOUT", "Nichow", "GPL " + new SimpleDateFormat("yyyy").format(new Date())));
			developper.append("<br/><a href='").append(MTGConstants.MTG_DESKTOP_WEBSITE).append("'>").append(MTGConstants.MTG_DESKTOP_WEBSITE).append("</a>");
			try {
				developper.append("<br/>Download count : ").append(GithubUtils.inst().downloadCount());
			} catch (IOException e1) {
				logger.error(e1);
			}
			
			
			developper.append("</center></html>");
		
		JLabel icon = new JLabel(new ImageIcon(MTGConstants.IMAGE_LOGO));
				icon.setFont(new Font("Tahoma", Font.BOLD, 16));
				icon.setVerticalTextPosition(SwingConstants.BOTTOM);
				icon.setHorizontalTextPosition(SwingConstants.CENTER);
				icon.setText(MTGConstants.MTG_APP_NAME +" ("+ check.getVersion()+")");
		
				
		JPanel panneauHaut = new JPanel();
			panneauHaut.setLayout(new BorderLayout());
				
		JTextArea copyText=new JTextArea(MTGConstants.COPYRIGHT_STRING);
				copyText.setWrapStyleWord(true);
				copyText.setLineWrap(true);
				copyText.setRows(4);
	
		JPanel centers = new JPanel();
			   centers.setLayout(new BorderLayout());
			   centers.add(new JLabel("Special thanks to my supporters:"),BorderLayout.NORTH);
			   
			   
		JPanel supporters = new JPanel();
		((FlowLayout) supporters.getLayout()).setAlignment(FlowLayout.LEFT);
		
		 supporters.setForeground(SystemColor.activeCaption);
			   
			   try {
				JsonArray obj = URLTools.extractJson(MTGConstants.MTG_SUPPORTERS_URI).getAsJsonArray();
				
				for(JsonElement element : obj)
				{
					JsonObject supp = element.getAsJsonObject();
					ImageIcon ic = new ImageIcon(ImageTools.readBase64(supp.get("logo").getAsString()).getScaledInstance(50, 50, Image.SCALE_SMOOTH));
					JLabel lab = new JLabel(ic);
							lab.setText(supp.get("name").getAsString());
							lab.setVerticalTextPosition(SwingConstants.BOTTOM);
							lab.setHorizontalTextPosition(SwingConstants.CENTER);
							lab.addMouseListener(new MouseAdapter() {
								@Override
								public void mouseClicked(MouseEvent e) {
									try {
										Desktop.getDesktop().browse(new URI(supp.get("url").getAsString()));
									} catch (Exception e2) {
										logger.error(e2);
									}
								}
							});
							supporters.add(lab);
				}
				
				
				
			} catch (Exception e) {
				logger.error(e);
			}
			   
		
		panneauHaut.add(icon,BorderLayout.NORTH);
		JButton button = new JButton("UPDATE");
		button.addActionListener(e->{
				
				if(check.hasNewVersion())
				{
					MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().get(NEW_VERSION), MTGControler.getInstance().getLangService().get(NEW_VERSION) + " " + MTGControler.getInstance().getLangService().get("AVAILABLE"), MESSAGE_TYPE.WARNING));
				}
				else
				{
					MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().get(NEW_VERSION), "You're up to date", MESSAGE_TYPE.INFO));
				}
		});
		
		panneauHaut.add(button,BorderLayout.EAST);
		JLabel label = new JLabel(developper.toString());
		
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(MTGConstants.MTG_DESKTOP_WEBSITE));
				} catch (Exception e1) {
					logger.error(e1);
				}
			}
		});
		
		
		label.setHorizontalAlignment(SwingConstants.CENTER);
		panneauHaut.add(label,BorderLayout.SOUTH);
		centers.add(supporters,BorderLayout.CENTER);
		add(panneauHaut,BorderLayout.NORTH);
		add(copyText,BorderLayout.SOUTH);
		add(centers,BorderLayout.CENTER);
		
	}

	@Override
	public ImageIcon getIcon() {
		return new ImageIcon(MTGConstants.IMAGE_LOGO);
	}

	@Override
	public String getTitle() {
		return MTGControler.getInstance().getLangService().getCapitalize("ABOUT") + " " + MTGConstants.MTG_APP_NAME;
	}

}
