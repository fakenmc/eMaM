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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.Arrays;
import java.util.Iterator;

/**
 * The eMaM model.
 * 
 * @author Nuno Fachada
 *
 */
public class EMaMModel implements EMaMModelInterface {
	/* Constants. */
	final String MAIL = "[Mail]";
	final String REMOVED = "[Removed]";
	final String RETURNED = "[Returned]";
	final String EXTRACTED = "[Extracted]";
	/* Observers. */
	private HashSet<AddressTablesObserver> atObservers;
	private HashSet<FileStatusObserver> fsObservers;
	/* Current working file. */
	private File currentFile;
	/* Flag indicating is current file is saved. */
	private boolean saved;
	/* Email data. */
	private TreeSet<String> mailList;
	private TreeSet<String> removed;
	private TreeMap<String, Integer> returned;
	private TreeSet<String> extracted;
	
	/**
	 * Constructor for the model. Initializes lists and variables.
	 */
	public EMaMModel() {
		mailList = new TreeSet<String>();
		removed = new TreeSet<String>();
		returned = new TreeMap<String, Integer>();
		extracted = new TreeSet<String>();
		atObservers = new HashSet<AddressTablesObserver>();
		fsObservers = new HashSet<FileStatusObserver>();
		currentFile = null;
		saved = true;
	}
	
	/**
	 * @see EMaMModelInterface#newFile()
	 */
	public void newFile() {
		clearData();
		currentFile = null;
		saved = true;
		notifyAddressTablesObservers();
		notifyFileStatusObservers();
	}
	
	/**
	 * @see EMaMModelInterface#openFile(File)
	 */
	public void openFile(File file) 
	throws FileNotFoundException, IOException, PatternSyntaxException,
	InvalidStringException, InvalidFileFormatException {
		/* Clear all previous listings. */
		clearData();
		/* Open file. */
		BufferedReader br = new BufferedReader(
				new FileReader(file));
		String line;
		String type = null;
		/* Read file line by line. */
		for (int i = 1; (line = br.readLine()) != null; i++) {
			/* Trim line. */
			line = line.trim();
			/* Check line. */
			if (line.compareTo(this.MAIL) == 0) {
				type = this.MAIL;
			} else if (line.compareTo(this.REMOVED) == 0) {
				type = this.REMOVED;
			} else if (line.compareTo(this.RETURNED) == 0) {
				type = this.RETURNED;
			} else if (line.compareTo(this.EXTRACTED) == 0) {
				type = this.EXTRACTED;
			} else if (type == null) {
				throw new InvalidFileFormatException("(1) ?");
			} else {
				line = line.toLowerCase();
				if (type != this.RETURNED) {
					/* Parse email address. */
					if (!line.matches(EMaM.getProperties().getProperty("regexp")))
						throw new InvalidStringException("("+ i +") " + line);
					if (type == this.MAIL) {
						/* Add email to mailing list set. */
						mailList.add(line);
					} else if (type == this.REMOVED) {
						/* Add email to removed list set. */
						removed.add(line);
					} else if (type == this.EXTRACTED) {
						/* Add email to extracted list set. */
						extracted.add(line);
					}
				} else {
					/* Parse address and number, add to returned map. */
					String splitLine[] = line.split(" ");
					/* Line must have two things: an email address, and an integer value. */
					if (splitLine.length != 2)
						throw new InvalidFileFormatException("("+ i +") " + line);
					/* Check if address is valid. */
					if (!splitLine[0].matches(EMaM.getProperties().getProperty("regexp")))
						throw new InvalidStringException("("+ i +") " + line);
					/* Check if integer is valid. */
					try {
						Integer.valueOf(splitLine[1]);
					} catch (NumberFormatException nfe) {
						throw new InvalidFileFormatException("("+ i +") " + line);
					}
					/* Everything OK, lets add to the map. */
					if (returned.containsKey(splitLine[0]))
						returned.put(splitLine[0], returned.get(splitLine[0]) + Integer.valueOf(splitLine[1]));
					else
						returned.put(splitLine[0], Integer.valueOf(splitLine[1]));
				}
			}
		}
		/* Close reader. */
		br.close();
		/* Make the given file the current file. */
		currentFile = file;
		/* Set saved file flag to true. */
		saved = true;
		/* Notify observers. */
		notifyAddressTablesObservers();
		notifyFileStatusObservers();
	}
	
