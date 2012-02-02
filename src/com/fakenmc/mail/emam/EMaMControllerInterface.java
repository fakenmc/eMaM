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

/**
 * Interface which EMaM controllers must implement.
 * Controller responsibilities are described by the MVC design patter.
 * 
 * @author Nuno Fachada
 */
public interface EMaMControllerInterface {
	
	/**
	 * Request to start a new blank file.
	 */
	public void newFile();

	/**
	 * Request to open file.
	 */
	public void openFile();
	
	/**
	 * Request to save file.
	 */
	public void saveFile();
	
	/**
	 * Request to save file with a different name.
	 */
	public void saveAsFile();
	
	/**
	 * Request for application options.
	 */
	public void options();
	
	/**
	 * Request for information about the application.
	 */
	public void about();
	
	/**
	 * Request to terminate the application.
	 */
	public void exit();
	
	/**
	 * Request to add an address to the mailing list.
	 */
	public void addToMailList();
	
	/**
	 * Request to delete an array of addresses from the mailing list.
	 * 
	 * @param addresses Array of strings containing addresses to delete.
	 */
	public void delFromMailList(String[] addresses);
	
	/**
	 * Request for copying mailing list contents to the system clipboard.
	 */
	public void copyMailListToClipboard();
	
	/**
	 * Request for moving an array of addresses from the mailing list to
	 * the returned list.
	 * 
	 * @param addresses Array of strings containing addresses to be moved.
	 */
	public void moveFromMailListToRemoved(String[] addresses);
	
	/**
	 * Request for moving an array of addresses from the removed list to
	 * the mailing list.
	 * @param addresses
	 */
	public void moveFromRemovedToMailList(String[] addresses);
	
	/**
	 * Request to add an address to the removed list.
	 */
	public void addToRemoved();
	
	/**
	 * Request to delete an array of addresses from the removed list.
	 * 
	 * @param addresses Array of strings containing addresses to delete.
	 */
	public void delFromRemoved(String[] addresses);
	
	/**
	 * Request for copying removed list contents to the system clipboard.
	 */
	public void copyRemovedToClipboard();
	
	/**
	 * Request to add an address to the returned list.
	 */
	public void addToReturned();
	
	/**
	 * Request to delete an array of addresses from the returned list.
	 * 
	 * @param addresses Array of strings containing addresses to delete.
	 */
	public void delFromReturned(String[] addresses);
	
	/**
	 * Increment by one the number of returns of the given addresses 
	 * in the returned list.
	 * 
	 * @param addresses Array of strings containing addresses to increment.
	 */
	public void incrementReturned(String[] addresses);
	
	/**
	 * Decrement by one the number of returns of the given addresses 
	 * in the returned list.
	 * 
	 * @param addresses Array of strings containing addresses to decrement.
	 */
	public void decrementReturned(String[] addresses);
	
	/**
	 * Process returned mail list, i.e., move addresses indicated in the returned
	 * list from the mail list to the removed list.
	 */
	public void processReturned();
	
	/**
	 * Extract addresses from a file. Addresses will be placed in the
	 * extracted list.
	 */
	public void extractAddressesFromFile();

	/**
	 * Extract addresses from given text. Addresses will be placed in the
	 * extracted list.
	 * 
	 * @param text Text from where to extract addresses.
	 */
	public void extractAddresses(String text);
	
	/**
	 * Move extracted addresses to the mail list.
	 */
	public void moveExtractedToMailList();
	
	/**
	 * Move extracted addresses to the removed list.
	 */
	public void moveExtractedToRemoved();
	
	/**
	 * Move extracted addresses to the returned list.
	 */
	public void moveExtractedToReturned();
	
	/**
	 * Request to add an address to the extracted list.
	 */
	public void addToExtracted();
	
	/**
	 * Request to delete an array of addresses from the extracted list.
	 * 
	 * @param addresses Array of strings containing addresses to delete.
	 */

	public void delFromExtracted(String[] addresses);
	
	/**
	 * Clear extracted list.
	 */
	public void clearExtracted();


}
