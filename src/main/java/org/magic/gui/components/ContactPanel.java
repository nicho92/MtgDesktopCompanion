package org.magic.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.magic.api.beans.shop.Contact;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class ContactPanel extends MTGUIComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private transient BindingGroup mbindingGroup;
	private Contact contact = new Contact();
	private JTextArea addressJTextArea;
	private JTextField countryJTextField;
	private JTextField emailJTextField;
	private JTextField lastNameJTextField;
	private JTextField nameJTextField;
	private JButton passwordJPasswordBtn;
	private JTextField telephoneJTextField;
	private JTextField websiteJTextField;
	private JCheckBox emailAcceptationCheckBox;
	private JTextField zipCodeJTextField;
	private JTextField cityJTextField;
	private JCheckBox activeCheckBox;

	public ContactPanel(boolean enableSaveButton) {
		var gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 82, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4 };
		setLayout(gridBagLayout);

		add(new JLangLabel("LASTNAME"), UITools.createGridBagConstraints(null, null, 0, 0));
		add(new JLangLabel("NAME"), UITools.createGridBagConstraints(null, null, 0, 1));
		add(new JLangLabel("ADDRESS"), UITools.createGridBagConstraints(null, null, 0, 2));
		add(new JLangLabel("ZIPCODE"), UITools.createGridBagConstraints(null, null, 0, 3));
		add(new JLangLabel("CITY"), UITools.createGridBagConstraints(null, null, 0, 4));
		add(new JLangLabel("COUNTRY"), UITools.createGridBagConstraints(null, null, 0, 5));
		add(new JLangLabel("EMAIL"), UITools.createGridBagConstraints(null, null, 0, 6));
		add(new JLangLabel("PASSWORD"), UITools.createGridBagConstraints(null, null, 0, 7));
		add(new JLangLabel("TELEPHONE"), UITools.createGridBagConstraints(null, null, 0, 8));
		add(new JLangLabel("WEBSITE"), UITools.createGridBagConstraints(null, null, 0, 9));
		add(new JLangLabel("EMAIL_ACCEPT"), UITools.createGridBagConstraints(null, null, 0, 10));
		add(new JLangLabel("ACTIVE"), UITools.createGridBagConstraints(null, null, 0, 11));

		lastNameJTextField = new JTextField();
		add(lastNameJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 0));

		nameJTextField = new JTextField();
		add(nameJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 1));

		addressJTextArea = new JTextArea();
		add(addressJTextArea, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 2));

		zipCodeJTextField = new JTextField();
		add(zipCodeJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 3));

		cityJTextField = new JTextField();
		add(cityJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 4));

		countryJTextField = new JTextField();
		add(countryJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 5));

		emailJTextField = new JTextField();
		add(emailJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 6));

		passwordJPasswordBtn = new JButton("CHANGE_PASSWORD");
		add(passwordJPasswordBtn, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 7));

		telephoneJTextField = new JTextField();
		add(telephoneJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 8));

		websiteJTextField = new JTextField();
		add(websiteJTextField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 9));

		emailAcceptationCheckBox = new JCheckBox();
		add(emailAcceptationCheckBox, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 10));

		activeCheckBox = new JCheckBox();
		add(activeCheckBox, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 11));

		if(enableSaveButton) {
			var btnUpdate = new JButton(MTGConstants.ICON_SAVE);
			add(btnUpdate, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 12));
			btnUpdate.addActionListener(al->{

				try {
					MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateContact(getContact());
				} catch (SQLException e) {
					logger.error("error saving contact ",e);
					MTGControler.getInstance().notify(e);
				}

			});
		}
		if (contact != null) {
			mbindingGroup = initDataBindings();
		}



		passwordJPasswordBtn.addActionListener(el->{
			String content = JOptionPane.showInputDialog("Type new password");
			try {
				MTG.getEnabledPlugin(MTGDao.class).changePassword(getContact(),content);
			} catch (SQLException e) {
				logger.error("error updating password",e);
				MTGControler.getInstance().notify(e);
			}
		});

	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact newContact) {
		setContact(newContact, true);
	}

	public void setContact(Contact newContact, boolean update) {
		contact = newContact;
		if (update) {
			if (mbindingGroup != null) {
				mbindingGroup.unbind();
				mbindingGroup = null;
			}
			if (contact != null) {
				mbindingGroup = initDataBindings();
			}
		}
	}

	protected BindingGroup initDataBindings() {
		var addressProperty = BeanProperty.create("address");
		var textProperty = BeanProperty.create("text");
		var autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, contact, addressProperty, addressJTextArea, textProperty);
		autoBinding.bind();
		//
		var countryProperty = BeanProperty.create("country");
		var textProperty1 = BeanProperty.create("text");
		var autoBinding1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, contact, countryProperty, countryJTextField, textProperty1);
		autoBinding1.bind();
		//
		var emailProperty = BeanProperty.create("email");
		var textProperty2 = BeanProperty.create("text");
		var autoBinding2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, contact, emailProperty, emailJTextField, textProperty2);
		autoBinding2.bind();
		//
		var lastNameProperty = BeanProperty.create("lastName");
		var textProperty3 = BeanProperty.create("text");
		var autoBinding4 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, contact, lastNameProperty, lastNameJTextField, textProperty3);
		autoBinding4.bind();
		//
		var nameProperty = BeanProperty.create("name");
		var textProperty4 = BeanProperty.create("text");
		var autoBinding5 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, contact, nameProperty, nameJTextField, textProperty4);
		autoBinding5.bind();
		//
		var telephoneProperty = BeanProperty.create("telephone");
		var textProperty6 = BeanProperty.create("text");
		var autoBinding7 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, contact, telephoneProperty, telephoneJTextField, textProperty6);
		autoBinding7.bind();
		//
		var websiteProperty = BeanProperty.create("website");
		var textProperty7 = BeanProperty.create("text");
		var autoBinding8 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, contact, websiteProperty, websiteJTextField, textProperty7);
		autoBinding8.bind();

		var mailAcceptProperty = BeanProperty.create("emailAccept");
		var boolProperty8 = BeanProperty.create("selected");
		var autoBinding9 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, contact, mailAcceptProperty, emailAcceptationCheckBox,boolProperty8);
		autoBinding9.bind();

		var zipProperty = BeanProperty.create("zipCode");
		var textProperty9 = BeanProperty.create("text");
		var autoBinding10 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, contact, zipProperty, zipCodeJTextField, textProperty9);
		autoBinding10.bind();

		var cityProperty = BeanProperty.create("city");
		var textProperty10 = BeanProperty.create("text");
		var autoBinding11 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, contact, cityProperty, cityJTextField, textProperty10);
		autoBinding11.bind();

		var activeProperty = BeanProperty.create("active");
		var boolProperty11 = BeanProperty.create("selected");
		var autoBinding12 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, contact, activeProperty, activeCheckBox,boolProperty11);
		autoBinding12.bind();


		//
		var bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding);
		bindingGroup.addBinding(autoBinding1);
		bindingGroup.addBinding(autoBinding2);
		bindingGroup.addBinding(autoBinding4);
		bindingGroup.addBinding(autoBinding5);
		bindingGroup.addBinding(autoBinding7);
		bindingGroup.addBinding(autoBinding8);
		bindingGroup.addBinding(autoBinding9);
		bindingGroup.addBinding(autoBinding10);
		bindingGroup.addBinding(autoBinding11);
		bindingGroup.addBinding(autoBinding12);

		return bindingGroup;
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_USER;
	}

	@Override
	public String getTitle() {
		return "CONTACT";
	}
}
