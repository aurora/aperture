package org.semanticdesktop.aperture.vocabulary;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Sun Jun 29 15:41:49 CEST 2008
 * input file: D:\workspace\aperture/doc/ontology/nfo.rdfs
 * namespace: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#
 */
public class NFO {

    /** Path to the ontology resource */
    public static final String NFO_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/vocabulary/nfo.rdfs";

    /**
     * Puts the NFO ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getNFOOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(NFO_RESOURCE_PATH, NFO.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + NFO_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.RdfXml);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for NFO */
    public static final URI NS_NFO = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#");
    /**
     * Type: Class <br/>
     * Label: Visual  <br/>
     * Comment: File containing visual content.  <br/>
     */
    public static final URI Visual = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Visual");
    /**
     * Type: Class <br/>
     * Label: Media  <br/>
     * Comment: A piece of media content. This class may be used to express complex media containers with many streams of various media content (both aural and visual).  <br/>
     */
    public static final URI Media = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Media");
    /**
     * Type: Class <br/>
     * Label: PaginatedTextDocument  <br/>
     * Comment: A file containing a text document, that is unambiguously divided into pages. Examples might include PDF, DOC, PS, DVI etc.  <br/>
     */
    public static final URI PaginatedTextDocument = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#PaginatedTextDocument");
    /**
     * Type: Class <br/>
     * Label: TextDocument  <br/>
     * Comment: A text document  <br/>
     */
    public static final URI TextDocument = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#TextDocument");
    /**
     * Type: Class <br/>
     * Label: Application  <br/>
     * Comment: An application  <br/>
     */
    public static final URI Application = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Application");
    /**
     * Type: Class <br/>
     * Label: Software  <br/>
     * Comment: A piece of software. Examples may include applications and the operating system. This interpretation most commonly applies to SoftwareItems.  <br/>
     */
    public static final URI Software = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Software");
    /**
     * Type: Class <br/>
     * Label: HardDiskPartition  <br/>
     * Comment: A partition on a hard disk  <br/>
     */
    public static final URI HardDiskPartition = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#HardDiskPartition");
    /**
     * Type: Class <br/>
     * Label: Audio  <br/>
     * Comment: A file containing audio content  <br/>
     */
    public static final URI Audio = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Audio");
    /**
     * Type: Class <br/>
     * Label: Presentation  <br/>
     * Comment: A Presentation made by some presentation software (Corel Presentations, OpenOffice Impress, MS Powerpoint etc.)  <br/>
     */
    public static final URI Presentation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Presentation");
    /**
     * Type: Class <br/>
     * Label: Document  <br/>
     * Comment: A generic document. A common superclass for all documents on the desktop.  <br/>
     */
    public static final URI Document = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Document");
    /**
     * Type: Class <br/>
     * Label: MediaStream  <br/>
     * Comment: A stream of multimedia content, usually contained within a media container such as a movie (containing both audio and video) or a DVD (possibly containing many streams of audio and video). Most common interpretations for such a DataObject include Audio and Video.  <br/>
     */
    public static final URI MediaStream = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#MediaStream");
    /**
     * Type: Class <br/>
     * Label: EmbeddedFileDataObject  <br/>
     * Comment: A file embedded in another data object. There are many ways in which a file may be embedded in another one. Use this class directly only in cases if none of the subclasses gives a better description of your case.  <br/>
     */
    public static final URI EmbeddedFileDataObject = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#EmbeddedFileDataObject");
    /**
     * Type: Class <br/>
     * Label: FileDataObject  <br/>
     * Comment: A resource containing a finite sequence of bytes with arbitrary information, that is available to a computer program and is usually based on some kind of durable storage. A file is durable in the sense that it remains available for programs to use after the current program has finished.  <br/>
     */
    public static final URI FileDataObject = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#FileDataObject");
    /**
     * Type: Class <br/>
     * Label: MindMap  <br/>
     * Comment: A MindMap, created by a mind-mapping utility. Examples might include FreeMind or mind mapper.  <br/>
     */
    public static final URI MindMap = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#MindMap");
    /**
     * Type: Class <br/>
     * Label: Image  <br/>
     * Comment: A file containing an image.  <br/>
     */
    public static final URI Image = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Image");
    /**
     * Type: Class <br/>
     * Label: SoftwareService  <br/>
     * Comment: A service published by a piece of software, either by an operating system or an application. Examples of such services may include calendar, addresbook and mailbox managed by a PIM application. This category is introduced to distinguish between data available directly from the applications (Via some Interprocess Communication Mechanisms) and data available from files on a disk. In either case both DataObjects would receive a similar interpretation (e.g. a Mailbox) and wouldn't differ on the content level.  <br/>
     */
    public static final URI SoftwareService = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#SoftwareService");
    /**
     * Type: Class <br/>
     * Label: PlainTextDocument  <br/>
     * Comment: A file containing plain text (ASCII, Unicode or other encodings). Examples may include TXT, HTML, XML, program source code etc.  <br/>
     */
    public static final URI PlainTextDocument = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#PlainTextDocument");
    /**
     * Type: Class <br/>
     * Label: FilesystemImage  <br/>
     * Comment: An image of a filesystem. Instances of this class may include CD images, DVD images or hard disk partition images created by various pieces of software (e.g. Norton Ghost)  <br/>
     */
    public static final URI FilesystemImage = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#FilesystemImage");
    /**
     * Type: Class <br/>
     * Label: Filesystem  <br/>
     * Comment: A filesystem. Examples of filesystems include hard disk partitions, removable media, but also images thereof stored in files.  <br/>
     */
    public static final URI Filesystem = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Filesystem");
    /**
     * Type: Class <br/>
     * Label: CompressionType  <br/>
     * Comment: Type of compression. Instances of this class represent the limited set of values allowed for the nfo:compressionType property.  <br/>
     */
    public static final URI CompressionType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#CompressionType");
    /**
     * Type: Class <br/>
     * Label: Folder  <br/>
     * Comment: A folder/directory. Examples of folders include folders on a filesystem and message folders in a mailbox.  <br/>
     */
    public static final URI Folder = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Folder");
    /**
     * Type: Class <br/>
     * Label: DataContainer  <br/>
     * Comment: A superclass for all entities, whose primary purpose is to serve as containers for other data object. They usually don't have any "meaning" by themselves. Examples include folders, archives and optical disc images.  <br/>
     */
    public static final URI DataContainer = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#DataContainer");
    /**
     * Type: Class <br/>
     * Label: Website  <br/>
     * Comment: A website, usually a container for remote resources, that may be interpreted as HTMLDocuments, images or other types of content.  <br/>
     */
    public static final URI Website = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Website");
    /**
     * Type: Class <br/>
     * Label: ArchiveItem  <br/>
     * Comment: A file entity inside an archive.  <br/>
     */
    public static final URI ArchiveItem = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#ArchiveItem");
    /**
     * Type: Class <br/>
     * Label: VectorImage  <br/>
     */
    public static final URI VectorImage = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#VectorImage");
    /**
     * Type: Class <br/>
     * Label: RasterImage  <br/>
     * Comment: A raster image.  <br/>
     */
    public static final URI RasterImage = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#RasterImage");
    /**
     * Type: Class <br/>
     * Label: Cursor  <br/>
     * Comment: A Cursor.  <br/>
     */
    public static final URI Cursor = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Cursor");
    /**
     * Type: Class <br/>
     * Label: DeletedResource  <br/>
     * Comment: A file entity that has been deleted from the original source. Usually such entities are stored within various kinds of 'Trash' or 'Recycle Bin' folders.  <br/>
     */
    public static final URI DeletedResource = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#DeletedResource");
    /**
     * Type: Class <br/>
     * Label: MediaFileListEntry  <br/>
     * Comment: A single node in the list of media files contained within an MediaList instance. This class is intended to provide a type all those links have. In valid NRL untyped resources cannot be linked. There are no properties defined for this class but the application may expect rdf:first and rdf:last links. The former points to the DataObject instance, interpreted as Media the latter points at another MediaFileListEntr. At the end of the list there is a link to rdf:nil.  <br/>
     */
    public static final URI MediaFileListEntry = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#MediaFileListEntry");
    /**
     * Type: Class <br/>
     * Label: Trash  <br/>
     * Comment: Represents a container for deleted files, a feature common in modern operating systems.  <br/>
     */
    public static final URI Trash = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Trash");
    /**
     * Type: Class <br/>
     * Label: HtmlDocument  <br/>
     * Comment: A HTML document, may contain links to other files.  <br/>
     */
    public static final URI HtmlDocument = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#HtmlDocument");
    /**
     * Type: Class <br/>
     * Label: Archive  <br/>
     * Comment: A compressed file. May contain other files or folder inside.  <br/>
     */
    public static final URI Archive = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Archive");
    /**
     * Type: Class <br/>
     * Label: FileHash  <br/>
     * Comment: A fingerprint of the file, generated by some hashing function.  <br/>
     */
    public static final URI FileHash = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#FileHash");
    /**
     * Type: Class <br/>
     * Label: Executable  <br/>
     * Comment: An executable file.  <br/>
     */
    public static final URI Executable = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Executable");
    /**
     * Type: Class <br/>
     * Label: Video  <br/>
     * Comment: A video file.  <br/>
     */
    public static final URI Video = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Video");
    /**
     * Type: Class <br/>
     * Label: Font  <br/>
     * Comment: A font.  <br/>
     */
    public static final URI Font = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Font");
    /**
     * Type: Class <br/>
     * Label: SoftwareItem  <br/>
     * Comment: A DataObject representing a piece of software. Examples of interpretations of a SoftwareItem include an Application and an OperatingSystem.  <br/>
     */
    public static final URI SoftwareItem = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#SoftwareItem");
    /**
     * Type: Class <br/>
     * Label: SourceCode  <br/>
     * Comment: Code in a compilable or interpreted programming language.  <br/>
     */
    public static final URI SourceCode = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#SourceCode");
    /**
     * Type: Class <br/>
     * Label: Spreadsheet  <br/>
     * Comment: A spreadsheet, created by a spreadsheet application. Examples might include Gnumeric, OpenOffice Calc or MS Excel.  <br/>
     */
    public static final URI Spreadsheet = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Spreadsheet");
    /**
     * Type: Class <br/>
     * Label: OperatingSystem  <br/>
     * Comment: An OperatingSystem  <br/>
     */
    public static final URI OperatingSystem = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#OperatingSystem");
    /**
     * Type: Class <br/>
     * Label: Icon  <br/>
     * Comment: An Icon (regardless of whether it's a raster or a vector icon. A resource representing an icon could have two types (Icon and Raster, or Icon and Vector) if required.  <br/>
     */
    public static final URI Icon = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Icon");
    /**
     * Type: Class <br/>
     * Label: RemotePortAddress  <br/>
     * Comment: An address specifying a remote host and port. Such an address can be interpreted in many ways (examples of such interpretations include mailboxes, websites, remote calendars or filesystems), depending on an interpretation, various kinds of data may be extracted from such an address.  <br/>
     */
    public static final URI RemotePortAddress = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#RemotePortAddress");
    /**
     * Type: Class <br/>
     * Label: Attachment  <br/>
     * Comment: A file attached to another data object. Many data formats allow for attachments: emails, vcards, ical events, id3 and exif...  <br/>
     */
    public static final URI Attachment = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Attachment");
    /**
     * Type: Class <br/>
     * Label: MediaList  <br/>
     * Comment: A file containing a list of media files.e.g. a playlist  <br/>
     */
    public static final URI MediaList = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#MediaList");
    /**
     * Type: Class <br/>
     * Label: RemoteDataObject  <br/>
     * Comment: A file data object stored at a remote location. Don't confuse this class with a RemotePortAddress. This one applies to a particular resource, RemotePortAddress applies to an address, that can have various interpretations.  <br/>
     */
    public static final URI RemoteDataObject = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#RemoteDataObject");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#CompressionType <br/>
     * Label: losslessCompressionType  <br/>
     */
    public static final URI losslessCompressionType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#losslessCompressionType");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#CompressionType <br/>
     * Label: lossyCompressionType  <br/>
     */
    public static final URI lossyCompressionType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#lossyCompressionType");
    /**
     * Type: Property <br/>
     * Label: horizontalResolution  <br/>
     * Comment: Horizontal resolution of an image (if printed). Expressed in DPI.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Image  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI horizontalResolution = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#horizontalResolution");
    /**
     * Type: Property <br/>
     * Label: sampleRate  <br/>
     * Comment: The amount of audio samples per second.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#float  <br/>
     */
    public static final URI sampleRate = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#sampleRate");
    /**
     * Type: Property <br/>
     * Label: rate  <br/>
     * Comment: A common superproperty for all properties specifying the media rate. Examples of subproperties may include frameRate for video and sampleRate for audio. This property is expressed in units per second.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Media  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#float  <br/>
     */
    public static final URI rate = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#rate");
    /**
     * Type: Property <br/>
     * Label: fileName  <br/>
     * Comment: Name of the file, together with the extension  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#FileDataObject  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI fileName = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#fileName");
    /**
     * Type: Property <br/>
     * Label: hashAlgorithm  <br/>
     * Comment: Name of the algorithm used to compute the hash value. Examples might include CRC32, MD5, SHA, TTH etc.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#FileHash  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI hashAlgorithm = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#hashAlgorithm");
    /**
     * Type: Property <br/>
     * Label: uncompressedSize  <br/>
     * Comment: Uncompressed size of the content of a compressed file.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Archive  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI uncompressedSize = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#uncompressedSize");
    /**
     * Type: Property <br/>
     * Label: commentCharacterCount  <br/>
     * Comment: The amount of character in comments i.e. characters ignored by the compiler/interpreter.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#SourceCode  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI commentCharacterCount = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#commentCharacterCount");
    /**
     * Type: Property <br/>
     * Label: deletionDate  <br/>
     * Comment: The date and time of the deletion.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#DeletedResource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI deletionDate = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#deletionDate");
    /**
     * Type: Property <br/>
     * Label: foundry  <br/>
     * Comment: The foundry, the organization that created the font.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Font  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI foundry = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#foundry");
    /**
     * Type: Property <br/>
     * Label: sideChannels  <br/>
     * Comment: Number of side channels  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI sideChannels = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#sideChannels");
    /**
     * Type: Property <br/>
     * Label: channels  <br/>
     * Comment: Number of channels. This property is to be used directly if no detailed information is necessary. Otherwise use more detailed subproperties.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI channels = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#channels");
    /**
     * Type: Property <br/>
     * Label: interlaceMode  <br/>
     * Comment: True if the image is interlaced, false if not.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Visual  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#boolean  <br/>
     */
    public static final URI interlaceMode = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#interlaceMode");
    /**
     * Type: Property <br/>
     * Label: width  <br/>
     * Comment: Visual content width in pixels.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Visual  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI width = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#width");
    /**
     * Type: Property <br/>
     * Label: originalLocation  <br/>
     * Comment: The original location of the deleted resource.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#DeletedResource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI originalLocation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#originalLocation");
    /**
     * Type: Property <br/>
     * Label: frameCount  <br/>
     * Comment: The amount of frames in a video sequence.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Video  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI frameCount = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#frameCount");
    /**
     * Type: Property <br/>
     * Label: count  <br/>
     * Comment: A common superproperty for all properties signifying the amount of atomic media data units. Examples of subproperties may include sampleCount and frameCount.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Media  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI count = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#count");
    /**
     * Type: Property <br/>
     * Label: definesFunction  <br/>
     * Comment: A name of a function/method defined in the given source code file.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#SourceCode  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI definesFunction = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#definesFunction");
    /**
     * Type: Property <br/>
     * Label: hasMediaFileListEntry  <br/>
     * Comment: This property is intended to point to an RDF list of MediaFiles.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#MediaList  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#MediaFileListEntry  <br/>
     */
    public static final URI hasMediaFileListEntry = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#hasMediaFileListEntry");
    /**
     * Type: Property <br/>
     * Label: permissions  <br/>
     * Comment: A string containing the permissions of a file. A feature common in many UNIX-like operating systems.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#FileDataObject  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI permissions = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#permissions");
    /**
     * Type: Property <br/>
     * Label: lineCount  <br/>
     * Comment: The amount of lines in a text document  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#TextDocument  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI lineCount = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#lineCount");
    /**
     * Type: Property <br/>
     * Label: colorDepth  <br/>
     * Comment: Amount of bits used to express the color of each pixel.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Visual  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI colorDepth = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#colorDepth");
    /**
     * Type: Property <br/>
     * Label: bitDepth  <br/>
     * Comment: A common superproperty for all properties signifying the amount of bits for an atomic unit of data. Examples of subproperties may include bitsPerSample and bitsPerPixel  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Media  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI bitDepth = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#bitDepth");
    /**
     * Type: Property <br/>
     * Label: averageBitrate  <br/>
     * Comment: The average overall bitrate of a media container. (i.e. the size of the piece of media in bits, divided by it's duration expressed in seconds).  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Media  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#float  <br/>
     */
    public static final URI averageBitrate = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#averageBitrate");
    /**
     * Type: Property <br/>
     * Label: wordCount  <br/>
     * Comment: The amount of words in a text document.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#TextDocument  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI wordCount = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#wordCount");
    /**
     * Type: Property <br/>
     * Label: fileOwner  <br/>
     * Comment: The owner of the file as defined by the file system access rights feature.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#FileDataObject  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI fileOwner = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#fileOwner");
    /**
     * Type: Property <br/>
     * Label: fileLastAccessed  <br/>
     * Comment: Time when the file was last accessed.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#FileDataObject  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI fileLastAccessed = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#fileLastAccessed");
    /**
     * Type: Property <br/>
     * Label: characterCount  <br/>
     * Comment: The amount of characters in the document.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#TextDocument  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI characterCount = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#characterCount");
    /**
     * Type: Property <br/>
     * Label: aspectRatio  <br/>
     * Comment: Visual content aspect ratio. (Width divided by Height)  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Visual  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#float  <br/>
     */
    public static final URI aspectRatio = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#aspectRatio");
    /**
     * Type: Property <br/>
     * Label: supercedes  <br/>
     * Comment: States that a piece of software supercedes another piece of software.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Software  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Software  <br/>
     */
    public static final URI supercedes = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#supercedes");
    /**
     * Type: Property <br/>
     * Label: belongsToContainer  <br/>
     * Comment: Models the containment relations between Files and Folders (or CompressedFiles).  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#DataContainer  <br/>
     */
    public static final URI belongsToContainer = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#belongsToContainer");
    /**
     * Type: Property <br/>
     * Label: programmingLanguage  <br/>
     * Comment: Indicates the name of the programming language this source code file is written in. Examples might include 'C', 'C++', 'Java' etc.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#SourceCode  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI programmingLanguage = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#programmingLanguage");
    /**
     * Type: Property <br/>
     * Label: verticalResolution  <br/>
     * Comment: Vertical resolution of an Image (if printed). Expressed in DPI  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Image  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI verticalResolution = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#verticalResolution");
    /**
     * Type: Property <br/>
     * Label: fileUrl  <br/>
     * Comment: URL of the file. It points at the location of the file. In cases where creating a simple file:// or http:// URL for a file is difficult (e.g. for files inside compressed archives) the applications are encouraged to use conventions defined by Apache Commons VFS Project at http://jakarta.apache.org/  commons/ vfs/ filesystems.html.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#FileDataObject  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI fileUrl = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#fileUrl");
    /**
     * Type: Property <br/>
     * Label: frameRate  <br/>
     * Comment: Amount of video frames per second.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Video  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#float  <br/>
     */
    public static final URI frameRate = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#frameRate");
    /**
     * Type: Property <br/>
     * Label: sampleCount  <br/>
     * Comment: The amount of samples in an audio clip.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI sampleCount = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#sampleCount");
    /**
     * Type: Property <br/>
     * Label: fontFamily  <br/>
     * Comment: The name of the font family.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Font  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI fontFamily = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#fontFamily");
    /**
     * Type: Property <br/>
     * Label: height  <br/>
     * Comment: Visual content height in pixels.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Visual  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI height = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#height");
    /**
     * Type: Property <br/>
     * Label: frontChannels  <br/>
     * Comment: Number of front channels.  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI frontChannels = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#frontChannels");
    /**
     * Type: Property <br/>
     * Label: fileCreated  <br/>
     * Comment: File creation date  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#FileDataObject  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI fileCreated = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#fileCreated");
    /**
     * Type: Property <br/>
     * Label: bitrateType  <br/>
     * Comment: The type of the bitrate. Examples may include CBR and VBR.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Media  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI bitrateType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#bitrateType");
    /**
     * Type: Property <br/>
     * Label: encoding  <br/>
     * Comment: The encoding used for the Embedded File. Examples might include BASE64 or UUEncode  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#EmbeddedFileDataObject  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI encoding = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#encoding");
    /**
     * Type: Property <br/>
     * Label: hasHash  <br/>
     * Comment: Links the file with it's hash value.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#FileDataObject  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#FileHash  <br/>
     */
    public static final URI hasHash = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#hasHash");
    /**
     * Type: Property <br/>
     * Label: codec  <br/>
     * Comment: The name of the codec necessary to decode a piece of media.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Media  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI codec = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#codec");
    /**
     * Type: Property <br/>
     * Label: fileLastModified  <br/>
     * Comment: last modification date  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#FileDataObject  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI fileLastModified = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#fileLastModified");
    /**
     * Type: Property <br/>
     * Label: pageCount  <br/>
     * Comment: Number of pages.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#PaginatedTextDocument  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI pageCount = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#pageCount");
    /**
     * Type: Property <br/>
     * Label: compressionType  <br/>
     * Comment: The type of the compression. Values include, 'lossy' and 'lossless'.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Media  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#CompressionType  <br/>
     */
    public static final URI compressionType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#compressionType");
    /**
     * Type: Property <br/>
     * Label: definesGlobalVariable  <br/>
     * Comment: Name of a global variable defined within the source code file.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#SourceCode  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI definesGlobalVariable = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#definesGlobalVariable");
    /**
     * Type: Property <br/>
     * Label: rearChannels  <br/>
     * Comment: Number of rear channels.  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI rearChannels = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#rearChannels");
    /**
     * Type: Property <br/>
     * Label: bitsPerSample  <br/>
     * Comment: Amount of bits in each audio sample.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI bitsPerSample = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#bitsPerSample");
    /**
     * Type: Property <br/>
     * Label: conflicts  <br/>
     * Comment: States that a piece of software is in conflict with another piece of software.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Software  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Software  <br/>
     */
    public static final URI conflicts = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#conflicts");
    /**
     * Type: Property <br/>
     * Label: duration  <br/>
     * Comment: Duration of a media piece.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Media  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#duration  <br/>
     */
    public static final URI duration = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#duration");
    /**
     * Type: Property <br/>
     * Label: lfeChannels  <br/>
     * Comment: Number of Low Frequency Expansion (subwoofer) channels.  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI lfeChannels = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#lfeChannels");
    /**
     * Type: Property <br/>
     * Label: hasMediaStream  <br/>
     * Comment: Connects a media container with a single media stream contained within.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#Media  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     */
    public static final URI hasMediaStream = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#hasMediaStream");
    /**
     * Type: Property <br/>
     * Label: definesClass  <br/>
     * Comment: Name of a class defined in the source code file.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#SourceCode  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI definesClass = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#definesClass");
    /**
     * Type: Property <br/>
     * Label: isPasswordProtected  <br/>
     * Comment: States if a given resource is password-protected.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#ArchiveItem  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#boolean  <br/>
     */
    public static final URI isPasswordProtected = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#isPasswordProtected");
    /**
     * Type: Property <br/>
     * Label: hashValue  <br/>
     * Comment: The actual value of the hash.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#FileHash  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI hashValue = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#hashValue");
    /**
     * Type: Property <br/>
     * Label: fileSize  <br/>
     * Comment: The size of the file in bytes. For compressed files it means the size of the packed file, not of the contents. For folders it means the aggregated size of all contained files and folders  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#FileDataObject  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI fileSize = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#fileSize");
}