	/**
	 * @see EMaMModelInterface#saveFile()
	 */
	public void saveFile() throws IOException {
		/* Create a writer. */
		BufferedWriter bw = new BufferedWriter(
				new FileWriter(currentFile));
		/* Write everything to the writer. */
		String allAddressListsTypes[] = {this.MAIL, this.REMOVED, this.RETURNED, this.EXTRACTED};
		for (String addressListType : allAddressListsTypes) {
			/* Write header indicating address list. */
			bw.write(addressListType);
			bw.newLine();
			/* Select address list to write. */
			Iterator<String> addressIter = null;
			if (addressListType == this.MAIL)
				addressIter = mailList.iterator();
			else if (addressListType == this.REMOVED)
				addressIter = removed.iterator();
			else if (addressListType == this.RETURNED)
				addressIter = returned.keySet().iterator();
			else if (addressListType == this.EXTRACTED)
				addressIter = extracted.iterator();
			/* Write addresses. */
			while (addressIter.hasNext()) {
				String address = addressIter.next();
				bw.write(address);
				if (addressListType == this.RETURNED)
					/* In the case of the RETURNED list, we also have to write the 
					 * number of times the mail was returned. */
					bw.write(" " + returned.get(address));
				bw.newLine();
			}
		}
		/* Close writer. */
		bw.close();
		/* Save and notify file status observers. */
		saveAndNotifyFileStatusObservers();
	}

	/**
	 * @see EMaMModelInterface#saveFileAs(File)
	 */
	public void saveFileAs(File file) throws IOException {
		currentFile = file;
		saveFile();
		notifyFileStatusObservers();
	}

	/**
	 * @see EMaMModelInterface#addToMailList(String[])
	 */
	public void addToMailList(String[] addresses) throws MutualExclusionException {
		/* To lower case. */
		toLowerCase(addresses);
		/* Check if any of the given addresses exists on the removed set. */
		checkExclusiveContents(addresses, removed);
		/* Add addresses to mail list set. */
		addToSet(addresses, mailList);
		notifyAddressTablesObservers();
		unsaveAndNotifyFileStatusObservers();
	}
	
	/**
	 * @see EMaMModelInterface#delFromMailList(String[])
	 */
	public void delFromMailList(String[] addresses) {
		/* To lower case. */
		toLowerCase(addresses);
		delFromSet(addresses, mailList);
		notifyAddressTablesObservers();
		unsaveAndNotifyFileStatusObservers();
	}

	/**
	 * @see EMaMModelInterface#moveFromMailListToRemoved(String[])
	 */
	public void moveFromMailListToRemoved(String[] addresses) {
		/* To lower case. */
		toLowerCase(addresses);
		delFromSet(addresses, mailList);
		addToSet(addresses, removed);
		notifyAddressTablesObservers();
		unsaveAndNotifyFileStatusObservers();
	}

	/**
	 * @see EMaMModelInterface#addToRemoved(String[])
	 */
	public void addToRemoved(String[] addresses) throws MutualExclusionException {
		/* To lower case. */
		toLowerCase(addresses);
		/* Check if any of the given addresses exists on the mail list set. */
		checkExclusiveContents(addresses, mailList);
		/* Add addresses to removed set. */
		addToSet(addresses, removed);
		notifyAddressTablesObservers();
		unsaveAndNotifyFileStatusObservers();
	}
	
	/**
	 * @see EMaMModelInterface#delFromRemoved(String[])
	 */
	public void delFromRemoved(String[] addresses) {
		/* To lower case. */
		toLowerCase(addresses);
		delFromSet(addresses, removed);
		notifyAddressTablesObservers();
		unsaveAndNotifyFileStatusObservers();
	}

