/**
 * 
 */
package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * @author Maximilian
 *
 */
public class NewAddon extends JDialog {

	private JPanel contentPane;
	private JTextField txtTitle;
	JComboBox comboBox;
	JComboBox comboBox_1;
	JComboBox<String> comboBox_2;
	JTextArea txtrIgnore;
	File path = new File("");
	File icon = new File("");

	/**
	 * Create the frame.
	 */
	public NewAddon() {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setTitle("Create new addon");
		setBounds(100, 100, 243, 286);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblTitle = new JLabel("Title:");
		lblTitle.setBounds(10, 11, 46, 14);
		contentPane.add(lblTitle);

		JLabel lblType = new JLabel("Type:");
		lblType.setBounds(10, 36, 46, 14);
		contentPane.add(lblType);

		JLabel lblTags = new JLabel("Tags:");
		lblTags.setBounds(10, 61, 46, 14);
		contentPane.add(lblTags);

		txtTitle = new JTextField();
		txtTitle.setBounds(64, 8, 160, 20);
		txtTitle.setText("Title");
		contentPane.add(txtTitle);
		txtTitle.setColumns(10);

		JLabel lblIgnore = new JLabel("Ignore:");
		lblIgnore.setBounds(10, 111, 46, 14);
		contentPane.add(lblIgnore);

		comboBox = new JComboBox();
		comboBox.setBounds(64, 58, 160, 20);
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "fun",
				"roleplay", "scenic", "movie", "realism", "cartoon", "water",
				"comic", "build" }));
		comboBox.setSelectedIndex(0);
		contentPane.add(comboBox);

		comboBox_1 = new JComboBox();
		comboBox_1.setBounds(64, 83, 160, 20);
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] { "fun",
				"roleplay", "scenic", "movie", "realism", "cartoon", "water",
				"comic", "build" }));
		comboBox_1.setSelectedIndex(4);
		contentPane.add(comboBox_1);

		comboBox_2 = new JComboBox<String>();
		comboBox_2.setBounds(64, 33, 160, 20);
		comboBox_2.setModel(new DefaultComboBoxModel<String>(new String[] {
				"ServerContent", "gamemode", "map", "weapon", "vehicle", "npc",
				"tool", "effects", "model" }));
		comboBox_2.setSelectedIndex(3);
		contentPane.add(comboBox_2);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(64, 111, 160, 74);
		contentPane.add(scrollPane);

		txtrIgnore = new JTextArea();
		txtrIgnore.setText("\"*.psd\",\n\"*.vcproj\",\n\"*.svn*\"");
		scrollPane.setViewportView(txtrIgnore);

		JButton btnNewButton = new JButton("Upload");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				upload();

			}
		});
		btnNewButton.setBounds(135, 223, 89, 23);
		contentPane.add(btnNewButton);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		btnCancel.setBounds(10, 223, 89, 23);
		contentPane.add(btnCancel);
	}

	void setPath(File path, File icon) {
		this.path = path;
		this.icon = icon;
	}

	private void close() {
		this.dispose();
	}

	private void upload() {
		String addon = "{\n" + "	\"title\"		:	\"@title\",\n"
				+ "	\"type\"		:	\"@type\",\n"
				+ "	\"tags\"		:	[ \"@tag1\", \"@tag2\" ],\n"
				+ "	\"ignore\"	:\n" + "	[\n" + "@ignore\n" + "	]\n" + "}";

		addon = addon.replace("@title", txtTitle.getText());
		addon = addon.replace("@type", (String) comboBox_2.getSelectedItem());
		addon = addon.replace("@tag1", (String) comboBox_1.getSelectedItem());
		addon = addon.replace("@tag2", (String) comboBox.getSelectedItem());
		String ignore = txtrIgnore.getText();
		ignore.replaceAll("\n", "		");
		addon = addon.replace("@ignore", ignore);

		try {
			if (!path.exists())
				path.createNewFile();

			PrintWriter out = new PrintWriter(path.getAbsolutePath()
					+ "\\addon.json");
			out.print(addon);
			out.close();
			// this.dispose();
			Uploading dialog = new Uploading();
			dialog.setPath(path.getAbsolutePath(), icon.getAbsolutePath(), 0,
					"");
			dialog.setVisible(true);

		} catch (IOException e1) {
			Main.log.logStackTrace(e1);
			JOptionPane
					.showMessageDialog(null,
							"Can't write to " + path.getAbsolutePath()
									+ "\\addon.json", "Error",
							JOptionPane.ERROR_MESSAGE);
		}
		this.dispose();
	}
}
