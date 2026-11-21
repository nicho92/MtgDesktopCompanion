package org.magic.gui.components.dialog;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.VersionChecker;
import org.magic.services.network.URLTools;
import org.magic.services.tools.GithubUtils;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.UITools;

import com.google.gson.JsonElement;

public class AboutDialog extends MTGUIComponent {

	private static final String NEW_VERSION = "NEW_VERSION";
	private static final long serialVersionUID = 1L;

	public AboutDialog() {
		final var check = new VersionChecker();
		setLayout(new BorderLayout(0, 0));
		setPreferredSize(new Dimension(600, 400));
		final var developper = new StringBuilder("<html><center>");
		developper.append(capitalize("DEVELOPPERS_ABOUT", "Nicho",
				"Apache License " + new SimpleDateFormat("yyyy").format(new Date())));
		developper.append("<br/><a href='").append(MTGConstants.MTG_DESKTOP_WEBSITE).append("'>")
				.append(MTGConstants.MTG_DESKTOP_WEBSITE).append("</a>");
		developper.append("<br/>Download count : ").append(GithubUtils.inst().downloadCount());
		developper.append("</center></html>");

		final var icon = new JLabel(new ImageIcon(MTGConstants.IMAGE_LOGO_128));
		icon.setFont(new Font("Tahoma", Font.BOLD, 16));
		icon.setVerticalTextPosition(SwingConstants.BOTTOM);
		icon.setHorizontalTextPosition(SwingConstants.CENTER);
		icon.setText(MTGConstants.MTG_APP_NAME + " (" + check.getVersion() + ")");
		icon.setToolTipText("<html>"
				+ GithubUtils.inst().getReleaseContent().replaceAll(System.lineSeparator(), "<br/>") + "</html>");

		final var panneauHaut = new JPanel();
		panneauHaut.setLayout(new BorderLayout());

		final var copyText = new JTextArea(MTGConstants.COPYRIGHT_STRING);
		copyText.setWrapStyleWord(true);
		copyText.setLineWrap(true);
		copyText.setRows(4);

		final var centers = new JPanel();
		centers.setLayout(new BorderLayout());
		centers.add(new JLabel("Special thanks to my supporters:"), BorderLayout.NORTH);

		final var supporters = new JPanel();
		((FlowLayout) supporters.getLayout()).setAlignment(FlowLayout.LEFT);

		supporters.setForeground(SystemColor.activeCaption);

		try {
			final var obj = URLTools.extractAsJson(MTGConstants.MTG_SUPPORTERS_URI).getAsJsonArray();

			for (final JsonElement element : obj) {
				final var supp = element.getAsJsonObject();
				final var ic = new ImageIcon(ImageTools.readBase64(supp.get("logo").getAsString()).getScaledInstance(50, 50,
						Image.SCALE_SMOOTH));
				final var lab = new JLabel(ic);
				lab.setText(supp.get("name").getAsString());
				lab.setVerticalTextPosition(SwingConstants.BOTTOM);
				lab.setHorizontalTextPosition(SwingConstants.CENTER);
				lab.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						try {
							UITools.browse(supp.get("url").getAsString());
						} catch (final Exception e2) {
							logger.error(e2);
						}
					}
				});
				supporters.add(lab);
			}

		} catch (final Exception e) {
			logger.error(e);
		}

		panneauHaut.add(icon, BorderLayout.NORTH);
		final var button = new JButton("UPDATE");
		button.addActionListener(_ -> {

			if (check.hasNewVersion()) {
				MTGControler.getInstance()
						.notify(new MTGNotification(MTGControler.getInstance().getLangService().get(NEW_VERSION),
								MTGControler.getInstance().getLangService().get(NEW_VERSION) + " "
										+ MTGControler.getInstance().getLangService().get("AVAILABLE"),
								MESSAGE_TYPE.WARNING));
			} else {
				MTGControler.getInstance()
						.notify(new MTGNotification(MTGControler.getInstance().getLangService().get(NEW_VERSION),
								"You're up to date", MESSAGE_TYPE.INFO));
			}
		});

		panneauHaut.add(button, BorderLayout.EAST);
		final var label = new JLabel(developper.toString());

		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				UITools.browse(MTGConstants.MTG_DESKTOP_WEBSITE);
			}
		});

		label.setHorizontalAlignment(SwingConstants.CENTER);
		panneauHaut.add(label, BorderLayout.SOUTH);
		centers.add(supporters, BorderLayout.CENTER);
		add(panneauHaut, BorderLayout.NORTH);
		add(copyText, BorderLayout.SOUTH);
		add(centers, BorderLayout.CENTER);

	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_LOGO;
	}

	@Override
	public String getTitle() {
		return capitalize("ABOUT") + " " + MTGConstants.MTG_APP_NAME;
	}

}