	/**
	 * @see EMaMModelInterface#addToReturned(String[])
	 */
	public void addToReturned(String[] addresses) {
		/* To lower case. */
		toLowerCase(addresses);
		for (String address : addresses) {
			if (returned.containsKey(address)) {
				int currentN = returned.get(address);
				currentN++;
				returned.put(address, currentN);				
			} else {
				returned.put(address, 0);
			}
		}
		notifyAddressTablesObservers();
		unsaveAndNotifyFileStatusObservers();
	}
	
	/**
	 * @see EMaMModelInterface#delFromReturned(String[])
	 */
	public void delFromReturned(String[] addresses) {
		/* To lower case. */
		toLowerCase(addresses);
		delFromSet(addresses, returned.keySet());
		notifyAddressTablesObservers();
		unsaveAndNotifyFileStatusObservers();
	}
	
	/**
	 * @see EMaMModelInterface#moveFromRemovedToMailList(String[])
	 */
	public void moveFromRemovedToMailList(String[] addresses) {
		/* To lower case. */
		toLowerCase(addresses);
		delFromSet(addresses, removed);
		addToSet(addresses, mailList);
		notifyAddressTablesObservers();
		unsaveAndNotifyFileStatusObservers();
	}

	/**
	 * @see EMaMModelInterface#incrementReturned(String[])
	 */
	public void incrementReturned(String[] addresses) {
		/* To lower case. */
		toLowerCase(addresses);
		for (String address : addresses) {
			if (returned.containsKey(address)) {
				int currentN = returned.get(address);
				currentN++;
				returned.put(address, currentN);				
			} else {
				returned.put(address, 1);
			}
		}
		notifyAddressTablesObservers();
		unsaveAndNotifyFileStatusObservers();
	}
	
	/**
	 * @see EMaMModelInterface#decrementReturned(String[])
	 */
	public void decrementReturned(String[] addresses) {
		/* To lower case. */
		toLowerCase(addresses);
		for (String address : addresses) {
			int currentN = returned.get(address);
			currentN--;
			if (currentN >= 0) {
				returned.put(address, currentN);
			}
		}
		notifyAddressTablesObservers();
		unsaveAndNotifyFileStatusObservers();
	}
	
	/**
	 * @see EMaMModelInterface#processReturned(int)
	 */
	public void processReturned(int n) {
		/* Create a list of addresses to move from returned to removed. */
		ArrayList<String> addressesToMove = new ArrayList<String>();
		Iterator<String> iterAddress = returned.keySet().iterator();
		while (iterAddress.hasNext()) {
			String address = iterAddress.next();
			int currentN = returned.get(address);
			if (currentN > n)
				addressesToMove.add(address);
		}
		/* Move addresses... */
		if (addressesToMove.size() > 0) {
			String[] addressesToMoveArray = new String[addressesToMove.size()];
			addressesToMoveArray = addressesToMove.toArray(addressesToMoveArray);
			delFromSet(addressesToMoveArray, mailList);
			delFromSet(addressesToMoveArray, returned.keySet());
			addToSet(addressesToMoveArray, removed);
			/* Notify observers. */
			notifyAddressTablesObservers();
			unsaveAndNotifyFileStatusObservers();
		}
	}
	
	/**
	 * @see EMaMModelInterface#extractAddresses(String)
	 */
	public int extractAddresses(String text) {
		/* Create a list where to keep extracted addresses. */
		ArrayList<String> addressesExtracted = new ArrayList<String>();
		/* Create a pattern to extract from the regular expression in 
		 * the properties file. */
		Pattern p = Pattern.compile(EMaM.getProperties().getProperty("regexp"));
		/* Extract addresses. */
		Matcher m = p.matcher(text);
		while (m.find()) {
			String address = m.group();
			addressesExtracted.add(address);
		}
		/* Clear previoulsy extracted addresses. */
		extracted.clear();
		/* Populate with newly extracted addresses. */
		addToExtracted(addressesExtracted.toArray(new String[addressesExtracted.size()]));
		/* Notify observers. */
		notifyAddressTablesObservers();
		unsaveAndNotifyFileStatusObservers();
		/* Return number of addresses extracted. */
		return addressesExtracted.size();
	}

