package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.magic.tools.ImageTools;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.awt.FlowLayout;

public class AboutDialog extends MTGUIComponent {

	private static final String NEW_VERSION = "NEW_VERSION";
	private static final long serialVersionUID = 1L;

	public AboutDialog() {
		
		setLayout(new BorderLayout(0, 0));
		setPreferredSize(new Dimension(600, 400));
		StringBuilder developper = new StringBuilder("<html>"); 
			developper.append(MTGControler.getInstance().getLangService().getCapitalize("DEVELOPPERS_ABOUT", "Nichow", "GPL " + new SimpleDateFormat("yyyy").format(new Date())));
			developper.append("<br/><a href='").append(MTGConstants.MTG_DESKTOP_WEBSITE).append("'>website</a>");
			developper.append("</html>");
		
		JLabel icon = new JLabel(new ImageIcon(MTGConstants.IMAGE_LOGO));
				icon.setFont(new Font("Tahoma", Font.BOLD, 16));
				icon.setVerticalTextPosition(SwingConstants.BOTTOM);
				icon.setHorizontalTextPosition(SwingConstants.CENTER);
				icon.setText(MTGConstants.MTG_APP_NAME);
		
				
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
		FlowLayout flowLayout = (FlowLayout) supporters.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
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
								public void mouseClicked(MouseEvent arg0) {
									try {
										Desktop.getDesktop().browse(new URI(supp.get("url").getAsString()));
									} catch (Exception e) {
										logger.error(e);
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
				VersionChecker check = new VersionChecker();
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
		panneauHaut.add(new JLabel(developper.toString()),BorderLayout.SOUTH);
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
