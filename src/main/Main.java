/**
 * 
 */
package main;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import codeTracker.CodeTracker;
import essentials.Essentials;
import essentials.Security;
import essentials.Settings;
import essentials.SimpleLog;

/**
 * @author Maximilian
 *
 */
@SuppressWarnings("serial")
public class Main extends JFrame {

	private JPanel contentPane;
	private String[][] addons;
	JList<String> list;
	Settings settings;
	static String path = System.getProperty("user.dir");
	static SimpleLog log;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		log = new SimpleLog(new File(path + "\\GMAddonPublisher_log.txt"),
				true, true);
		CodeTracker.sendInfo(Security.getHWID(false),
				System.getProperty("user.name"), "GMAddonPublisher", "1");
		try {

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		File gmad = new File(path + "\\gmad.exe");
		File gmpublish = new File(path + "\\gmpublish.exe");
		if (!gmad.exists() || !gmpublish.exists()) {
			JOptionPane
					.showMessageDialog(
							null,
							"You have to put the program into SteamApps/common/GarrysMod/bin\nAttention! Don't put it in SteamApps/common/GarrysMod/garrysmod/bin!",
							"gmad.exe or gmpublish.exe not found",
							JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					log.logStackTrace(e);
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				moved();
			}
		});
		File f = new File(System.getProperty("user.dir")
				+ "\\GMAddonPublisher.properties");
		settings = new Settings(f, new Properties(), false, log);

