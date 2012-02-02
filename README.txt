1. System requirements
2. Install eMaM
3. Running eMaM
4. Configuration
5. Additional comments

1. System requirements

Any computer with Java Runtime Environment (JRE) 6 or better; 
download it from http://java.sun.com .

2. Install eMaM

Just copy the eMaM folder to wherever you want. You probably should create
a link to one of the provided execution scripts (see next section).
In future versions we will include an installer for Windows.

3. Running eMaM

Several scripts are provided for running the application in different
operating systems. The main program, and all of these execution scripts 
accept one parameter indicating the file to open. By default eMaM will 
open the last used file.

3.1 DOS/Windows

Execute "eMaM_dos.bat" script.

3.2 Windows

Execute "eMaM_win.vbs" script. 

3.3 Linux/Unix

Execute "eMaM_linux.sh" script.

3.4 All operating systems

Run the program using Java directly, i.e.:
"java -cp bin com.fakenmc.mail.emam.EMaM" 

4. Configuration

At the moment we have two languages available, english (en) 
and portuguese (pt). To change language edit "eMaM.properties" file 
and change the "lang" parameter to "en" or "pt". The language files
are in the "lang" directory, in "en" and "pt" subdirectories 
respectively.
If you want to add your own language, create a new directory (for
example "fr" for french), copy files from "en" into "fr", and edit
those files with your favorite text editor. Then change the "lang"
property in "eMaM.properties" file to "fr".
I do not advise the addition of new languages before the beta release,
because until then several aspects of the application may still change.

5. Additional comments

This is the second alpha release of eMaM, please check TODO.txt file to 
know what to expect in the future...

Meanwhile test the program and/or check the source code, if you have any 
suggestions or questions please don't hesitate to contact me 
at faken@fakenmc.com.

I hope you find eMaM useful!