	/**
	 * @see EMaMModelInterface#addToExtracted(String[])
	 */
	public void addToExtracted(String[] addresses) {
		/* To lower case. */
		toLowerCase(addresses);
		/* Add addresses to extracted set. */
		addToSet(addresses, extracted);
		notifyAddressTablesObservers();
		unsaveAndNotifyFileStatusObservers();
	}

	/**
	 * @see EMaMModelInterface#delFromExtracted(String[])
	 */
	public void delFromExtracted(String[] addresses) {
		/* To lower case. */
		toLowerCase(addresses);
		extracted.removeAll(Arrays.asList(addresses));
		notifyAddressTablesObservers();
		unsaveAndNotifyFileStatusObservers();
	}


	/**
	 * @see EMaMModelInterface#moveExtractedToMailList()
	 */
	public void moveExtractedToMailList() throws MutualExclusionException {
		if (extracted.size() > 0) {
			String extractedArray[] = new String[extracted.size()]; 
			extractedArray = extracted.toArray(extractedArray);
			addToMailList(extractedArray);
			extracted.clear();
			notifyAddressTablesObservers();
			unsaveAndNotifyFileStatusObservers();
		}
	}

	/**
	 * @see EMaMModelInterface#moveExtractedToRemoved()
	 */
	public void moveExtractedToRemoved() throws MutualExclusionException {
		if (extracted.size() > 0) {
			String extractedArray[] = new String[extracted.size()]; 
			extractedArray = extracted.toArray(extractedArray);
			addToRemoved(extractedArray);
			extracted.clear();
			notifyAddressTablesObservers();
			unsaveAndNotifyFileStatusObservers();
		}
	}

	/**
	 * @see EMaMModelInterface#moveExtractedToReturned()
	 */
	public void moveExtractedToReturned() {
		if (extracted.size() > 0) {
			String extractedArray[] = new String[extracted.size()]; 
			extractedArray = extracted.toArray(extractedArray);
			incrementReturned(extractedArray);
			extracted.clear();
			notifyAddressTablesObservers();
			unsaveAndNotifyFileStatusObservers();
		}
	}
	
	/**
	 * @see EMaMModelInterface#getListedAddresses()
	 */
	public String[] getListedAddresses() {
		if (mailList.size() == 0) {
			String empty[] = {};
			return empty;
		}
		return mailList.toArray(new String[mailList.size()]);	
	}
	
	/**
	 * @see EMaMModelInterface#getRemovedAddresses()
	 */
	public String[] getRemovedAddresses() {
		if (removed.size() == 0) {
			String empty[] = {};
			return empty;
		}
		return removed.toArray(new String[removed.size()]);	
	}
	
	/**
	 * @see EMaMModelInterface#getReturnedAddresses()
	 */
	public Object[][] getReturnedAddresses() {
		if (returned.size() == 0) {
			Object empty[][] = {{}};
			return empty;
		}
		Object[][] returnedAddresses = new Object[2][returned.size()];
		returned.keySet().toArray(returnedAddresses[0]);
		returned.values().toArray(returnedAddresses[1]);
		return returnedAddresses;	
	}
	
	/**
	 * @see EMaMModelInterface#getExtractedAddresses()
	 */
	public String[] getExtractedAddresses() {
		if (extracted.size() == 0) {
			String empty[] = {};
			return empty;
		}
		return extracted.toArray(new String[extracted.size()]);	
	}
	
	/**
	 * @see EMaMModelInterface#getNumberOfReturns(String)
	 */
	public int getNumberOfReturns(String address) {
		/* To lower case. */
		address = address.toLowerCase();
		return returned.get(address);
	}
	
	/**
	 * @see EMaMModelInterface#clearExtracted()
	 */
	public void clearExtracted() {
		extracted.clear();
		notifyAddressTablesObservers();
	}

	/**
	 * @see EMaMModelInterface#registerAddressTablesObserver(AddressTablesObserver)
	 */
	public void registerAddressTablesObserver(AddressTablesObserver ato) {
		atObservers.add(ato);
	}

