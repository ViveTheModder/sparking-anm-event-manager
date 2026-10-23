package gui;
//Sparking ANM Event Manager by ViveTheJoestar
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.MenuBarUI;
import cmd.Animation;
import cmd.Event;
import cmd.EventInfo;
import cmd.Main;
/* TODO: 
 * 1. [DONE] IMPORTANT - Make a version of Event.getFlagInfo() that checks the right checkboxes per event. (HINT: Rely on byte arrays).
 * 2. [DONE] IMPORTANT - Add spinner that can navigate from event to event.
 * 3. [DONE] IMPORTANT - Add two text fields: one for the total number of frames, the other for the current event's keyframe. Make them editable.
 * 4. [DONE] IMPORTANT - Keep track of last opened ANM directory.
 * 5. [DONE] IMPORTANT - Implement "Save Changes" functionality.
 * 6. [DONE] IMPORTANT - Add feature that can add/remove events (a.k.a increment/decrement the number of events), preferably separate from the spinner.
 * 7. [DONE] FUN - Add "Themes" (pre-configured color sets).
 * 8. [DONE] OPTIONAL - Add "Save As..." button next to the "Save Changes" one.
 * 9. NEO. I NEED YOU. */
public class Program {
	private static final Dimension SPIN_SIZE = new Dimension(48, 24);
	private static final Font BOLD = new Font("Tahoma", Font.BOLD, 20);
	private static final Font NORMAL = new Font("Tahoma", Font.PLAIN, 12);
	private static final String HTML_HEADER = "<html><body style='text-align: justify; text-justify: auto;'>";
	private static final String HTML_HEADER_TT = "<html><p style='text-align: center; font-style: italic; font-size: 14px;'>";
	private static final String HTML_FOOTER = "</body></html>";
	private static final String HTML_FOOTER_TT = "</p></html>";
	private static final String WINDOW_TITLE = "Sparking! ANM Event Manager";
	private static final String[] ITEM_TEXT = { "Open ANM...", "Apply Events", "Copy ANM...", "Add Event", "Remove Event", "Credits" };
	private static final String[] ITEM_TIPS = {
		"Only accepts PS2 DBZ BT2 & BT3 animations that are deemed valid.",
		"Carries over the changes (selected checkboxes) applied to the animation's current events.",
		"Saves the currently loaded animation as a new one.",
		"Inserts an event after the selected one.", 
		"Deletes the selected event.",
		"Learn more about " + WINDOW_TITLE + "."
	};
	private static final String[] MENU_TEXT = { "File", "Event", "Themes", "Help" };
	private static final Toolkit DEF_TOOLKIT = Toolkit.getDefaultToolkit();
	private static final Image ICON_IMG = DEF_TOOLKIT.getImage(ClassLoader.getSystemResource("img/icon.png"));
	
