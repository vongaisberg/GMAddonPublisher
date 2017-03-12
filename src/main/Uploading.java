/**
 * 
 */
package main;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * @author Maximilian
 *
 */
public class Uploading extends JDialog {

	private final JPanel contentPanel = new JPanel();
	String icon, path, message;
	JTextArea txtrLog;
	String id;
	int update;

	/**
	 * Create the dialog.
	 */
	public Uploading() {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setTitle("Uploading Addon");
		setBounds(100, 100, 722, 533);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 696, 448);
		contentPanel.add(scrollPane);

		txtrLog = new JTextArea();
		txtrLog.setText("");
		scrollPane.setViewportView(txtrLog);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		btnCancel.setBounds(617, 470, 89, 23);
		contentPanel.add(btnCancel);

		Timer timer = new Timer();

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (update == 0)
					upload(path, icon);
				if (update == 1)
					update(path, icon, message);
				if (update == 2)
					icon(icon, path);
			}
		}, 100);

	}

	private void close() {
		this.dispose();
	}

	void setPath(String path, String icon, int update, String message) {
		this.path = path;
		this.icon = icon;
		this.update = update;
		this.message = message;
	}

	void upload(String path, String icon) {
		Main.log.info("Uploading");
		BufferedReader reader = Tools.createGMA(path);
		String line;
		boolean success = false;
		try {

			while ((line = reader.readLine()) != null) {
				Main.log.info(line);
				txtrLog.setText(txtrLog.getText() + line + "\n");
				if (line.startsWith("Successfully saved to")) {
					success = true;
					Main.log.info("\nGMA FILE HAS BEEN CREATED\n\nUploading...");
					txtrLog.setText(txtrLog.getText()
							+ "\nGMA FILE HAS BEEN CREATED\n\nUploading...");
				}
			}

		} catch (IOException e) {
			Main.log.logStackTrace(e);
		}
		if (!success) {
			Main.log.info("\nERROR: Something went wrong while creating the GMA file.");
			txtrLog.setText(txtrLog.getText()
					+ "\nERROR: Something went wrong while creating the GMA file.");
		} else {
			reader = Tools.uploadAddon(path, icon);
			success = false;
			try {
				while ((line = reader.readLine()) != null) {
					Main.log.info(line);
					txtrLog.setText(txtrLog.getText() + line + "\n");
					if (line.startsWith("All done!")) {
						success = true;
						Main.log.info("\nADDON HAS BEEN UPLOADED");
						txtrLog.setText(txtrLog.getText()
								+ "\nADDON HAS BEEN UPLOADED");

						id = txtrLog.getText().substring(
								txtrLog.getText().indexOf("UID: ") + 5,
								txtrLog.getText().indexOf("\n",
										txtrLog.getText().indexOf("UID: ")));
						int i = JOptionPane
								.showConfirmDialog(
										null,
										"The addon was successfully uploaded.\nIt is currently set to private, you need to go to the addon page to set it to public if you want other people to see it.\nOpen the addon page now?",
										"Success", JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);

						if (i == JOptionPane.YES_OPTION) {
							try {
								Desktop.getDesktop().browse(
										new URI(
												"http://steamcommunity.com/sharedfiles/filedetails/?id="
														+ id));
							} catch (URISyntaxException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			} catch (IOException e) {
				Main.log.logStackTrace(e);
			}
			if (!success) {
				Main.log.error(" Something went wrong while uploading the addon.");
				txtrLog.setText(txtrLog.getText()
						+ "\nERROR: Something went wrong while uploading the addon.");
			}

		}
		if (!success) {
			JOptionPane
					.showMessageDialog(
							null,
							"Something went wrong while creating a new addon. Check the log and try again.",
							"ERROR", JOptionPane.ERROR_MESSAGE);
		}
		this.dispose();
	}

	void update(String path, String id, String message) {
		Main.log.info("Updating");
		BufferedReader reader = Tools.createGMA(path);
		String line;
		boolean success = false;
		try {

			while ((line = reader.readLine()) != null) {
				Main.log.info(line);
				txtrLog.setText(txtrLog.getText() + line + "\n");
				if (line.startsWith("Successfully saved to")) {
					success = true;
					Main.log.info("\nGMA FILE HAS BEEN CREATED\n\nUploading...");
					txtrLog.setText(txtrLog.getText()
							+ "\nGMA FILE HAS BEEN CREATED\n\nUploading...");
				}
			}

		} catch (IOException e) {
			Main.log.logStackTrace(e);
		}
		if (!success) {
			Main.log.error("Something went wrong while creating the GMA file.");
			txtrLog.setText(txtrLog.getText()
					+ "\nERROR: Something went wrong while creating the GMA file.");
		} else {
			reader = Tools.updateAddon(path, icon, message);
			success = false;
			try {
				while ((line = reader.readLine()) != null) {
					Main.log.info(line);
					txtrLog.setText(txtrLog.getText() + line + "\n");
					if (line.startsWith("Success!")) {
						success = true;
						Main.log.info("\nADDON HAS BEEN UPDATED");
						txtrLog.setText(txtrLog.getText()
								+ "\nADDON HAS BEEN UPDATED");

						JOptionPane.showMessageDialog(null,
								"The addon was successfully updated",
								"Success!", JOptionPane.OK_OPTION);
					}
				}
			} catch (IOException e) {
				Main.log.logStackTrace(e);
			}
			if (!success) {
				Main.log.error("Something went wrong while updating the addon.");
				txtrLog.setText(txtrLog.getText()
						+ "\nERROR: Something went wrong while updating the addon.");
			}

		}
		if (!success) {
			JOptionPane
					.showMessageDialog(
							null,
							"Something went wrong while updating the addon. Check the log and try again.",
							"ERROR", JOptionPane.ERROR_MESSAGE);
		}
		this.dispose();
	}

	void icon(String icon, String id) {
		Main.log.info("Updating icon");
		BufferedReader reader = Tools.updateIcon(icon, id);
		boolean success = false;
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				Main.log.info(line);
				txtrLog.setText(txtrLog.getText() + line + "\n");
				if (line.startsWith("Success!")) {
					success = true;
					Main.log.info("\nICON HAS BEEN UPDATED");
					txtrLog.setText(txtrLog.getText()
							+ "\nICON HAS BEEN UPDATED");

					JOptionPane.showMessageDialog(null,
							"The icon was successfully updated", "Success!",
							JOptionPane.OK_OPTION);
				}
			}
		} catch (IOException e) {
			Main.log.logStackTrace(e);
		}
		if (!success) {
			Main.log.error("Something went wrong while updating the icon.");
			txtrLog.setText(txtrLog.getText()
					+ "\nERROR: Something went wrong while updating the icon.");
		}

		if (!success) {
			JOptionPane
					.showMessageDialog(
							null,
							"Something went wrong while updating the icon. Check the log and try again.",
							"ERROR", JOptionPane.ERROR_MESSAGE);
		}
		this.dispose();
	}

}