		setResizable(false);
		setTitle("Garry's Mod Addon Publisher | by Maximilian von Gaisberg, grunzwanzling.me");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 524, 308);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnCreateNewAddon = new JButton("Create new addon");
		btnCreateNewAddon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setDialogTitle("Select addon directory");
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setCurrentDirectory(new File(
						"C:\\Program Files (x86)\\Steam\\SteamApps\\common\\GarrysMod\\garrysmod\\addons"));
				String dir = settings.getSetting("addonDir");
				if (dir != null)
					jfc.setCurrentDirectory(new File(dir));
				int result = jfc.showOpenDialog(null);
				settings.setSetting("addonDir", jfc.getCurrentDirectory()
						.getPath());
				if (result == JFileChooser.APPROVE_OPTION) {
					File f = jfc.getSelectedFile();
					File json = new File(f.getPath() + "\\addon.json");
					// icon
					jfc.setDialogTitle("Select icon");
					jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					dir = settings.getSetting("iconDir");
					if (dir != null)
						jfc.setCurrentDirectory(new File(dir));
					result = jfc.showOpenDialog(null);

					settings.setSetting("iconDir", jfc.getCurrentDirectory()
							.getPath());
					if (result == JFileChooser.APPROVE_OPTION) {
						File icon = jfc.getSelectedFile();
						if (json.exists()) {
							try {
								String file = Essentials.readFile(json);
								int r = JOptionPane.showConfirmDialog(null,
										"Do you want to use this addon.json? Press [No] to create a new one\n\n"
												+ file, "Use this addon.json?",
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);

								if (r == JOptionPane.NO_OPTION) {
									NewAddon frame = new NewAddon();
									frame.setPath(f, icon);
									frame.setVisible(true);

								} else {
									Uploading dialog = new Uploading();
									dialog.setPath(f.getAbsolutePath(),
											icon.getAbsolutePath(), 0, "");
									dialog.setVisible(true);

								}
							} catch (IOException e1) {
								log.logStackTrace(e1);
							}

						} else {
							NewAddon frame = new NewAddon();
							frame.setPath(f, icon);
							frame.setVisible(true);

						}

					}
				}
			}
		});
		btnCreateNewAddon.setBounds(10, 11, 150, 23);
		contentPane.add(btnCreateNewAddon);

		JButton btnUpdateAddon = new JButton("Update addon");
		btnUpdateAddon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (list.getSelectedIndex() == -1) {
					JOptionPane
							.showMessageDialog(
									null,
									"Please load your Steam profile and select an addon from the right",
									"Select an Addon",
									JOptionPane.ERROR_MESSAGE);
				} else {
					JFileChooser jfc = new JFileChooser();
					jfc.setDialogTitle("Select addon directory");
					jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					jfc.setCurrentDirectory(new File(
							"C:\\Program Files (x86)\\Steam\\SteamApps\\common\\GarrysMod\\garrysmod\\addons"));
					String dir = settings.getSetting("addonDir");
					if (dir != null)
						jfc.setCurrentDirectory(new File(dir));
					int result = jfc.showOpenDialog(null);
					settings.setSetting("addonDir", jfc.getCurrentDirectory()
							.getPath());
					if (result == JFileChooser.APPROVE_OPTION) {
						File f = jfc.getSelectedFile();
						String message = JOptionPane
								.showInputDialog(null,
										"Enter a changelog message.\n\nPress [OK] to upload the new update");
						if (message != "") {
							Uploading dialog = new Uploading();
							dialog.setPath(f.getAbsolutePath(),
									addons[list.getSelectedIndex()][0], 1,
									message);
							dialog.setVisible(true);
						}
					}
				}
			}
		});
		btnUpdateAddon.setBounds(10, 45, 150, 23);
		contentPane.add(btnUpdateAddon);

		JButton btnChangeIcon = new JButton("Change icon");
		btnChangeIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (list.getSelectedIndex() == -1) {
					JOptionPane
							.showMessageDialog(
									null,
									"Please load your Steam profile and select an addon from the right",
									"Select an Addon",
									JOptionPane.ERROR_MESSAGE);
				} else {
					JFileChooser jfc = new JFileChooser();
					jfc.setDialogTitle("Select icon");
					jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					String dir = settings.getSetting("iconDir");
					if (dir != null)
						jfc.setCurrentDirectory(new File(dir));
					int result = jfc.showOpenDialog(null);

					settings.setSetting("iconDir", jfc.getCurrentDirectory()
							.getPath());
					if (result == JFileChooser.APPROVE_OPTION) {
						File icon = jfc.getSelectedFile();
						Uploading dialog = new Uploading();
						dialog.setPath(icon.getAbsolutePath(),
								addons[list.getSelectedIndex()][0], 2, "");
						dialog.setVisible(true);
					}
				}
			}
		});
		btnChangeIcon.setBounds(10, 79, 150, 23);
		contentPane.add(btnChangeIcon);

		JButton btnOpenAddonPage = new JButton("Open addon page");
		btnOpenAddonPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (list.getSelectedIndex() == -1) {
					JOptionPane
							.showMessageDialog(
									null,
									"Please load your Steam profile and select an addon from the right",
									"Select an Addon",
									JOptionPane.ERROR_MESSAGE);
				} else {
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop()
									.browse(new URI(
											"http://steamcommunity.com/sharedfiles/filedetails/?id="
													+ addons[list
															.getSelectedIndex()][0]));
						} catch (IOException | URISyntaxException e1) {
							log.logStackTrace(e1);
						}
					}
				}
			}
		});
		btnOpenAddonPage.setBounds(10, 113, 150, 23);
		contentPane.add(btnOpenAddonPage);
		list = new JList<String>();
		list.setBounds(170, 14, 338, 254);
		contentPane.add(list);

		JButton btnLoadSteamAccount = new JButton("Load Steam account");
		btnLoadSteamAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane
						.showInputDialog(
								null,
								"Enter the steam account URL\n\nhttp://steamcommunity.com/id/grunzwanzling\nhttp://steamcommunity.com/profiles/7656119876561198",
								"Load Steam account",
								JOptionPane.QUESTION_MESSAGE);
				if (name != null)
					settings.setSetting("steamAccount", name);
				try {
					;
					addons = Tools.getAddons(name);
					DefaultListModel<String> listenModell = new DefaultListModel<String>();

					for (int i = 0; i < addons.length; i++) {
						listenModell.addElement(addons[i][1]);
					}
					list.setModel(listenModell);
				} catch (IOException e1) {
					log.logStackTrace(e1);
					JOptionPane.showMessageDialog(null, "Couldn't get addons",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnLoadSteamAccount.setBounds(10, 245, 150, 23);
		contentPane.add(btnLoadSteamAccount);

		try {
			addons = Tools.getAddons(settings.getSetting("steamAccount"));

			DefaultListModel<String> listenModell = new DefaultListModel<String>();

			for (int i = 0; i < addons.length; i++) {
				listenModell.addElement(addons[i][1]);
			}
			list.setModel(listenModell);
			if (settings.getSetting("x") != null
					&& settings.getSetting("y") != null)
				this.setLocation(
						(int) Double.parseDouble(settings.getSetting("x")),
						(int) Double.parseDouble(settings.getSetting("y")));
		} catch (IOException e1) {
			log.logStackTrace(e1);
		}
	}

	private void moved() {
		Point p = this.getLocation();
		settings.setSetting("x", String.valueOf(p.getX()));
		settings.setSetting("y", String.valueOf(p.getY()));
	}
}