	private static Animation getAnimationFromChooser(JFrame frame, JCheckBox wiiCheck, File[] lastDir) throws IOException {
		JFileChooser fc = new JFileChooser();
		fc.setAcceptAllFileFilterUsed(false);
		fc.setDialogTitle("Open ANM File...");
		fc.setFileFilter(new FileNameExtensionFilter("Animation (.ANM)", "anm"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (lastDir[0] != null) fc.setCurrentDirectory(lastDir[0]); 
		int result = fc.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {
			File tmp = fc.getSelectedFile();
			Animation anm = new Animation(tmp, wiiCheck.isSelected());
			if (anm.isValid()) {
				frame.setTitle(WINDOW_TITLE + " - " + tmp.getCanonicalPath());
				lastDir[0] = tmp;
				return anm;
			}
			else {
				errorBeep();
				String err = "Invalid ANM file!";
				JOptionPane.showMessageDialog(frame, err, WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		return null;
	}
	private static File getFileFromSaveAs(JFrame frame, File[] lastDir) throws IOException {
		JFileChooser fc = new JFileChooser();
		fc.setAcceptAllFileFilterUsed(false);
		fc.setDialogTitle("Save ANM File As...");
		fc.setFileFilter(new FileNameExtensionFilter("Animation (.ANM)", "anm"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (lastDir[0] != null) fc.setCurrentDirectory(lastDir[0]); 
		int result = fc.showSaveDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {
			File newFile = fc.getSelectedFile();
			frame.setTitle(WINDOW_TITLE + " - " + newFile.getCanonicalPath());
			lastDir[0] = newFile;
			return newFile;
		}
		return null;
	}
	private static void errorBeep() {
		Runnable runWinErrorSnd = (Runnable) DEF_TOOLKIT.getDesktopProperty("win.sound.exclamation");
		if (runWinErrorSnd != null) runWinErrorSnd.run();
	}
	private static void setAnmPanel(Color bg, Color fg, Color btnBg, Color btnFg, JCheckBox ch, JLabel[] lbl, JPanel pnl, JSpinner[] spn, boolean update) {
		ch.setBackground(bg);
		ch.setForeground(fg);
		ch.setFont(BOLD);
		if (!update) ch.setToolTipText(HTML_HEADER_TT + "When checked, the byte order will be set to Big Endian." + HTML_FOOTER_TT);
		pnl.setBackground(bg);
		Box spinBox = Box.createHorizontalBox();
		String[] lblText = { "Duration", "Event No.", "Keyframe" };
		String[] lblTips = { "The length of the animation in frames.", null, "The keyframe associated with the selected event." };
		for (int lblCnt = 0; lblCnt < lbl.length; lblCnt++) {
			if (!update) lbl[lblCnt] = new JLabel(lblText[lblCnt]);
			lbl[lblCnt].setForeground(btnFg);
			lbl[lblCnt].setFont(BOLD);
			lbl[lblCnt].setHorizontalAlignment(JLabel.CENTER);
			if (lblTips[lblCnt] != null && !update)
				lbl[lblCnt].setToolTipText(HTML_HEADER_TT + lblTips[lblCnt] + HTML_FOOTER_TT);
		}
		if (!update) spinBox.add(Box.createHorizontalGlue());
		for (int spinCnt = 0; spinCnt < spn.length; spinCnt++) {
			spn[spinCnt].setBackground(bg);
			spn[spinCnt].setFont(NORMAL);
			spn[spinCnt].setForeground(fg);
			spn[spinCnt].setMinimumSize(SPIN_SIZE);
			spn[spinCnt].setMaximumSize(SPIN_SIZE);
			spn[spinCnt].setPreferredSize(SPIN_SIZE);
			if (!update) {
				spinBox.add(lbl[spinCnt]);
				spinBox.add(Box.createHorizontalStrut(8));
				spinBox.add(spn[spinCnt]);
				spinBox.add(Box.createHorizontalStrut(8));
			}
		}
		if (!update) {
			spinBox.add(ch);
			spinBox.add(Box.createHorizontalGlue());
			pnl.add(Box.createVerticalStrut(12));
			pnl.add(spinBox);
			pnl.add(Box.createVerticalStrut(12));
		}
	}
	private static void setEventPanel(Color bg, Color fg, JPanel panel, JCheckBox[] checks, JLabel[] labels, String[] rows, boolean update) {
		panel.setBackground(bg);
		EventInfo[] rowObjs = null;
		if (!update) {
			rowObjs = new EventInfo[rows.length];
			for (int rowCnt = 0; rowCnt < rowObjs.length; rowCnt++)
				rowObjs[rowCnt] = new EventInfo(rows[rowCnt]);
		}
		for (int checkCnt = 0; checkCnt < checks.length; checkCnt++) {
			if (!update)
				checks[checkCnt] = new JCheckBox(HTML_HEADER + rowObjs[checkCnt].getDesc() + HTML_FOOTER);
			checks[checkCnt].setBackground(bg);
			checks[checkCnt].setForeground(fg);
			checks[checkCnt].setFont(NORMAL);
			if (!update) checks[checkCnt].setToolTipText(HTML_HEADER_TT + rowObjs[checkCnt].getDesc() + HTML_FOOTER_TT);
			if (checkCnt % 8 == 0) {
				if (!update)
					labels[checkCnt / 8] = new JLabel(HTML_HEADER + "Flag Set " + ((checkCnt / 8) + 1) + HTML_FOOTER);
				labels[checkCnt / 8].setAlignmentX(JLabel.CENTER_ALIGNMENT);
				labels[checkCnt / 8].setBackground(bg);
				labels[checkCnt / 8].setForeground(fg);
				labels[checkCnt / 8].setFont(BOLD);
				labels[checkCnt / 8].setHorizontalAlignment(JLabel.CENTER);
			}
		}
		if (!update) {
			for (int lblCnt = 0; lblCnt < labels.length; lblCnt++)
				panel.add(labels[lblCnt], lblCnt);
			int compCnt = labels.length;
			for (int checkCnt = 0; checkCnt < 64; checkCnt++) {
				int index = 8 * (checkCnt % 8) + (checkCnt / 8);
				if (index < checks.length) {
					panel.add(checks[index], compCnt);
					compCnt++;
				}
			}
		}
	}
	private static void setFlagChecks(Event[] events, JCheckBox[] checks, JSpinner spin) {
		for (JCheckBox ch: checks) ch.setSelected(false);
		byte[] flags = events[(int) spin.getValue() - 1].getFlags();
		for (int byteCnt = 0; byteCnt < flags.length; byteCnt++) {
			for (int bitCnt = 0; bitCnt < 8; bitCnt++) {
				int flag = 1 << bitCnt;
				if ((flags[byteCnt] & flag) == flag) {
					int index = 8 * byteCnt + bitCnt;
					checks[index].setSelected(true);
				}
			}
		}
	}
	private static void setMenuBar(Color bg, Color btnBg, Color btnFg, JMenuBar bar, JMenu[] menus, JMenuItem[] items, JMenuItem[] themeItems, Theme[] thm, boolean update) {
		for (int menuCnt = 0; menuCnt < menus.length; menuCnt++) {
			if (!update) menus[menuCnt] = new JMenu(HTML_HEADER_TT + MENU_TEXT[menuCnt] + HTML_FOOTER_TT);
			menus[menuCnt].setBackground(bg);
			menus[menuCnt].setForeground(btnFg);
			menus[menuCnt].setFont(BOLD);
			menus[menuCnt].setOpaque(true);
		}
		for (int itemCnt = 0; itemCnt < items.length; itemCnt++) {
			if (!update)items[itemCnt] = new JMenuItem(ITEM_TEXT[itemCnt]);
			items[itemCnt].setBackground(btnBg);
			items[itemCnt].setForeground(btnFg);
			items[itemCnt].setFont(NORMAL);
			items[itemCnt].setOpaque(true);
			if (!update) items[itemCnt].setToolTipText(HTML_HEADER_TT + ITEM_TIPS[itemCnt] + HTML_FOOTER_TT);
		}
		for (int themeCnt = 0; themeCnt < themeItems.length; themeCnt++) {
			if (!update) themeItems[themeCnt] = new JMenuItem(thm[themeCnt].getName());
			themeItems[themeCnt].setBackground(btnBg);
			themeItems[themeCnt].setForeground(btnFg);
			themeItems[themeCnt].setFont(NORMAL);
			themeItems[themeCnt].setOpaque(true);
		}
		//Solution taken from user28595's answer: https://answall.com/q/149373/how-to-change-the-color-of-a-jmenubar/
		bar.setUI(new MenuBarUI() {
			@Override
		    public void paint(Graphics g, JComponent c) {
		        g.setColor(bg);
		        g.fillRect(0, 0, c.getWidth(), c.getHeight());
		    }
		});		
	}
	private static void setUI() throws IOException {
		final Animation[] anm = new Animation[1];
		final File[] lastDir = new File[1];
		int[] spinMaxVals = { Short.MAX_VALUE, 1, Short.MAX_VALUE };
		File anmCsv = new File("./csv/anm-flags.csv"), effCsv = new File("./csv/eff-flags.csv");
		if (!(anmCsv.exists() && effCsv.exists())) {
			errorBeep();
			String msg = "Required CSV files (for the event names) are not present!";
			JOptionPane.showMessageDialog(null, msg, WINDOW_TITLE, JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		String[] anmEventRows = Main.readCsvRows(anmCsv);
		String[] effEventRows = Main.readCsvRows(effCsv);
		Theme[] customThemes = ThemeBuilder.getAllThemes();
		Theme[] themes = new Theme[customThemes.length + 1];
		//Set themes
		Color fragola = new Color(241, 168, 179);
		Color vaniglia = new Color(242, 236, 233);
		Color ciocco = new Color(93, 72, 72);
		if (customThemes.length > 0) System.arraycopy(customThemes, 0, themes, 0, customThemes.length);
		themes[customThemes.length] = new Theme("Neopolitan (Default)", fragola, vaniglia, ciocco, Color.black);
		//Set components
		Dimension minSize = new Dimension(512, 1024), prefSize = new Dimension(1152, 1024);
		JCheckBox wiiCheck = new JCheckBox("Wii Mode");
		JCheckBox[] anmChecks = new JCheckBox[anmEventRows.length], effChecks = new JCheckBox[effEventRows.length];
		JLabel[] anmLabels = new JLabel[anmChecks.length / 8], effLabels = new JLabel[effChecks.length / 8];
		JLabel[] neoPanelLbls = new JLabel[3];
		JFrame frame = new JFrame();
		JMenu[] menus = new JMenu[MENU_TEXT.length];
		JMenuBar bar = new JMenuBar();
		JMenuItem[] items = new JMenuItem[ITEM_TEXT.length], themeItems = new JMenuItem[themes.length];
		JPanel neoPanel = new JPanel();
		JPanel poliPanel = new JPanel(new GridLayout(9, 0));
		JPanel tanPanel = new JPanel(new GridLayout(9, 0));
		JPanel romanPanel = new JPanel(new BorderLayout());
		SpinnerNumberModel[] models = new SpinnerNumberModel[3];
		JSpinner[] spinners = new JSpinner[3];
		for (int spinCnt = 0; spinCnt < spinners.length; spinCnt++) {
			models[spinCnt] = new SpinnerNumberModel(0, 0, spinMaxVals[spinCnt], 1);
			spinners[spinCnt] = new JSpinner(models[spinCnt]);
			spinners[spinCnt].setEnabled(false);
			spinners[spinCnt].setMinimumSize(SPIN_SIZE);
			spinners[spinCnt].setMaximumSize(SPIN_SIZE);
			spinners[spinCnt].setPreferredSize(SPIN_SIZE);
		}
		//Set component properties
		neoPanel.setLayout(new BoxLayout(neoPanel, BoxLayout.Y_AXIS));
		setMenuBar(fragola, ciocco, vaniglia, bar, menus, items, themeItems, themes, false);
		setAnmPanel(fragola, vaniglia, ciocco, vaniglia, wiiCheck, neoPanelLbls, neoPanel, spinners, false);
		setEventPanel(vaniglia, Color.black, poliPanel, anmChecks, anmLabels, anmEventRows, false);
		setEventPanel(ciocco, vaniglia, tanPanel, effChecks, effLabels, effEventRows, false);
		//Add listeners for every checkbox
		for (int checkCnt = 0; checkCnt < anmChecks.length; checkCnt++) {
			final int index = checkCnt;
			anmChecks[checkCnt].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (anm[0] != null) {
						int keyFrameIndex = (int) spinners[1].getValue() - 1;
						int newKeyframe = (int) spinners[2].getValue();
						anm[0].setEventByte(newKeyframe, index, keyFrameIndex, true, anmChecks[index].isSelected());
					}
				}
			});
		}
		for (int checkCnt = 0; checkCnt < effChecks.length; checkCnt++) {
			final int index = checkCnt;
			effChecks[checkCnt].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (anm[0] != null) {
						int keyframeIndex = (int) spinners[1].getValue() - 1;
						anm[0].setEventByte(-1, index, keyframeIndex, false, effChecks[index].isSelected());
					}
				}		
			});
		}
		//Add listener for the Open menu item
		items[0].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					anm[0] = getAnimationFromChooser(frame, wiiCheck, lastDir);
					if (anm[0] != null) {
						Event[] vfxEvent = { anm[0].getEffEvent() };
						Event[] events = anm[0].getAllEvents();
						int defVal = events.length > 0 ? 1 : 0;
						for (JSpinner spin: spinners) spin.setEnabled(true);
						spinners[0].setValue(anm[0].getDuration());
						spinners[1].setModel(new SpinnerNumberModel(defVal, 0, events.length, 1));
						if (events.length > 0) {
							spinners[2].setValue(events[0].getKeyframe());
							setFlagChecks(events, anmChecks, spinners[1]);
							setFlagChecks(vfxEvent, effChecks, spinners[1]);
						}
					}
					else {
						for (JCheckBox ch: anmChecks) ch.setSelected(false);
						for (JCheckBox ch: effChecks) ch.setSelected(false);
						for (JSpinner spin: spinners) spin.setEnabled(false);
					}
				} 
				catch (IOException ex) {
					showExceptionDialog(frame, ex);		
				}
			}
		});
		//Add listener for the Apply menu item
		items[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (anm[0] != null) {
						int duration = (int) spinners[0].getValue();
						int keyframeIndex = (int) spinners[1].getValue() - 1;
						int keyframe = (int) spinners[2].getValue();
						if (!anm[0].isClosed()) {
							anm[0].setAllEvents();
							anm[0].setDuration(duration);
							anm[0].setEffEvent();
							anm[0].setEventKeyframe(keyframe, keyframeIndex);
							anm[0].closeStream();
							showSuccessDialog(frame, false);
						}
						else {
							anm[0].reopenStream(null, duration, keyframe, keyframeIndex);
							showSuccessDialog(frame, false);
						}
					}
				} 
				catch (IOException ex) {
					showExceptionDialog(frame, ex);
				}
			}
		});
		//Add listener for the Copy menu item
		items[2].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (anm[0] != null) {
						File newFile = getFileFromSaveAs(frame, lastDir);
						int duration = (int) spinners[0].getValue();
						int keyframeIndex = (int) spinners[1].getValue() - 1;
						int keyframe = (int) spinners[2].getValue();
						if (newFile != null) {
							anm[0].reopenStream(newFile, duration, keyframe, keyframeIndex);
							showSuccessDialog(frame, true);
						}
					}
				}
				catch (IOException ex) {
					showExceptionDialog(frame, ex);
				}
			}
		});
		//Add listener for the Add Event menu item
		items[3].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (anm[0] != null) {
						int eventIndex = (int) spinners[1].getValue() - 1;
						int keyframe = (int) spinners[2].getValue() + 1;
						if (anm[0].isClosed()) {
							anm[0].addOrRemoveEvent(eventIndex, keyframe, true, false);
							anm[0].closeStream();
						}
						else anm[0].addOrRemoveEvent(eventIndex, keyframe, true, true);
						Event[] events = anm[0].getAllEvents();
						if (events.length > 0) {
							spinners[1].setModel(new SpinnerNumberModel(1, 0, events.length, 1));
							spinners[2].setValue(events[0].getKeyframe());
							setFlagChecks(events, anmChecks, spinners[1]);
						}
					}
				}
				catch (IOException ex) {
					showExceptionDialog(frame, ex);
				}
			}
		});
		//Add listener for the Remove Event menu item
		items[4].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (anm[0] != null) {
						int eventIndex = (int) spinners[1].getValue() - 1;
						if (anm[0].isClosed()) {
							anm[0].addOrRemoveEvent(eventIndex, 0, false, false);
							anm[0].closeStream();
						}
						else anm[0].addOrRemoveEvent(eventIndex, 0, false, true);
						Event[] events = anm[0].getAllEvents();
						int numEvents = anm[0].getNumEvents();
						if (numEvents > 0) {
							spinners[1].setModel(new SpinnerNumberModel(1, 1, events.length, 1));
							spinners[2].setValue(events[0].getKeyframe());
							setFlagChecks(events, anmChecks, spinners[1]);
						}
						else {
							spinners[1].setModel(new SpinnerNumberModel(0, 0, spinMaxVals[1], 1));
							spinners[2].setModel(new SpinnerNumberModel(0, 0, spinMaxVals[2], 1));
							for (JCheckBox ch: anmChecks) ch.setSelected(false);
						}
					}
				}
				catch (IOException ex) {
					showExceptionDialog(frame, ex);
				}
			}
		});
		//Add listener for the Credits menu item
		items[5].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = "Originally went under the name \"Tenkaichi Basic ANM Editor\".\n";
				msg += "Development started in April 2025, only to be abandoned.\nConsider this tool a remake of that...\n\n";
				msg += "Themes feature suggested by SpideyGirl.\nSon Goku theme suggested by Valen2006.";
				Image iconImgSmall = ICON_IMG.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
				JOptionPane.showMessageDialog(null, msg, WINDOW_TITLE, JOptionPane.PLAIN_MESSAGE, new ImageIcon(iconImgSmall));
				
			}
		});
		spinners[1].addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				try {
					if (anm[0] != null) {
						int keyframeIndex = (int) spinners[1].getValue() - 1;
						Event[] events = anm[0].getAllEvents();
						spinners[2].setValue(events[keyframeIndex].getKeyframe());
						setFlagChecks(events, anmChecks, spinners[1]);
					}
				} catch (IOException ex) {
					showExceptionDialog(frame, ex);
				}
			}
		});
		//Add components
		menus[0].add(items[0]);
		menus[0].add(items[2]);
		menus[1].add(items[1]);
		menus[1].add(items[3]);
		menus[1].add(items[4]);
		menus[3].add(items[5]);
		for (int themeCnt = 0; themeCnt < themes.length; themeCnt++) {
			final int index = themeCnt;
			menus[2].add(themeItems[themeCnt]);
			themeItems[index].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Color fragola = themes[index].getTopColor(), vaniglia = themes[index].getMedColor();
					Color ciocco = themes[index].getBtmColor(), nero = themes[index].getTxtColor();
					setMenuBar(fragola, ciocco, vaniglia, bar, menus, items, themeItems, themes, true);
					setAnmPanel(fragola, vaniglia, ciocco, vaniglia, wiiCheck, neoPanelLbls, neoPanel, spinners, true);
					setEventPanel(vaniglia, nero, poliPanel, anmChecks, anmLabels, anmEventRows, true);
					setEventPanel(ciocco, vaniglia, tanPanel, effChecks, effLabels, effEventRows, true);
				}
			});
		}
		for (JMenu m: menus) bar.add(m); 
		romanPanel.add(neoPanel, BorderLayout.NORTH);
		romanPanel.add(poliPanel, BorderLayout.CENTER);
		romanPanel.add(tanPanel, BorderLayout.SOUTH);
		frame.add(romanPanel);
		//Set frame properties
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setIconImage(ICON_IMG);
		frame.setJMenuBar(bar);
		frame.setMinimumSize(minSize);
		frame.setSize(prefSize);
		frame.setLocationRelativeTo(null);
		frame.setTitle(WINDOW_TITLE);
		frame.setVisible(true);
	}
	private static void showExceptionDialog(JFrame frame, Exception e) {
		errorBeep();
		String exMsg = e.getClass().getSimpleName() + ": " + e.getMessage() + "\n";
		for (StackTraceElement ste: e.getStackTrace()) exMsg += ste.toString() + "\n";
		JOptionPane.showMessageDialog(frame, exMsg, "Exception", JOptionPane.ERROR_MESSAGE);		
	}
	private static void showSuccessDialog(JFrame frame, boolean saveAs) {
		DEF_TOOLKIT.beep();
		String msg = saveAs ? "Changes successfully saved to new file!" : "Changes saved successfully!";
		JOptionPane.showMessageDialog(frame, msg, WINDOW_TITLE, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void start() {	
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			setUI();
		} 
		catch (Exception e) {
			showExceptionDialog(null, e);
		}
	}
}