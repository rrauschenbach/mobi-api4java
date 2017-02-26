## Introduction
As the name suggest, mobi-api4java is a java api to read, write and modify mobipocket (.mobi, .azw) files.

At the current state the following features are supported.
  * Access, change and add metadata
  * Access and change the cover and thumbnail images
  * Make changes to the text of the book.
  
Missing features.
  * The books index is read but can not be accessed or changed.
  * After changing the books text it is stored uncompressed. 

## Get mobi-api4java
### Download
Download the latest jar from the [github maven repository](https://github.com/rrauschenbach/mobi-api4java/tree/mvn-repo/org/rr/mobi-api4java/0.0.2-SNAPSHOT). 

### Maven
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
	// do some changes to the mobi document
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

Metadata can be be changed by setting a new value to them.
```java
	// get the author record and set a new value.
	mobiDoc.getMetaData().getAuthorRecords().setStringData("Name of author");
```

Also new metadata can be added to a document.
```java
	// create a new record
	EXTHRecord record = EXTHRecordFactory.createEXTHRecord(RECORD_TYPE.AUTHOR);
	
	// wrap the record into a StringRecordDelegate which makes it easier to use.
	StringRecordDelegate stringRecord = new StringRecordDelegate(record);
	stringRecord.setStringData("Name of author");
	
	// add the metadata record to the document.
	mobiDoc.getMetaData().addEXTHRecord(stringRecord);
```
### 