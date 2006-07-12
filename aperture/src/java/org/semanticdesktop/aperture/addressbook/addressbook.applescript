-- Really crappy Aperture Crawler for the MacOSX Address Book
-- This version by Gunnar Grimnes, standing on the shoulder of giants (well, midgets?)
-- See http://gnowsis.opendfki.de/wiki/AppleAddressBookDatasource, which in turn has more references

-- Find/change routine from Apple's AppleScript Guidebook
-- module "Essential sub-routines."
on replace_chars(this_text, search_string, replacement_string)
	set AppleScript's text item delimiters to the search_string
	set the item_list to every text item of this_text
	set AppleScript's text item delimiters to the replacement_string
	set this_text to the item_list as string
	set AppleScript's text item delimiters to ""
	return this_text
end replace_chars 


on trim_line(this_text, trim_chars, trim_indicator)
    -- 0 = beginning, 1 = end, 2 = both
    set x to the length of the trim_chars
    -- TRIM BEGINNING
    if the trim_indicator is in {0, 2} then
        repeat while this_text begins with the trim_chars
            try
                set this_text to characters (x + 1) thru -1 of this_text as string
            on error
                -- the text contains nothing but the trim characters
                return ""
            end try
        end repeat
    end if
    -- TRIM ENDING
    if the trim_indicator is in {1, 2} then
        repeat while this_text ends with the trim_chars
            try
                set this_text to characters 1 thru -(x + 1) of this_text as string
            on error
                -- the text contains nothing but the trim characters
                return ""
            end try
        end repeat
    end if
    return this_text
end trim_line

-- my own
on xmlescape(txt)
    set txt to replace_chars(txt,"&","&amp;")
	set txt to replace_chars(txt,"<","&lt;")
	set txt to replace_chars(txt,">","&gt;")
	return txt
end myxmlescape

-- urlencode is from:
-- http://harvey.nu/applescript_url_encode_routine.html
-- This safely encodes text so it can be used in a URL
on urlencode(theText)
	set theTextEnc to ""
	repeat with eachChar in characters of theText
		set useChar to eachChar
		set eachCharNum to ASCII number of eachChar
		if eachCharNum = 32 then
			set useChar to "+"
		else if (eachCharNum ≠ 42) and (eachCharNum ≠ 95) and (eachCharNum < 45 or eachCharNum > 46) and (eachCharNum < 48 or eachCharNum > 57) and (eachCharNum < 65 or eachCharNum > 90) and (eachCharNum < 97 or eachCharNum > 122) then
			set firstDig to round (eachCharNum / 16) rounding down
			set secondDig to eachCharNum mod 16
			if firstDig > 9 then
				set aNum to firstDig + 55
				set firstDig to ASCII character aNum
			end if
			if secondDig > 9 then
				set aNum to secondDig + 55
				set secondDig to ASCII character aNum
			end if
			set numHex to ("%" & (firstDig as string) & (secondDig as string)) as string
			set useChar to numHex
		end if
		set theTextEnc to theTextEnc & useChar as string
	end repeat
	return theTextEnc
end urlencode

set quitonfinish to true
tell application "Finder"
	if exists process "Address Book" then
		set quitonfinish to false
	end if
end tell

tell application "Address Book"
	set out to "<rdf:RDF xmlns:data='http://aperture.semanticdesktop.org/ontology/data#' xmlns:foaf='http://xmlns.com/foaf/0.1/' xmlns:vcard='http://www.gnowsis.org/ont/vcard#' xmlns:rdfs='http://www.w3.org/2000/01/rdf-schema#' xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>" & return
	repeat with p in people
		set out to out & "<vcard:vCard rdf:about='urn:mac:addressbook:" & my urlencode(name of p) & "'>" & return 
		set person_name to my xmlescape(name of p)
		set out to out & "  <vcard:fullname>" & (person_name) & "</vcard:fullname>" & return 
		set out to out & "  <rdfs:label>" & (person_name) & "</rdfs:label>" & return 
		
		repeat with e in emails of p
			set em to my trim_line(my xmlescape(value of e)," ",2)
			set out to out & "  <vcard:email>" & em & "</vcard:email>" & return 
			set out to out & "  <data:emailAddress>" & em & "</data:emailAddress>" & return 
		end repeat
		repeat with g in groups of p 
			set gt to my trim_line(my xmlescape(name of g), " ", 2)
		 	set out to out & "  <data:group>" & gt & "</data:group>" & return 
		end repeat 
		set out to out & "</vcard:vCard>" & return 
	end repeat
	set out to out & "</rdf:RDF>"

	if quitonfinish is true then
		quit
	end if
	
	return out

end tell