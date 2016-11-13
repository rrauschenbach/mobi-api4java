As the name suggest, mobi-api4java is a java api to read, write and modify mobipocket (.mobi, .azw) files.

## Maven
Add the following to your pom to make mobi-api4java available in your project. Please take a look to the branch `mvn-repro` for the latest version.
```xml
	<repository>
		<id>mobi-api4java-mvn-repo</id>
		<url>https://raw.github.com/rrauschenbach/mobi-api4java/mvn-repo/</url>
		<snapshots>
			<enabled>true</enabled>
			<updatePolicy>always</updatePolicy>
		</snapshots>
	</repository>
	
	<dependency>
		<groupId>org.rr</groupId>
		<artifactId>mobi-api4java</artifactId>
		<version>0.0.1-SNAPSHOT</version>
	</dependency>
```


## Getting started
### Reading and writing mobi files
The MobiReader and MobiWriter classes can be used to read and write mobi files.  
```java
	MobiDocument mobiDoc = new MobiReader().read(new File("/tmp/sample.mobi"));
	new MobiWriter().write(mobiDoc, new File("/tmp/sample_edit.mobi"));
```
### Dealing with metadata
Use `MobiDocument.getMetaData()` to get the metadata from the mobipocket document. If possible, use the methods returning some RecordDelegate implementations instead of using the low level `getEXTHRecords()` method. 
Because the mobipocket format isn't documented it could be necessary to make use of it but be aware that it's possible to get an invalid mobipocket file when putting some wired data in there.     
```java
	// get the author
	mobiDoc.getMetaData().getAuthorRecords()
	// get the isbn
	mobiDoc.getMetaData().getISBNRecords()
	// get the publisher
	mobiDoc.getMetaData().getPublisherRecords()
	
	// and so on
```
### 