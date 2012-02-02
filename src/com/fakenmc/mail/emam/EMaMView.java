/*
 * Copyright (c) 2008, Nuno Fachada
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice, 
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, 
 *     this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *   * Neither the name of the FakenMC.com nor the names of its contributors 
 *     may be used to endorse or promote products derived from this software without 
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.fakenmc.mail.emam;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.util.Properties;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class implements a GUI view for EMaM.
 * 
 * @author Nuno Fachada
 *
 */
public class EMaMView 
implements EMaMViewInterface, ActionListener, AddressTablesObserver, 
FileStatusObserver, ClipboardOwner {
	
	/* Controller instance. */
	private EMaMControllerInterface controller;
	/* Model instance. */
	private EMaMModelInterface model;
	/* Language properties. */
	private Properties langProperties;
	
	/* Main window frame. */
	private JFrame frame = null;
	/* File chooser. */
	private JFileChooser fceMaM;
	/* File filters. */
	private FileFilter emamFileFilter;
	/* Menu items. */
	private JMenuItem fileNewMenuItem;
	private JMenuItem fileOpenMenuItem;
	private JMenuItem fileSaveMenuItem;
	private JMenuItem fileSaveAsMenuItem;
	private JMenuItem fileOptionsMenuItem;
	private JMenuItem fileExitMenuItem;
	private JMenuItem helpAboutMenuItem;

	/* Button for adding an email address to the mailing list. */
	private JButton addToMailListButton;
	/* Button for deleting an email address from the mailing list. */
	private JButton delFromMailListButton;
	/* Button for copying the mailing list to the clipboard. */
	private JButton copyMailListToClipboardButton;
	/* Button for transferring an address from the mail list to the removed list. */
	private JButton moveFromMailToRemovedButton;
	/* Button for transferring an address from the removed list to the mail list. */
	private JButton moveFromRemovedToMailButton;
	/* Button for adding an email address to the removed list. */
	private JButton addToRemovedButton;
	/* Button for deleting an email address from the removed list. */
	private JButton delFromRemovedButton;
	/* Button for copying the removed list to the clipboard. */
	private JButton copyRemovedToClipboardButton;
	/* Button for adding an email address to the returned list. */
	private JButton addToReturnedButton;
	/* Button for deleting an email address from the returned list. */
	private JButton delFromReturnedButton;
	/* Button for incrementing the number of times an email was returned. */
	private JButton incReturnedButton;
	/* Button for decrementing the number of times an email was returned. */
	private JButton decReturnedButton;
	/* Button for processing returned emails from mail list to removed. */
	private JButton processReturnedButton;
	/* Button for importing a file into the extraction text area. */
	private JButton importFromFileButton;
	/* Button for extracting emails the text area. */
	private JButton extractButton;
	/* Button for clearing the text area. */
	private JButton clearTextAreaButton;
	/* Button for copying extracted mails to the mail list. */
	private JButton moveExtractedToMailListButton;
	/* Button for copying extracted mails to the removed list. */
	private JButton moveExtractedToRemovedButton;
	/* Button for copying extracted mails to the returned. */
	private JButton moveExtractedToReturnedButton;
	/* Button for adding an email address to the mailing list. */
	private JButton addToExtractedButton;
	/* Button for deleting an email address from the mailing list. */
	private JButton delFromExtractedButton;
	/* Button for clearing extracted mails. */
	private JButton clearExtractedButton;
	
	/* Mailing list table and model. */
	private JTable mailListTable;
	private MailTableModel mailListTableModel;
	/* Removed list table and model. */
	private JTable removedTable;
	private MailTableModel removedTableModel;
	/* Returned list table and model. */
	private JTable returnedTable;
	private MailIntTableModel returnedTableModel;
	/* Extracted list table and model. */
	private JTable extractedTable;
	private MailTableModel extractedTableModel;
	
	/* Text area from where to extract emails. */
	private JTextArea textArea;
	
	/* Private class which manages address tables. */
	@SuppressWarnings("serial")
	private class MailTableModel extends AbstractTableModel {
		private String[] mails = {};
		private String header;
		public MailTableModel() {header = langProperties.getProperty("address");}
		public int getColumnCount() {return 1;}
		public int getRowCount() {return mails.length;}
		public String getColumnName(int col) {return header;}
		public Object getValueAt(int row, int col) {return mails[row];}
		public Class<?> getColumnClass(int c) {return String.class.getClass();}
		public void update(String[] updatedMails) {
			this.mails = updatedMails; 
			this.fireTableDataChanged();
		}
	}
	/* Private class which manages address tables and an integer. */
	@SuppressWarnings("serial")
	private class MailIntTableModel extends AbstractTableModel {
		private Object[][] mailCount = {{}};
		private String[] header;
		public MailIntTableModel() {
			header = new String[2];
			header[0] = langProperties.getProperty("address");
			header[1] = langProperties.getProperty("count");
		}
		public int getColumnCount() {return 2;}
		public int getRowCount() {return mailCount[0].length;}
		public String getColumnName(int col) {return header[col];}
		public Object getValueAt(int row, int col) {return mailCount[col][row];}
		public Class<?> getColumnClass(int c) {
			if (c == 0) return String.class.getClass();
			else return Integer.class.getClass();
		}
		public void update(Object[][] updatedMailCount) {
			this.mailCount = updatedMailCount;
			this.fireTableDataChanged();
		}
	}


	/**
	 * Constructor for this view.
	 * 
	 * @param controller The application controller.
	 * @param model The application model.
	 * @throws FileNotFoundException When the language file is not found.
	 * @throws IOException When there is an error opening the language file.
	 */
	public EMaMView(EMaMControllerInterface controller, EMaMModelInterface model) 
	throws FileNotFoundException, IOException {
		/* Keep references to controller and model. */
		this.controller = controller;
		this.model = model;
		/* Set language. */
		setLanguage();
		/* Initialize file filters. */
		emamFileFilter = 
				new FileNameExtensionFilter(
						langProperties.getProperty("eMaMFileDescription"),
						EMaM.getProperties().getProperty("eMaMFilenameExt"));
	}
	
	/**
	 * Creates and displays the view.
	 * In this case delegates the task of creating and displaying the GUI to a thread.
	 * 
	 * @see EMaMViewInterface#createView()
	 */
	public void createView() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGui();
			}
		});
	}
	
	/**
	 * @see FileStatusObserver#updateFileStatus()
	 */
	public void updateFileStatus() {
		if (frame != null) {
			String fileName;
			String asterisk = "";
			if (!model.isFileSaved())
				asterisk = "*";
			if (model.getCurrentFile() != null)
				fileName = model.getCurrentFile().getName();
			else
				fileName = langProperties.getProperty("new");
			frame.setTitle(asterisk + fileName + " - " + EMaM.getProperties().getProperty("AppTitle"));
		}
	}
		
	/**
	 * @see AddressTablesObserver#updateAddressTables()
	 */
	public void updateAddressTables() {
		mailListTableModel.update(model.getListedAddresses());
		/* Removed list table. */
		removedTableModel.update(model.getRemovedAddresses());
		/* Returned list table. */
		returnedTableModel.update(model.getReturnedAddresses());
		/* Extracted list table. */
		extractedTableModel.update(model.getExtractedAddresses());
	}

	/**
	 * @see EMaMViewInterface#dispose()
	 */
	public void dispose() {
		frame.dispose();
	}
	
	/**
	 * @see EMaMViewInterface#dialogOptions(String, String[], String, String[], String)
	 */
	public int dialogOptions(String message, String messageExtras[], String title, String options[], String defaultOption) {
		Object finalMessage[] = mergeMessageAndExtras(message, messageExtras);
		return JOptionPane.showOptionDialog(frame, 
				finalMessage, 
				title, 
				JOptionPane.DEFAULT_OPTION, 
				JOptionPane.INFORMATION_MESSAGE,
				null, 
				options, 
				defaultOption);
	}

	/**
	 * @see EMaMViewInterface#selectFileToOpen(int)
	 */
	public File selectFileToOpen(int fileType) {
		fceMaM.setCurrentDirectory(model.getCurrentFile());
		if (fileType == EMAM_FILES)
			fceMaM.setFileFilter(emamFileFilter);
		else
			fceMaM.setFileFilter(fceMaM.getAcceptAllFileFilter());
		if (fceMaM.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
			return fceMaM.getSelectedFile();
		else
			return null;
	}
	
	/**
	 * @see EMaMViewInterface#selectFileToSave(String)
	 */
	public File selectFileToSave(String filename) {
		File currentFile = model.getCurrentFile();
		if (currentFile != null)
			fceMaM.setCurrentDirectory(currentFile);
		fceMaM.setSelectedFile(new File(filename));
		if (fceMaM.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
			return fceMaM.getSelectedFile();
		else
			return null;
	}
	
	/**
	 * @see EMaMViewInterface#dialogInformation(String, String[], String)
	 */
	public void dialogInformation(String message, String messageExtras[], String title) {
		displayMessage(message, messageExtras, title, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * @see EMaMViewInterface#dialogWarning(String, String[], String)
	 */
	public void dialogWarning(String message, String messageExtras[], String title) {
		displayMessage(message, messageExtras, title, JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * @see EMaMViewInterface#dialogError(String, String[], String)
	 */
	public void dialogError(String message, String messageExtras[], String title) {
		displayMessage(message, messageExtras, title, JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * @see EMaMViewInterface#dialogInput(String, String[], String)
	 */
	public String dialogInput(String message, String messageExtras[], String title) {
		Object finalMessage[] = mergeMessageAndExtras(message, messageExtras);
		return
			JOptionPane.showInputDialog(frame, finalMessage, title, JOptionPane.QUESTION_MESSAGE);
	}
	
	/**
	 * This method handles all of the GUI events in this view.
	 * The effective actions are handled by the controller.
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == fileNewMenuItem) {
			controller.newFile();		
		} else if (event.getSource() == fileOpenMenuItem) {
			controller.openFile();		
		} else if (event.getSource() == fileSaveMenuItem) {
			controller.saveFile();
		} else if (event.getSource() == fileSaveAsMenuItem) {
			controller.saveAsFile();			
		} else if (event.getSource() == fileOptionsMenuItem) {
			controller.options();			
		} else if (event.getSource() == fileExitMenuItem) {
			controller.exit();
		} else if (event.getSource() == helpAboutMenuItem) {
			controller.about();			
		} else if (event.getSource() == addToMailListButton) {
			controller.addToMailList();						
		} else if (event.getSource() == delFromMailListButton) {
			controller.delFromMailList(getSelectedRows(mailListTable));		
		} else if (event.getSource() == copyMailListToClipboardButton) {
			controller.copyMailListToClipboard();	
		} else if (event.getSource() == moveFromMailToRemovedButton) {
			controller.moveFromMailListToRemoved(getSelectedRows(mailListTable));
		} else if (event.getSource() == moveFromRemovedToMailButton) {
			controller.moveFromRemovedToMailList(getSelectedRows(removedTable));
		} else if (event.getSource() == addToRemovedButton) {
			controller.addToRemoved();
		} else if (event.getSource() == delFromRemovedButton) {
			controller.delFromRemoved(getSelectedRows(removedTable));
		} else if (event.getSource() == copyRemovedToClipboardButton) {
			controller.copyRemovedToClipboard();
		} else if (event.getSource() == addToReturnedButton) {
			controller.addToReturned();
		} else if (event.getSource() == delFromReturnedButton) {
			controller.delFromReturned(getSelectedRows(returnedTable));
		} else if (event.getSource() == incReturnedButton) {
			int selectedRows[] = returnedTable.getSelectedRows();
			controller.incrementReturned(getSelectedRows(returnedTable));
			for (int i = 0; i < selectedRows.length; i++)
				returnedTable.addRowSelectionInterval(selectedRows[i], selectedRows[i]);
		} else if (event.getSource() == decReturnedButton) {
			int selectedRows[] = returnedTable.getSelectedRows();
			controller.decrementReturned(getSelectedRows(returnedTable));
			for (int i = 0; i < selectedRows.length; i++)
				returnedTable.addRowSelectionInterval(selectedRows[i], selectedRows[i]);
		} else if (event.getSource() == importFromFileButton) {
			controller.extractAddressesFromFile();
		} else if (event.getSource() == processReturnedButton) {
			controller.processReturned();
		} else if (event.getSource() == extractButton) {
			controller.extractAddresses(textArea.getText());
		} else if (event.getSource() == clearTextAreaButton) {
			textArea.setText("");
		} else if (event.getSource() == moveExtractedToMailListButton) {
			controller.moveExtractedToMailList();
		} else if (event.getSource() == moveExtractedToRemovedButton) {
			controller.moveExtractedToRemoved();
		} else if (event.getSource() == moveExtractedToReturnedButton) {
			controller.moveExtractedToReturned();
		} else if (event.getSource() == addToExtractedButton) {
			controller.addToExtracted();						
		} else if (event.getSource() == delFromExtractedButton) {
			controller.delFromExtracted(getSelectedRows(extractedTable));		
		} else if (event.getSource() == clearExtractedButton) {
			controller.clearExtracted();
		}
	}
	
	/**
	 * Mandatory method for implementing the {@link java.awt.datatransfer.ClipboardOwner}
	 * interface. Doesn't do anything in this context.
	 * 
	 * @see ClipboardOwner#lostOwnership(Clipboard, Transferable)
	 */
	public void lostOwnership(Clipboard arg0, Transferable arg1) {}
	
	/**
	 * @see EMaMViewInterface#copyToClipboard(String)
	 */
	public void copyToClipboard(String str) {
		StringSelection stringSelection = new StringSelection(str);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}

	/* ********************************************** */
	/*                PRIVATE METHODS                 */
	/* ********************************************** */

	/**
	 * Returns an array of strings containing the selected rows in a given 
	 * {@link javax.swing.JTable}.
	 * @param table Table where to get the selected rows from.
	 * @return An array of strings containing the selected rows in a given table.
	 */
	private String[] getSelectedRows(JTable table) {
		int selectedRows[] = table.getSelectedRows();
		String rows[] = new String[selectedRows.length];
		for (int i = 0; i < selectedRows.length; i++) {
			rows[i] = (String) table.getValueAt(selectedRows[i], 0);
		}
		return rows;
	}

	/**
	 * Create and show GUI.
	 */
	private void createAndShowGui() {
		/* Dispose of previously existing frame. */
		if (frame != null) {
			frame.dispose();
		}
		/* Create program window. */
		frame = new JFrame(EMaM.getProperties().getProperty("AppTitle"));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				controller.exit();
			}
		});
		/* Create file choosers. */
		fceMaM = new JFileChooser();
		/* Create menu bar. */
		JMenuBar menuBar = new JMenuBar();
		/* Create menus. */
		JMenu file = new JMenu(langProperties.getProperty("file"));
		JMenu help = new JMenu(langProperties.getProperty("help"));
		/* Add menus to menu bar. */
		menuBar.add(file);
		menuBar.add(help);
		/* Create menu items. */
		fileNewMenuItem = new JMenuItem(langProperties.getProperty("new"));
		fileNewMenuItem.addActionListener(this);
		fileOpenMenuItem = new JMenuItem(langProperties.getProperty("open"));
		fileOpenMenuItem.addActionListener(this);
		fileSaveMenuItem = new JMenuItem(langProperties.getProperty("save"));
		fileSaveMenuItem.addActionListener(this);
		fileSaveAsMenuItem = new JMenuItem(langProperties.getProperty("saveAs"));
		fileSaveAsMenuItem.addActionListener(this);
		fileOptionsMenuItem = new JMenuItem(langProperties.getProperty("options"));
		fileOptionsMenuItem.addActionListener(this);
		fileExitMenuItem = new JMenuItem(langProperties.getProperty("exit"));
		fileExitMenuItem.addActionListener(this);
		helpAboutMenuItem = new JMenuItem(langProperties.getProperty("about"));
		helpAboutMenuItem.addActionListener(this);
		/* Add menu items to menus. */
		file.add(fileNewMenuItem);
		file.add(fileOpenMenuItem);
		file.add(new JSeparator());
		file.add(fileSaveMenuItem);
		file.add(fileSaveAsMenuItem);
		file.add(new JSeparator());
		file.add(fileOptionsMenuItem);
		file.add(new JSeparator());
		file.add(fileExitMenuItem);
		help.add(helpAboutMenuItem);
		
		/* Create mail list panel. */
		JPanel mailListPanel = new JPanel();
		mailListPanel.setLayout(new BoxLayout(mailListPanel, BoxLayout.PAGE_AXIS));
		mailListPanel.add(new JLabel(langProperties.getProperty("mailListLabel"), JLabel.CENTER));
		mailListTableModel = new MailTableModel();
		mailListTable = new JTable(mailListTableModel);
		mailListPanel.add(new JScrollPane(mailListTable));
		JPanel mailListButtonsPanel = new JPanel();
		//mailListButtonsPanel.setLayout(new BoxLayout(mailListButtonsPanel, BoxLayout.LINE_AXIS));
		addToMailListButton = new JButton(langProperties.getProperty("addToMailListButton"));
		addToMailListButton.addActionListener(this);
		mailListButtonsPanel.add(addToMailListButton);
		delFromMailListButton = new JButton(langProperties.getProperty("delFromMailListButton"));
		delFromMailListButton.addActionListener(this);
		mailListButtonsPanel.add(delFromMailListButton);
		copyMailListToClipboardButton = new JButton(langProperties.getProperty("copyMailListToClipboardButton"));				
		copyMailListToClipboardButton.addActionListener(this);
		mailListButtonsPanel.add(copyMailListToClipboardButton);
		mailListPanel.add(mailListButtonsPanel);

		/* Create swap buttons panel. */
		JPanel swapButtonsPanel = new JPanel();
		swapButtonsPanel.setLayout(new BoxLayout(swapButtonsPanel, BoxLayout.PAGE_AXIS));
		moveFromMailToRemovedButton = new JButton(langProperties.getProperty("moveFromMailToRemovedButton"));
		moveFromMailToRemovedButton.addActionListener(this);
		moveFromRemovedToMailButton = new JButton(langProperties.getProperty("moveFromRemovedToMailButton"));
		moveFromRemovedToMailButton.addActionListener(this);
		swapButtonsPanel.add(moveFromMailToRemovedButton);
		swapButtonsPanel.add(moveFromRemovedToMailButton);
		
		/* Create removed list panel. */
		JPanel removedPanel = new JPanel();
		removedPanel.setLayout(new BoxLayout(removedPanel, BoxLayout.PAGE_AXIS));
		removedPanel.add(new JLabel(langProperties.getProperty("removedLabel"), JLabel.CENTER));
		removedTableModel = new MailTableModel();
		removedTable = new JTable(removedTableModel);
		removedPanel.add(new JScrollPane(removedTable));
		JPanel removedButtonsPanel = new JPanel();
		//removedButtonsPanel.setLayout(new BoxLayout(removedButtonsPanel, BoxLayout.LINE_AXIS));
		addToRemovedButton = new JButton(langProperties.getProperty("addToRemovedButton"));
		addToRemovedButton.addActionListener(this);
		removedButtonsPanel.add(addToRemovedButton);
		delFromRemovedButton = new JButton(langProperties.getProperty("delFromRemovedButton"));
		delFromRemovedButton.addActionListener(this);
		removedButtonsPanel.add(delFromRemovedButton);
		copyRemovedToClipboardButton = new JButton(langProperties.getProperty("copyRemovedToClipboardButton"));				
		copyRemovedToClipboardButton.addActionListener(this);
		removedButtonsPanel.add(copyRemovedToClipboardButton);
		removedPanel.add(removedButtonsPanel);

		/* Create returned panel. */
		JPanel returnedPanel = new JPanel();
		returnedPanel.setLayout(new BoxLayout(returnedPanel, BoxLayout.PAGE_AXIS));
		returnedPanel.add(new JLabel(langProperties.getProperty("returnedLabel"), JLabel.CENTER));
		returnedTableModel = new MailIntTableModel();
		returnedTable = new JTable(returnedTableModel);
		returnedPanel.add(new JScrollPane(returnedTable));
		JPanel returnedButtonsPanel = new JPanel();
		//returnedButtonsPanel.setLayout(new BoxLayout(returnedButtonsPanel, BoxLayout.LINE_AXIS));
		addToReturnedButton = new JButton(langProperties.getProperty("addToReturnedButton"));
		addToReturnedButton.addActionListener(this);
		returnedButtonsPanel.add(addToReturnedButton);
		delFromReturnedButton = new JButton(langProperties.getProperty("delFromReturnedButton"));
		delFromReturnedButton.addActionListener(this);
		returnedButtonsPanel.add(delFromReturnedButton);
		incReturnedButton = new JButton(langProperties.getProperty("incReturnedButton"));
		incReturnedButton.addActionListener(this);
		returnedButtonsPanel.add(incReturnedButton);
		decReturnedButton = new JButton(langProperties.getProperty("decReturnedButton"));
		decReturnedButton.addActionListener(this);
		returnedButtonsPanel.add(decReturnedButton);		
		processReturnedButton = new JButton(langProperties.getProperty("processReturnedButton"));
		processReturnedButton.addActionListener(this);
		returnedButtonsPanel.add(processReturnedButton);
		returnedPanel.add(returnedButtonsPanel);
		
		/* Create upper panel. */
		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.LINE_AXIS));
		/* Add mail, removed and returned list panels to upper panel. */
		upperPanel.add(mailListPanel);
		upperPanel.add(swapButtonsPanel);
		upperPanel.add(removedPanel);
		upperPanel.add(returnedPanel);
		
		/* Create text area panel. */
		JPanel textAreaPanel = new JPanel();
		textAreaPanel.setLayout(new BoxLayout(textAreaPanel, BoxLayout.PAGE_AXIS));
		textAreaPanel.add(new JLabel(langProperties.getProperty("textAreaLabel"), JLabel.CENTER));
		textArea = new JTextArea(20,50);
		JScrollPane scrollPane = new JScrollPane(textArea);
		textAreaPanel.add(scrollPane);
		JPanel textAreaButtonPanel = new JPanel();
		extractButton = new JButton(langProperties.getProperty("extractButton"));
		extractButton.addActionListener(this);
		textAreaButtonPanel.add(extractButton);
		clearTextAreaButton = new JButton(langProperties.getProperty("clearTextAreaButton"));
		clearTextAreaButton.addActionListener(this);
		textAreaButtonPanel.add(clearTextAreaButton);
		textAreaPanel.add(textAreaButtonPanel);
		
		/* Create extracted panel. */
		JPanel extractedPanel = new JPanel();
		extractedPanel.setLayout(new BoxLayout(extractedPanel, BoxLayout.PAGE_AXIS));
		extractedPanel.add(new JLabel(langProperties.getProperty("extractedLabel"), JLabel.CENTER));
		extractedTableModel = new MailTableModel();
		extractedTable = new JTable(extractedTableModel);
		extractedPanel.add(new JScrollPane(extractedTable));
		JPanel extractedButtonPanel1 = new JPanel();
		moveExtractedToMailListButton = new JButton(langProperties.getProperty("moveExtractedToMailListButton"));
		moveExtractedToMailListButton.addActionListener(this);
		extractedButtonPanel1.add(moveExtractedToMailListButton);
		moveExtractedToRemovedButton = new JButton(langProperties.getProperty("moveExtractedToRemovedButton"));
		moveExtractedToRemovedButton.addActionListener(this);
		extractedButtonPanel1.add(moveExtractedToRemovedButton);
		moveExtractedToReturnedButton = new JButton(langProperties.getProperty("moveExtractedToReturnedButton"));
		moveExtractedToReturnedButton.addActionListener(this);
		extractedButtonPanel1.add(moveExtractedToReturnedButton);
		extractedPanel.add(extractedButtonPanel1);
		JPanel extractedButtonPanel2 = new JPanel();
		importFromFileButton = new JButton(langProperties.getProperty("importButton"));
		importFromFileButton.addActionListener(this);
		extractedButtonPanel2.add(importFromFileButton);
		addToExtractedButton = new JButton(langProperties.getProperty("addToExtractedButton"));
		addToExtractedButton.addActionListener(this);
		extractedButtonPanel2.add(addToExtractedButton);
		delFromExtractedButton = new JButton(langProperties.getProperty("delFromExtractedButton"));
		delFromExtractedButton.addActionListener(this);
		extractedButtonPanel2.add(delFromExtractedButton);
		extractedPanel.add(extractedButtonPanel2);
		clearExtractedButton = new JButton(langProperties.getProperty("clearExtractedButton"));
		clearExtractedButton.addActionListener(this);
		extractedButtonPanel2.add(clearExtractedButton);
		
		/* Create lower panel. */
		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.LINE_AXIS));
		/* Add text area and extracted list panels to lower panel. */
		lowerPanel.add(textAreaPanel);
		lowerPanel.add(new JLabel(">>"));
		lowerPanel.add(extractedPanel);
		
		/* Create larger panel which will be added to the main frame. */
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		/* Add upper and lower panels to the larger panel. */
		panel.add(upperPanel);
		panel.add(lowerPanel);	
		
		/* Display the window. */
		frame.setJMenuBar(menuBar);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		/* After the GUI is built register the view as an observer for the model. */
		model.registerAddressTablesObserver(this);
		model.registerFileStatusObserver(this);
		
		/* Observe model for the first time. */
		updateAddressTables();
		updateFileStatus();
	}
	
	/**
	 * Sets the language of this view.
	 * 
	 * @throws FileNotFoundException When the language file is not found.
	 * @throws IOException When there is an error opening the language file.
	 */
	private void setLanguage() throws FileNotFoundException, IOException {
		String lang = EMaM.getProperties().getProperty("lang");
		String langFolder = EMaM.getProperties().getProperty("langFolder");
		langProperties = new Properties();
		langProperties.load(new FileReader(
				langFolder + File.separatorChar + lang + 
				File.separatorChar + this.getClass().getSimpleName() + ".properties"));
	}
	
	/**
	 * Displays a message to the user.
	 * 
	 * @param message Message to display.
	 * @param messageExtras Extras to display (will appear in a text area below the message).
	 * @param title Title of the message.
	 * @param messageType Type of message.
	 */
	private void displayMessage(String message, String messageExtras[], String title, int messageType) {
		Object finalMessage[] = mergeMessageAndExtras(message, messageExtras);
		JOptionPane.showMessageDialog(frame, finalMessage, title, messageType);	
	}

	/**
	 * Merge message and message extras in one single object to be displayed in a
	 * dialog box.
	 * 
	 * @param message Message to be presented to the user.
	 * @param messageExtras Message extras which will be displayed inside a text area.
	 * @return The object composed of the message and the message extras.
	 */
	private Object[] mergeMessageAndExtras(String message, String messageExtras[]) {
		Object[] finalMessage;
		if (messageExtras != null) {
			finalMessage = new Object[2];
			int rowSize = 0, colSize = 0;
			for (String extra : messageExtras) {
				rowSize += extra.split("\n").length;
				colSize = Math.max(colSize, extra.length());
			}
			colSize = Math.min(colSize, 60);
			rowSize += messageExtras.length;
			rowSize = Math.min(rowSize, 30);
			JTextArea jta = new JTextArea(rowSize, colSize);
			jta.setEditable(false);
			jta.setLineWrap(true);
			jta.setWrapStyleWord(true);
			for (int i = 0; i < messageExtras.length; i++) {
				jta.append(messageExtras[i] + "\n");
			}
			jta.setCaretPosition(0);
			finalMessage[1] = new JScrollPane(jta);
		} else {
			finalMessage = new Object[1];
		}
		finalMessage[0] = message;
		return finalMessage;
	}
	
}