	/**
	 * @see EMaMModelInterface#removeAddressTablesObserver(AddressTablesObserver)
	 */
	public void removeAddressTablesObserver(AddressTablesObserver ato) {
		atObservers.remove(ato);
	}
	
	/**
	 * @see EMaMModelInterface#notifyAddressTablesObservers()
	 */
	public void notifyAddressTablesObservers() {
		for (AddressTablesObserver ato : atObservers) {
			ato.updateAddressTables();
		}
	}

	/**
	 * @see EMaMModelInterface#getCurrentFile()
	 */
	public File getCurrentFile() {
		return currentFile;
	}
	
	/**
	 * @see EMaMModelInterface#isFileSaved()
	 */
	public boolean isFileSaved() {
		return saved;
	}

	/**
	 * @see EMaMModelInterface#notifyFileStatusObservers()
	 */
	public void notifyFileStatusObservers() {
		for (FileStatusObserver fso : fsObservers) {
			fso.updateFileStatus();
		}
	}

	/**
	 * @see EMaMModelInterface#registerFileStatusObserver(FileStatusObserver)
	 */
	public void registerFileStatusObserver(FileStatusObserver fso) {
		fsObservers.add(fso);	
	}

	/**
	 * @see EMaMModelInterface#removeFileStatusObserver(FileStatusObserver)
	 */
	public void removeFileStatusObserver(FileStatusObserver fso) {
		fsObservers.remove(fso);			
	}
	
	/* ********************************************** */
	/*                PRIVATE METHODS                 */
	/* ********************************************** */
	
	/**
	 * Clear data in all sets.
	 */
	private void clearData() {
		/* Clear all previous listings. */
		mailList.clear();
		removed.clear();
		returned.clear();
		extracted.clear();
	}
	
	/**
	 * Checks if any of the given addresses exist in the given set.
	 * 
	 * @param addresses Addresses to check presence of.
	 * @param set Set to check presence of addresses.
	 * @throws MutualExclusionException If any of the given addresses exist in the given set.  
	 */
	private void checkExclusiveContents(String[] addresses, Set<String> set) 
	throws MutualExclusionException {
		/* Check if any of the given addresses exists on the given set. */
		TreeSet<String> problemAddresses = new TreeSet<String>();
		for (String address : addresses) {
			if (set.contains(address)) {
				problemAddresses.add(address);
			}
		}
		/* If there are problematic addresses then throw exception. */
		if (problemAddresses.size() > 0) {
			String problemAddressesArray[] = new String[problemAddresses.size()];
			throw new MutualExclusionException(
					problemAddresses.toArray(
							problemAddressesArray));
		}
	}
	
	/**
	 * Add given addresses to given set.
	 * 
	 * @param addresses Addresses to add.
	 * @param set Set where to add addresses.
	 */
	private void addToSet(String[] addresses, Set<String> set) {
		/* Add addresses to given set. */
		for (String address : addresses) {
			set.add(address);
		}
	}
	
	/**
	 * Delete given addresses from given set.
	 * 
	 * @param addresses Addresses to delete.
	 * @param set Set where to remove addresses from.
	 */
	private void delFromSet(String[] addresses, Set<String> set) {
		/* Remove addresses to given set. */
		for (String address : addresses) {
			set.remove(address);
		}
	}
	
	/**
	 * If saved status is true, set it to false and notity file status observers.
	 */
	private void unsaveAndNotifyFileStatusObservers() {
		if (saved) {
			/* Set saved status to false. */
			saved = false;
			/* Notify file status observers. */
			notifyFileStatusObservers();
		}
	}

	/**
	 * If saved status is false, set it to trye and notify file status observers.
	 */
	private void saveAndNotifyFileStatusObservers() {
		if (!saved) {
			/* Set saved status to true. */
			saved = true;
			/* Notify file status observers. */
			notifyFileStatusObservers();
		}
	}

	/**
	 * Accepts an array of strings and converts respective strings to lower case.
	 * 
	 * @param addresses Array of strings to convert to lower case.
	 */
	private void toLowerCase(String addresses[]) {
		for (int i = 0; i < addresses.length; i++)
			addresses[i] = addresses[i].toLowerCase();
	}
	

}
