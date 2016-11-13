As the name suggest, mobi-api4java is a java api to read, write and modify mobipocket (.mobi, .azw) files.

## Maven
Add the following to your pom to make mobi-api4java available in your project.
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
The MobiReader and MobiWriter can be used to read and write mobi files.  
```java
	MobiDocument mobiDoc = new MobiReader().read(new File("/tmp/sample.mobi"));
	new MobiWriter().write(mobiDoc, new File("/tmp/sample_edit.mobi"));
```
