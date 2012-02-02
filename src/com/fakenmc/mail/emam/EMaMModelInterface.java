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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.PatternSyntaxException;

/**
 * EMaM model interface.
 * 
 * @author Nuno Fachada
 */
public interface EMaMModelInterface {
	
	/**
	 * Loads an emam file into the model.
	 * 
	 * @param file File to load into the model.
	 * @throws FileNotFoundException @see java.io.FileNotFoundException
	 * @throws IOException @see java.io.IOException
	 * @throws PatternSyntaxException @see java.util.regex.PatternSyntaxException
	 * @throws InvalidStringException @see InvalidStringException
	 * @throws InvalidFileFormatException @see InvalidFileFormatException
	 */
	public void openFile(File file) 
	throws FileNotFoundException, IOException, 
	PatternSyntaxException, InvalidStringException, InvalidFileFormatException;
	
	/**
	 * Resets the model.
	 */
	public void newFile();
	
	/**
	 * Saves current model information into the currently set file.
	 * 
	 * @throws IOException @see java.io.IOException
	 */
	public void saveFile() throws IOException;

	/**
	 * Saves current model information into a specified file.
	 * 
	 * @param file File where to save model.
	 * @throws IOException @see java.io.IOException
	 */
	public void saveFileAs(File file) throws IOException;

	/**
	 * Adds an array of addresses to the mailing list.
	 * 
	 * @param addresses Array of addresses to add to the mailing list.
	 * @throws MutualExclusionException @see MutualExclusionException
	 */
	public void addToMailList(String[] addresses) throws MutualExclusionException;
	
	/**
	 * Deletes an array of addresses from the mailing list.
	 * 
	 * @param addresses Array of addresses to remove from the mailing list.
	 */
	public void delFromMailList(String[] addresses);
	
	/**
	 * Moves an array of addresses from the mailing list to the removed list.
	 * 
	 * @param addresses Addresses to be moved.
	 */
	public void moveFromMailListToRemoved(String[] addresses);
	
	/**
	 * Adds an array of addresses to the removed list.
	 * 
	 * @param addresses Array of addresses to add to the removed list.
	 * @throws MutualExclusionException @see MutualExclusionException
	 */
	public void addToRemoved(String[] addresses) throws MutualExclusionException;
	
	/**
	 * Deletes an array of addresses from the removed list.
	 * 
	 * @param addresses Array of addresses to remove from the removed list.
	 */
	public void delFromRemoved(String[] addresses);

	/**
	 * Moves an array of addresses from the removed list to the mailing list.
	 * 
	 * @param addresses Addresses to be moved.
	 */
	public void moveFromRemovedToMailList(String[] addresses);

	/**
	 * Adds an array of addresses to the returned list.
	 * 
	 * @param addresses Array of addresses to add to the returned list.
	 */
	public void addToReturned(String[] addresses);
	
	/**
	 * Deletes an array of addresses from the returned list.
	 * 
	 * @param addresses Array of addresses to remove from the returned list.
	 */
	public void delFromReturned(String[] addresses);

	/**
	 * Increment array of addresses returned count by one in returned list.
	 * 
	 * @param addresses Array of addresses to increment return count.
	 */
	public void incrementReturned(String[] addresses);
	
	/**
	 * Decrement array of addresses returned count by one in returned list.
	 * 
	 * @param addresses Array of addresses to decrement return count.
	 */
	public void decrementReturned(String[] addresses);
	
	/**
	 * Process addresses in return list which have a count equal or bigger than
	 * n.
	 * @param n Minimum number of returned count to process each address.
	 */
	public void processReturned(int n);
	
	/**
	 * Extract email addresses from given text string.
	 * 
	 * @param addresses Text string from where to extract email addresses.
	 * @return Number of addresses extracted.
	 */
	public int extractAddresses(String addresses);
	
	/**
	 * Adds an array of addresses to the extracted list.
	 * 
	 * @param addresses Array of addresses to add to the extracted list.
	 */
	public void addToExtracted(String[] addresses);
	
	/**
	 * Deletes an array of addresses from the extracted list.
	 * 
	 * @param addresses Array of addresses to remove from the extracted list.
	 */
	public void delFromExtracted(String[] addresses);

	/**
	 * Move extracted addresses to mailing list.
	 * 
	 * @throws MutualExclusionException @see MutualExclusionException
	 */
	public void moveExtractedToMailList() throws MutualExclusionException;

	/**
	 * Move extracted addresses to removed list.
	 * 
	 * @throws MutualExclusionException @see MutualExclusionException
	 */
	public void moveExtractedToRemoved() throws MutualExclusionException;

	/**
	 * Move extracted addresses to returned list.
	 */
	public void moveExtractedToReturned();

	/**
	 * Returns an array of addresses contained in the mailing list.
	 * 
	 * @return An array of addresses contained in the mailing list.
	 */
	public String[] getListedAddresses();
	
	/**
	 * Returns an array of addresses contained in the removed list.
	 * 
	 * @return An array of addresses contained in the removed list.
	 */
	public String[] getRemovedAddresses();
	
	/**
	 * Returns an array of addresses contained in the returned list, and
	 * respective return count.
	 * 
	 * @return An array of addresses contained in the return list, and respective return count.
	 */
	public Object[][] getReturnedAddresses();
	
	/**
	 * Returns an array of addresses contained in the extracted list.
	 * 
	 * @return An array of addresses contained in the extracted list.
	 */
	public String[] getExtractedAddresses();
	
	/**
	 * Get the return count for the given address.
	 * 
	 * @param addresses to get the return count of.
	 * @return The return count for the given address.
	 */
	public int getNumberOfReturns(String addresses);
	
	/**
	 * Clear extracted address list.
	 */
	public void clearExtracted();

	/**
	 * Register a given address tables observer. The observer will be notified
	 * when any of the address tables changes.
	 * 
	 * @param ato An address table observer.
	 */
	public void registerAddressTablesObserver(AddressTablesObserver ato);

	/**
	 * Remove given address tables observer.
	 * 
	 * @param ato An address table observer.
	 */
	public void removeAddressTablesObserver(AddressTablesObserver ato);
		
	/**
	 * Notify address table observers that address tables have changed.
	 */
	public void notifyAddressTablesObservers();
	
	/**
	 * Register a given file status observer. The observer will be notified
	 * when the name or saved status of the file changes.
	 * 
	 * @param fso A file status observer.
	 */
	public void registerFileStatusObserver(FileStatusObserver fso);

	/**
	 * Remove given file status observer.
	 * 
	 * @param fso A file status observer.
	 */
	public void removeFileStatusObserver(FileStatusObserver fso);
		
	/**
	 * Notify file status observers that the model's file status have changed.
	 */
	public void notifyFileStatusObservers();

	/**
	 * Returns the current model file.
	 * 
	 * @return The current model file.
	 */
	public File getCurrentFile();
	
	/**
	 * Returnd true if current model file is saved, false otherwise.
	 * @return True if current model file is saved, false otherwise.
	 */
	public boolean isFileSaved();

}
