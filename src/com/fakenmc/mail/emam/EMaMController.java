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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.PatternSyntaxException;

/**
 * The reference implementation of the application controller.
 * The responsibilities of this class are described by the
 * MVC design pattern.
 * 
 * @author Nuno Fachada.
 *
 */
public class EMaMController implements EMaMControllerInterface {
	/* Constants. */
	private final int YES = 0;
	private final int NO = 1;
	private final int CANCEL = 2;
	private final int DEL_OK = 0;
	//private final int DEL_CANCEL = 1;

	/* Model and view references. */
	private EMaMModelInterface model;
	private EMaMView view;
	/* Language properties. */
	private Properties langProperties;

	/**
	 * Creates a new EMaM controller.
	 * 
	 * @param model A EMaM model for the controller to control.
	 * @throws FileNotFoundException @see java.io.FileNotFoundException.
	 * @throws IOException @see java.io.IOException
	 */
	public EMaMController(EMaMModelInterface model) 
	throws FileNotFoundException, IOException {
		/* Set model to control. */
		this.model = model;
		/* Set language. */
		setLanguage();
		/* Create view for this controller and respective model. */
		view = new EMaMView(this, model);
		view.createView();
		/* Obtain last used file. */
		String currentFile = EMaM.getProperties().getProperty("currentFile");
		if ((currentFile != null) && (currentFile.compareTo("") != 0)) {
			openFile(new File(currentFile));
		} else {
			newFile();
		}
	}
	
	/**
	 * @see EMaMControllerInterface#newFile()
	 */
	public void newFile() {
		/* If current file is not saved ask what to do.  */
		if (!model.isFileSaved()) {
			int answer = saveCurrentFileQuestion();
			if (answer == YES)
				/* Save file. */
				saveFile();
			else if (answer == CANCEL)
				/* Cancel open file and return to previous file. */
				return;
		}
		model.newFile();
	}
	
	/**
	 * @see EMaMControllerInterface#openFile()
	 */
	public void openFile() {
		/* If current file is not saved ask what to do.  */
		if (!model.isFileSaved()) {
			/* Ask of*/
			int answer = saveCurrentFileQuestion();
			if (answer == YES)
				/* Save file. */
				saveFile();
			else if (answer == CANCEL)
				/* Cancel open file and return to previous file. */
				return;
		} 
		/* Request view for a filename. */
		File fileToOpen = view.selectFileToOpen(EMaMViewInterface.EMAM_FILES);
		if (fileToOpen == null) {
			/* User canceled the request. */
			return;
		}
		/* Effectively open file. */
		openFile(fileToOpen);
	}
	
	/**
	 * @see EMaMControllerInterface#saveFile()
	 */
	public void saveFile() {
		if (model.getCurrentFile() == null) {
			saveAsFile();
		} else {
			try {
				model.saveFile();
			} catch (IOException ioe) {
				view.dialogWarning(
						langProperties.getProperty("IOWarning"), 
						new String[] {ioe.getLocalizedMessage()},
						langProperties.getProperty("WarningMessageTitle"));
				return;
			}
		}
	}

	/**
	 * @see EMaMControllerInterface#saveAsFile()
	 */
	public void saveAsFile() {
		File newFile;
		/* Required cycle: if file exists and user does not want to overwrite it
		 * then we must ask again for another filename. */
		do {
			/* Ask user for a filename. */
			if (model.getCurrentFile() == null)
				newFile = view.selectFileToSave(newFilename());
			else
				newFile = view.selectFileToSave(model.getCurrentFile().getName());
			if (newFile == null) return;
			if (newFile.exists()) {
				/* If given file exists, ask if user wants to overwrite it. */
				String options[] = {
						langProperties.getProperty("Yes"),
						langProperties.getProperty("No"),
						langProperties.getProperty("Cancel")};
				int answer = view.dialogOptions(
						langProperties.getProperty("overwriteFile"),
						null,
						EMaM.getProperties().getProperty("AppTitle"), 
						options, langProperties.getProperty("Yes"));
				if (answer == this.YES)
					/* Get out of do-while and overwrite file. */
					break;
				else if (answer == this.NO)
					/* Don't overwrite, ask for new filename. */
					continue;
				else if (answer == this.CANCEL)
					/* Cancel Save As. */
					return;
			} else {
				/* If given file does not exist, get out of do-while and save file. */
				break;
			}
		} while (true);
		try {
			/* Save file. */
			model.saveFileAs(newFile);
		} catch (IOException ioe) {
			/* Catch boring old IO exception, warn user... */
			view.dialogWarning(
					langProperties.getProperty("IOWarning"), 
					new String[] {ioe.getLocalizedMessage()},
					langProperties.getProperty("WarningMessageTitle"));
			/* ...and return to main application window. */
			return;
		}
	}

