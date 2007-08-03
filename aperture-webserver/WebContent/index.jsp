<?xml version="1.0" encoding="ISO-8859-1" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0">
    <jsp:directive.page language="java"
        contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" />
    <jsp:text>
        <![CDATA[ <?xml version="1.0" encoding="ISO-8859-1" ?> ]]>
    </jsp:text>
    <jsp:text>
        <![CDATA[ <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> ]]>
    </jsp:text>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<link rel="stylesheet" href="style.css" type="text/css" />
<title>Aperture Document Inspection</title>
</head>
<body>

<h1>Aperture Document Inspection</h1>

<div>
Wecome to the Aperture Document Inspection. Please send an URL or a document and we will return the content in rdf/xml.
</div>

<div class="fileupload">
<p>Please chose a document.</p>
<form action="FileInspector" method="post" enctype="multipart/form-data">
	<input type="file" name="name" />
	<input type="submit" name="upload" value="upload" />
</form>
</div>

<div class="htmlupload">
<p>Please type in the URL of a web site.</p>
<form action="FileInspector" method="post">
	<input type="text" name="url" />
	<input type="submit" name="upload" value="upload" />
</form>
</div>

</body>
</html>
</jsp:root>