-- Crappy applescript for extracting iPhoto keywords

tell application "iPhoto"
	set out to ""
	repeat with k in keywords
		set out to out & name of k  & return
	end repeat
	out
end tell