	/**
	 * @see EMaMControllerInterface#options()
	 */
	public void options() {
		//TODO
		view.dialogInformation("Soon...", null, "Options not yet");
	}

	
	/**
	 * @see EMaMControllerInterface#about()
	 */
	public void about() {
		String license[] = {EMaM.license};
		view.dialogInformation("eMaM v0.2 alpha (05 Dec 2008) by FakenMC.com\nLicense:", license, langProperties.getProperty("about"));
	}

	
	/**
	 * @see EMaMControllerInterface#exit()
	 */
	public void exit() {
		if (!model.isFileSaved()) {
			int answer = saveCurrentFileQuestion();
			if (answer == YES)
				/* Save file. */
				saveFile();
			else if (answer == CANCEL)
				/* Cancel open file and return to previous file. */
				return;
		}
		if (model.getCurrentFile() != null)
			EMaM.getProperties().setProperty("currentFile", model.getCurrentFile().getAbsolutePath());
		else
			EMaM.getProperties().setProperty("currentFile", "");
		view.dispose();
		EMaM.saveProperties();
		System.exit(0);
	}

	/**
	 * @see EMaMControllerInterface#addToMailList()
	 */
	public void addToMailList() {
		/* Request user to insert an address. */
		String address = requestRegExp(
				langProperties.getProperty("RequestAddress"),
				EMaM.getProperties().getProperty("regexp"),
				langProperties.getProperty("InvalidAddress"));
		/* If user inserted a valid address and didn't press CANCEL then... */
		if (address != null) {
			/* ...add address to mailing list. */
			String[] addresses = {address};
			try {
				model.addToMailList(addresses);
			} catch (MutualExclusionException mee) {
				/* The address exists in the removed list. Ask if user wants to 
				 * move address from removed list to mail list. */
				if (confirmAddressesOp(mee.getAddresses(), 
						langProperties.getProperty("mutualExclusionMailList"))) {
					model.moveFromRemovedToMailList(mee.getAddresses());
				}
			}
		}
	}
	
