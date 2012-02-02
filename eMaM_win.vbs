Set WshShell = WScript.CreateObject("WScript.Shell")
if WScript.Arguments.Count > 0 then
	WshShell.currentdirectory = wscript.scriptfullname & "\.."
	WScript.Echo WshShell.currentdirectory
	WScript.Echo WScript.Arguments(0)
	WshShell.Run "javaw -cp bin com.fakenmc.mail.emam.EMaM " & """" & WScript.Arguments(0) & """"
Else
	WshShell.Run "javaw -cp bin com.fakenmc.mail.emam.EMaM"
End If