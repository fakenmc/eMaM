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

import java.util.Properties;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.OutputStream;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.event.ActionEvent;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

/**
 * This class is the application loader (contains main method).
 * Also provides global static access to the application properties.
 * 
 * @author Nuno Fachada
 *
 */
public class EMaM {
	
	/* Error constants. */
	public static final int PROPERTIES_NOT_FOUND = -1;
	public static final int ERROR_READING_PROPERTIES = -2;
	public static final int TOO_MANY_COMLINE_ARGS = -3;
	public static final int UNSUPPORTED_LOOK_AND_FEEL = -4;
	public static final int CLASS_NOT_FOUND = -5;
	public static final int INSTANTIATION_EXCEPTION = -6;
	public static final int ILLEGAL_ACCESS_EXCEPTION = -7;
	public static final int LANGUAGE_FILE_NOT_FOUND = -8;
	public static final int UNABLE_TO_SAVE_PROPERTIES = -9;
	public static final int ERROR_OPENING_LANGUAGE_FILE = -10;
	public static final int GUI_NOT_SUPPORTED = -11;
	public static final int MUTUAL_EXCLUSION_ERROR = -12;
	public static final int REGEXP_ERROR = -13;
	/* Global properties filename. */
	final static String PROPERTIES_FILE = "eMaM.properties";
	/* Global properties object. */
	private static Properties globalProperties;
	/* License. */
	public static final String license = "Copyright (c) 2008, Nuno Fachada" +
	"\nAll rights reserved." +
	"\nRedistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:" +
	"\n\n * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer." +
    "\n * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution." +
    "\n * Neither the name of the FakenMC.com nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission." +
    "\n\n THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";

	/** 
	 * Returns the global properties. 
	 * Static method which is available to all classes in the program.
	 * 
	 * @return The global properties class.
	 */
	public static Properties getProperties() {
		return globalProperties;
	}
	
	/**
	 * Static method which is available to all classes in the program.
	 * Saves the global properties.
	 */
	public static void saveProperties() {
		try {
			globalProperties.store(new FileWriter(PROPERTIES_FILE), "EMaM properties");
		} catch (IOException ioe) {
			errorHandler(ioe.getLocalizedMessage(), UNABLE_TO_SAVE_PROPERTIES);
		}
	}
	
	/**
	 * The only allowed command-line parameter is the name of a EMaM file. If not given,
	 * EMaM will try to open the last used file.
	 * 
	 * @param args Nothing, or a EMaM file.
	 */
	public static void main(String[] args) {
		/* Load global properties. */
		globalProperties = new Properties();
		try {
			globalProperties.load(new FileReader(PROPERTIES_FILE));
		} catch (FileNotFoundException fnfe) {
			errorHandler("Program properties file " + PROPERTIES_FILE + " not found!", PROPERTIES_NOT_FOUND);
			return;
		} catch (IOException ioe) {
			errorHandler("Error reading properties file " + PROPERTIES_FILE + " (IO Exception)!", ERROR_READING_PROPERTIES);
			return;
		}
		/* Parse command line parameters. */
		switch (args.length) {
		case 0:
			break;
		case 1:
			globalProperties.setProperty("currentFile", args[0]);
			break;
		default:
			errorHandler("Too many command-line arguments!", TOO_MANY_COMLINE_ARGS);
			return;
		}
		/* Set look and feel. */
	    try {
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException elfe) {
			errorHandler(elfe.getLocalizedMessage(), UNSUPPORTED_LOOK_AND_FEEL);
			return;
	    }
	    catch (ClassNotFoundException cnfe) {
			errorHandler(cnfe.getLocalizedMessage(), CLASS_NOT_FOUND);
			return;
	    }
	    catch (InstantiationException ie) {
			errorHandler(ie.getLocalizedMessage(), INSTANTIATION_EXCEPTION);
			return;
	    }
	    catch (IllegalAccessException iae) {
			errorHandler(iae.getLocalizedMessage(), ILLEGAL_ACCESS_EXCEPTION);
			return;
	    }
		/* Create model and controller. */
		EMaMModelInterface model = new EMaMModel();
		@SuppressWarnings("unused")
		EMaMControllerInterface controller = null;
		try {
			controller = new EMaMController(model);
		} catch (HeadlessException he) {
			errorHandler("This application requires an OS which supports GUIs.", GUI_NOT_SUPPORTED);
			return;
		} catch (FileNotFoundException fnfe) {
			errorHandler(fnfe.getLocalizedMessage(), LANGUAGE_FILE_NOT_FOUND);
			return;
		} catch (IOException ioe) {
			errorHandler(ioe.getLocalizedMessage(), ERROR_OPENING_LANGUAGE_FILE);
			return;
		}
	}
	
	/**
	 * Handles terminal errors in the application startup.
	 * @param errorMsg Error message.
	 * @param errorCode Error code.
	 */
	public static void errorHandler(String errorMsg, int errorCode) {
		/* If the OS supports GUIs, then display errors in a small window. */
		if (!GraphicsEnvironment.isHeadless()) {
			/* Fix. */
			final int internalErrorCode = errorCode;
			/* Create window exit action. */
			Action errorExitAction = new AbstractAction("Exit") {
				private static final long serialVersionUID = 5523280632108507081L;
				public void actionPerformed(ActionEvent e) {
					System.exit(internalErrorCode);
				}
			};
			/* Set error output destination to a small window. */
			System.setErr(createPrintStreamDialog(errorExitAction));
			/* Print error. */
			System.err.println(errorMsg);
		} else {
			System.err.println(errorMsg);
			System.exit(errorCode);
		}
	}
	
	/**
	 * Returns a print stream whose output is a window.
	 * 
	 * @return A print stream whose output is a window.
	 */
	private static PrintStream createPrintStreamDialog(Action action) {
		return
			new PrintStream(
				new OutputStream() {
					//private JFrame errFrame = null;
					private JTextArea errTextArea = null;
					//private JButton errButton = null;
					public void write(int i) {
						if (errTextArea == null) {
							/* Create text area for outputting errors. */
							errTextArea = new JTextArea(10,30);
							errTextArea.setEditable(false);
							errTextArea.setLineWrap(true);
							/* Create dialog. */
							javax.swing.SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									JOptionPane.showMessageDialog(
											null, 
											new JScrollPane(errTextArea), 
											"Error", 
											JOptionPane.ERROR_MESSAGE);
								}
							});
						}
						errTextArea.append("" + (char) i);
					}
				}
			);		
	}
		
}