	/**
	 * @see EMaMControllerInterface#delFromMailList(String[])
	 */
	public void delFromMailList(String[] addresses) {
		if (confirmSelection(addresses))
			if (confirmAddressesOp(addresses, langProperties.getProperty("delFrom")))
				model.delFromMailList(addresses);
	}

	
	/**
	 * @see EMaMControllerInterface#copyMailListToClipboard()
	 */
	public void copyMailListToClipboard() {
		copyToClipboard(model.getListedAddresses());
	}

	
	/**
	 * @see EMaMControllerInterface#moveFromMailListToRemoved(String[])
	 */
	public void moveFromMailListToRemoved(String[] addresses) {
		if (confirmSelection(addresses)) {
			if (confirmAddressesOp(addresses, langProperties.getProperty("moveFromTo"))) {
				model.moveFromMailListToRemoved(addresses);
			}
		}
	}

	
	/**
	 * @see EMaMControllerInterface#moveFromRemovedToMailList(String[])
	 */
	public void moveFromRemovedToMailList(String[] addresses) {
		if (confirmSelection(addresses)) {
			if (confirmAddressesOp(addresses, langProperties.getProperty("moveFromTo"))) {
				model.moveFromRemovedToMailList(addresses);
			}
		}
	}

	
	/**
	 * @see EMaMControllerInterface#addToRemoved()
	 */
	public void addToRemoved() {
		/* Request user to insert an address. */
		String address = requestRegExp(
				langProperties.getProperty("RequestAddress"),
				EMaM.getProperties().getProperty("regexp"),
				langProperties.getProperty("InvalidAddress"));
		/* If user inserted a valid address and didn't press CANCEL then... */
		if (address != null) {
			/* ...add address to removed list. */
			String[] addresses = {address};
			try {
				model.addToRemoved(addresses);
			} catch (MutualExclusionException mee) {
				/* The address exists in the mail list. Ask if user wants to 
				 * move address from mail list to removed list. */
				if (confirmAddressesOp(mee.getAddresses(), 
						langProperties.getProperty("mutualExclusionRemoved"))) {
					model.moveFromMailListToRemoved(mee.getAddresses());
				}
			}
		}
	}

	
	/**
	 * @see EMaMControllerInterface#delFromRemoved(String[])
	 */
	public void delFromRemoved(String[] addresses) {
		if (confirmSelection(addresses))
			if (confirmAddressesOp(addresses, langProperties.getProperty("delFrom")))
				model.delFromRemoved(addresses);
	}

	
	/**
	 * @see EMaMControllerInterface#copyRemovedToClipboard()
	 */
	public void copyRemovedToClipboard() {
		copyToClipboard(model.getRemovedAddresses());
	}

	
	/**
	 * @see EMaMControllerInterface#addToReturned()
	 */
	public void addToReturned() {
		/* Request user to insert an address. */
		String address = requestRegExp(
				langProperties.getProperty("RequestAddress"),
				EMaM.getProperties().getProperty("regexp"),
				langProperties.getProperty("InvalidAddress"));
		/* If user inserted a valid address and didn't press CANCEL then... */
		if (address != null) {
			/* ...add address to returned list. */
			String[] addresses = {address};
			model.addToReturned(addresses);
		}		
	}

	
	/**
	 * @see EMaMControllerInterface#delFromReturned(String[])
	 */
	public void delFromReturned(String[] addresses) {
		if (confirmSelection(addresses))
			if (confirmAddressesOp(addresses, langProperties.getProperty("delFrom")))
				model.delFromReturned(addresses);
		
	}

	
	/**
	 * @see EMaMControllerInterface#incrementReturned(String[])
	 */
	public void incrementReturned(String[] addresses) {
		if (confirmSelection(addresses))
			model.incrementReturned(addresses);
	}

	
	/**
	 * @see EMaMControllerInterface#decrementReturned(String[])
	 */
	public void decrementReturned(String[] addresses) {
		if (confirmSelection(addresses))
			model.decrementReturned(addresses);
	}

	
	/**
	 * @see EMaMControllerInterface#processReturned()
	 */
	public void processReturned() {
		int value;
		/* If user inserted a value bigger than 0... */
		if ((value = requestInteger()) > 0)
			/* Move addresses... */
			model.processReturned(value);
	}

	
	/**
	 * @see EMaMControllerInterface#extractAddressesFromFile()
	 */
	public void extractAddressesFromFile() {
		/* Request file from user using the view. */
		File file = view.selectFileToOpen(EMaMViewInterface.ALL_FILES);
		if (file == null) {
			/* The user canceled the request. */
			return;
		}
		/* Read file. */
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			/* The selected file is not found.  */
			view.dialogWarning(
					langProperties.getProperty("FileNotFoundWarning"),
					new String[] {e.getLocalizedMessage()},
					langProperties.getProperty("WarningMessageTitle"));
			model.newFile();
			return;
		}
		String line;
		StringBuilder text = new StringBuilder();
		try {
			while ((line = br.readLine()) != null)
				text.append(line + " ");
		} catch (IOException e) {
			/* IO problem. */
			view.dialogWarning(
					langProperties.getProperty("IOWarning"),
					new String[] {e.getLocalizedMessage()},
					langProperties.getProperty("WarningMessageTitle"));
			model.newFile();
			return;
		}
		extractAddresses(text.toString());
	}

	/**
	 * @see EMaMControllerInterface#extractAddresses(String)
	 */
	public void extractAddresses(String text) {
		if (model.getExtractedAddresses().length > 0) {
			if (confirmAddressesOp(model.getExtractedAddresses(), langProperties.getProperty("delFrom")))
				return;
		}
		model.extractAddresses(text);
	}

	/**
	 * @see EMaMControllerInterface#moveExtractedToMailList()
	 */	
	public void moveExtractedToMailList() {
		/* Ask user to confirm operation with given addresses. */
		if (confirmAddressesOp(
				model.getExtractedAddresses(), 
				langProperties.getProperty("moveFromTo"))) {
			try {
				/* Move extracted addresses to mail list. */
				model.moveExtractedToMailList();
			} catch (MutualExclusionException mee) {
				/* In case some of the addresses exist in the removed list, ask user
				 * what to do. */
				String options[] = {
						langProperties.getProperty("Yes"),
						langProperties.getProperty("No"),
						langProperties.getProperty("Cancel")
				};
				int op = view.dialogOptions(
						langProperties.getProperty("mutualExclusionOptionMailList"), 
						mee.getAddresses(), 
						langProperties.getProperty("QuestionMessageTitle"), 
						options, 
						langProperties.getProperty("No"));
				if (op == this.YES) {
					/* In this case, move mails which exist the removed list to the
					 * mailing list. */
					model.moveFromRemovedToMailList(mee.getAddresses());
				} else if (op == this.NO) {
					/* In this case, move only addresses which do not exist in the
					 * Removed List, i.e., remove said addresses from extracted list. */
					model.delFromExtracted(mee.getAddresses());
				} else if (op == this.CANCEL) {
					/* In this case do nothing.*/
					return;
				}
				/* If we get here its because the user answered yes or no. */
				try {
					model.moveExtractedToMailList();				
				} catch (MutualExclusionException mee2) {
					/* This should not occur. If it does, it's a critical error. */
					EMaM.errorHandler(
							langProperties.getProperty("mutualExclusionErrorMessage"), 
							EMaM.MUTUAL_EXCLUSION_ERROR);
					view.dispose();
				}				
			}
		}
	}

	
	/**
	 * @see EMaMControllerInterface#moveExtractedToRemoved()
	 */
	public void moveExtractedToRemoved() {
		/* Ask user to confirm operation with given addresses. */
		if (confirmAddressesOp(
				model.getExtractedAddresses(), 
				langProperties.getProperty("moveFromTo"))) {
			try {
				/* Move extracted addresses to removed list. */
				model.moveExtractedToRemoved();
			} catch (MutualExclusionException mee) {
				/* In case some of the addresses exist in the mailing list, ask user
				 * what to do. */
				String options[] = {
						langProperties.getProperty("Yes"),
						langProperties.getProperty("No"),
						langProperties.getProperty("Cancel")
				};
				int op = view.dialogOptions(
						langProperties.getProperty("mutualExclusionOptionRemoved"), 
						mee.getAddresses(), 
						langProperties.getProperty("QuestionMessageTitle"), 
						options, 
						langProperties.getProperty("No"));
				if (op == this.YES) {
					/* In this case, move mails which exist in the mailing list to the
					 * removed list. */
					model.moveFromMailListToRemoved(mee.getAddresses());
				} else if (op == this.NO) {
					/* In this case, move only addresses which do not exist in the
					 * Mailing List, i.e., remove said addresses from extracted list. */
					model.delFromExtracted(mee.getAddresses());
				} else if (op == this.CANCEL) {
					/* In this case do nothing.*/
					return;
				}
				/* If we get here its because the user answered yes or no. */
				try {
					model.moveExtractedToRemoved();
				} catch (MutualExclusionException mee2) {
					/* This should not occur. If it does, it's a critical error. */
					EMaM.errorHandler(
							langProperties.getProperty("mutualExclusionErrorMessage"), 
							EMaM.MUTUAL_EXCLUSION_ERROR);
					view.dispose();
				}				
			}
		}
	}

	
	/**
	 * @see EMaMControllerInterface#moveExtractedToReturned()
	 */
	public void moveExtractedToReturned() {
		if (confirmAddressesOp(model.getExtractedAddresses(), langProperties.getProperty("moveFromTo"))) {
			model.moveExtractedToReturned();
		}
	}

	/**
	 * @see EMaMControllerInterface#addToExtracted()
	 */
	public void addToExtracted() {
		/* Request user to insert an address. */
		String address = requestRegExp(
				langProperties.getProperty("RequestAddress"),
				EMaM.getProperties().getProperty("regexp"),
				langProperties.getProperty("InvalidAddress"));
		/* If user inserted a valid address and didn't press CANCEL then... */
		if (address != null) {
			/* ...add address to extracted list. */
			String[] addresses = {address};
			model.addToExtracted(addresses);
		}
	}
	
	/**
	 * @see EMaMControllerInterface#delFromExtracted(String[])
	 */
	public void delFromExtracted(String[] addresses) {
		if (confirmSelection(addresses))
			if (confirmAddressesOp(addresses, langProperties.getProperty("delFrom")))
				model.delFromExtracted(addresses);
	}

	/**
	 * @see EMaMControllerInterface#clearExtracted()
	 */
	public void clearExtracted() {
		if (confirmAddressesOp(model.getExtractedAddresses(), langProperties.getProperty("delFrom")))
				model.clearExtracted();
	}
	
	/* ********************************************** */
	/*                PRIVATE METHODS                 */
	/* ********************************************** */

	/**
	 * Sets the language for this controller.
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
	 * Copy an array of addresses to the system clipboard, requesting the user
	 * to insert a separator character.
	 * 
	 * @param addresses Array of addresses to be copied to the clipboard.
	 */
	private void copyToClipboard(String[] addresses) {
		String separator;
		/* Ask the user an address separator. */
		separator = requestRegExp(
				langProperties.getProperty("RequestSeparatorString"),
				".*",
				langProperties.getProperty("InvalidSeparatorString"));
		if (separator != null) {
			/* Copy addresses separated by a separator to the system clipboard. */
			StringBuilder toCopy = new StringBuilder();
			for (String address : addresses) {
				toCopy.append(address);
				toCopy.append(separator);
			}
			view.copyToClipboard(toCopy.toString());
		}		
	}

	/**
	 * In case given string array has no content, inform the user and return false.
	 * Otherwise return true.
	 * 
	 * @param addresses String array to check for content.
	 * @return True if array has content, false otherwise.
	 */
	private boolean confirmSelection(String[] addresses) {
		/* The must be some addresses to remove. */
		if (addresses.length < 1) {
			/* No addresses were selected, inform user. */
			view.dialogWarning(
					langProperties.getProperty("noSelection"),
					null,
					langProperties.getProperty("WarningMessageTitle"));
			return false;
		}
		return true;
	}

	/**
	 * Ask user to confirm operation on an array of addresses.
	 *  
	 * @param addresses Array of addresses.
	 * @param message Message to display to the user.
	 * @return True if user agrees, false otherwise.
	 */
	private boolean confirmAddressesOp(String[] addresses, String message) {
		/* Set options. */
		String options[] = {
				langProperties.getProperty("Ok"),
				langProperties.getProperty("Cancel")};
		/* Determine if the operation concerns one or more addresses. */
		String addressNum;
		if (addresses.length == 1)
			addressNum = langProperties.getProperty("address");
		else
			addressNum = langProperties.getProperty("addresses");
		/* Ask user if he really wants to delete given addresses. */
		int answer = 
			view.dialogOptions(
					message + "\n" 
					+ "(" + langProperties.getProperty("total") + ": "
					+ addresses.length + " " + addressNum + ")",
					addresses,
					EMaM.getProperties().getProperty("AppTitle"), 
					options, 
					langProperties.getProperty("Ok"));
		if (answer == DEL_OK)
			return true;
		return false;
	}
	
	/**
	 * Ask the user to insert a string according to a specific regular expression.
	 * 
	 * @param message Message to show to the user.
	 * @param regexp Regular expression to match user inserted string.
	 * @param warningMessage Warning message if inserted string is not in accordance with regular expression.
	 * @return A user string in accordance with the given regular expression.
	 */
	private String requestRegExp(String message, String regexp, String warningMessage) {
		String input = null;
		boolean trim = false;
		boolean validInput = false;
		/* Keep asking the user for input until a valid expression is given, or CANCEL is pressed. */
		do {
			/* Ask for user input. */
			input = view.dialogInput(
				message,
				null,
				langProperties.getProperty("InputMessageTitle"));
			/* If user didn't press CANCEL, proceed..*/
			if (input != null) {
				/* Check if user inserted valid expression. */
				if (input.matches(regexp)) {
					/* Valid address! */
					validInput = true;
				} else if (input.trim().matches(regexp)) {
					/* Valid address! */
					validInput = true;
					trim = true;
				} else {
					view.dialogWarning(
							warningMessage,
							null,
							langProperties.getProperty("WarningMessageTitle"));
				}
			} else {
				/* The user pressed cancel, let's get out of here. */
				break;
			}
		} while (!validInput);
		if (trim)
			return input.trim();
		else
			return input;
	}
	
	/**
	 * Ask the user to insert an integer value.
	 * 
	 * @return A user inserted integer value.
	 */
	private int requestInteger() {
		/* Keep asking the user for input until a valid address is given, or CANCEL is pressed. */
		do {
			/* Ask for user input. */
			String input;
			input = view.dialogInput(
				langProperties.getProperty("RequestMinimumProcessValue"),
				null,
				langProperties.getProperty("InputMessageTitle"));
			/* If user didn't press CANCEL, proceed..*/
			if (input != null) {
				/* Check if user inserted valid address. */
				int value;
				try {
					value = Integer.parseInt(input);
					if (value < 1) 
						throw new NumberFormatException();
				} catch (NumberFormatException nfe) {
					/* Not a valid integer. */
					view.dialogWarning(
							langProperties.getProperty("InvalidMinimumProcessValue"),
							null,
							langProperties.getProperty("WarningMessageTitle"));
					/* Ask again. */
					continue;
				}
				return value;
			} else {
				/* The user pressed cancel, let's get out of here. */
				return -1;
			}
		} while (true);
	}
	
	/**
	 * Effectively ask the model to open a given file and handle
	 * possible errors.
	 * 
	 * @param fileToOpen File to open.
	 */
	private void openFile(File fileToOpen) {
		/* Open new file. */
		try {
			model.openFile(fileToOpen);
		} catch (PatternSyntaxException e) {
			/* The expression given in the properties file is not a valid
			 * regular expression. */
			EMaM.errorHandler(
					langProperties.getProperty("PatternSyntaxException"), 
					EMaM.REGEXP_ERROR);
			view.dispose();
		} catch (FileNotFoundException e) {
			/* The selected file is not found.  */
			view.dialogWarning(
					langProperties.getProperty("FileNotFoundWarning"),
					new String[] {e.getLocalizedMessage()},
					langProperties.getProperty("WarningMessageTitle"));
			model.newFile();
		} catch (IOException e) {
			/* IO problem. */
			view.dialogWarning(
					langProperties.getProperty("IOWarning"),
					new String[] {e.getLocalizedMessage()},
					langProperties.getProperty("WarningMessageTitle"));
			model.newFile();
		} catch (InvalidStringException e) {
			/* A line of the file does not conform to the address regular expression. */
			view.dialogWarning(
					langProperties.getProperty("InvalidStringWarning"), 
					new String[] {e.getLocalizedMessage()},
					langProperties.getProperty("WarningMessageTitle"));
			model.newFile();
		} catch (InvalidFileFormatException e) {
			/* The file does not conform to the norm. */
			view.dialogWarning(
					langProperties.getProperty("InvalidFileFormatException"), 
					new String[] {e.getLocalizedMessage()},
					langProperties.getProperty("WarningMessageTitle"));
			model.newFile();
		}		
	}
	
	/**
	 * Ask the user if he wants to save current file.
	 * 
	 * @return YES, NO or CANCEL constants.
	 */
	private int saveCurrentFileQuestion() {
		String options[] = {
				langProperties.getProperty("Yes"),
				langProperties.getProperty("No"),
				langProperties.getProperty("Cancel")};
		int answer = view.dialogOptions(
				langProperties.getProperty("fileNotSavedMessage"),
				null,
				EMaM.getProperties().getProperty("AppTitle"), 
				options, langProperties.getProperty("Yes"));
		return answer;
	}
	
	/**
	 * Returns a default filename for new files.
	 * 
	 * @return A default filename for new files.
	 */
	private String newFilename() {
		return langProperties.getProperty("newEMaMFilename")
			+ "." +
			EMaM.getProperties().getProperty("eMaMFilenameExt");
	}	
