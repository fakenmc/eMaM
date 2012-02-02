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

import java.awt.datatransfer.ClipboardOwner;
import java.io.File;

/**
 * Interface for the views of the EMaM application.
 * 
 * @author Nuno Fachada
 */
public interface EMaMViewInterface extends AddressTablesObserver, FileStatusObserver, ClipboardOwner {

	/* Constant for opening eMaM files. */
	public final int EMAM_FILES = 0;
	/* Constant for opening all files. */
	public final int ALL_FILES = 1;

	/**
	 * Creates and displays the view.
	 */
	public void createView();

	/**
	 * Disposes of the view.
	 */
	public void dispose();
	
	/**
	 * Presents several options for the user to choose.
	 * @param message The message to present the user.
	 * @param messageExtras Extra message information.
	 * @param title Title of the request to be presented to the user. 
	 * @param options The options from which the user can choose.
	 * @param defaultOption The default option.
	 * @return An integer indicating which was the choosen option.
	 */
	public int dialogOptions(String message, String messageExtras[], String title, String options[], String defaultOption);
	
	/**
	 * Requests the user for a file to be opened.
	 * 
	 * @param fileType A constant in <code>EMaMViewInterface</code> indicating type of files to open. 
	 * @return A file to be opened.
	 */
	public File selectFileToOpen(int fileType);
	
	/**
	 * Requests the user for a file to be saved.
	 * @param filename Suggested filename depending on context.
	 * @return A file to be saved.
	 */
	public File selectFileToSave(String filename);
	
	/**
	 * Presents an information message to the user.
	 * @param message The message to present the user.
	 * @param messageExtras Extra message information.
	 * @param title Title of the request to be presented to the user.
	 */
	public void dialogInformation(String message, String messageExtras[], String title);

	/**
	 * Presents a warning message to the user.
	 * @param message The message to present the user.
	 * @param messageExtras Extra message information.
	 * @param title Title of the request to be presented to the user.
	 */
	public void dialogWarning(String message, String messageExtras[], String title);
	
	/**
	 * Presents an error message to the user.
	 * @param message The message to present the user.
	 * @param messageExtras Extra message information.
	 * @param title Title of the request to be presented to the user.
	 */
	public void dialogError(String message, String messageExtras[], String title);
	
	/**
	 * Requests the user to insert a specified string.
	 * @param message The message to present the user.
	 * @param messageExtras Extra message information.
	 * @param title Title of the request to be presented to the user.
	 */
	public String dialogInput(String message, String messageExtras[], String title);
	
	/**
	 * Copy the given string to the system clipboard, if possible.
	 * @param str The string to be copied to the system clipboard.
	 */
	public void copyToClipboard(String str);
}
