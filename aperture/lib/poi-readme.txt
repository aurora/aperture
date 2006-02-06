The POI jar files were created on Feb. 6th, 2005, SVN revision 375241, with
jdk.version.source and jdk.version.target in the build file set to Java 1.4.

The reason for building our own jar files is that the official builds
(version 2.5.1-final) did not include all classed used in Aperture (esp. in
the implementation of our WordExtractor) and these classes, although part of
the source tree at the time of the 2.5.1 release, were also not part of this
release, i.e. they had no 2.5.1-related tag.

The created jar files were renamed to include the SVN revision number
to ensure full recreatability.

Tests on a set of about 300 arbitrary Word documents indicated no
differences in output between this development code and the last official
release. Even the set of documents on which POI chokes was the same.
Finally, all POI unit tests passed. This gives us enough confidence in the
quality of this code, compared to the last official release.

--