//	/**
//	 * Forces deletion from removed list and ad
//	 * @param toDelFromRemoved
//	 * @param toAddToMailList
//	 */
//	private void forceDelFromRemovedAndAddToMailList(
//			String[] toDelFromRemoved, 
//			String[] toAddToMailList) {
//		if (confirmAddressesOp(toDelFromRemoved, langProperties.getProperty("mutualExclusionMailList"))) {
//			//model.
//			//model.delFromRemoved(toDelFromRemoved);
//			try {
//				model.addToMailList(toAddToMailList);
//			} catch (MutualExclusionException mee) {
//				view.dialogError(
//						langProperties.getProperty("mutualExclusionErrorMessage"), 
//						mee.getAddresses(), 
//						langProperties.getProperty("ErrorMessageTitle"));
//			}
//		}
//	}
//
//	private void forceDelFromMailListAndAddToRemoved(
//			String[] toDelFromMailList, 
//			String[] toAddToRemoved) {
//		if (confirmAddressesOp(toDelFromMailList, langProperties.getProperty("mutualExclusionRemoved"))) {
//			//moveFrom
//			//			model.delFromMailList(toDelFromMailList);
////			try {
////				model.addToRemoved(toAddToRemoved);
////			} catch (MutualExclusionException mee) {
////				view.dialogError(
////						langProperties.getProperty("mutualExclusionErrorMessage"), 
////						mee.getAddresses(), 
////						langProperties.getProperty("ErrorMessageTitle"));
////			}
//		}
//	}
}
