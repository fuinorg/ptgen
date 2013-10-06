PTGen
=====

**Parameterized Template Generator**


Eclipse plugin for the SrcGen4J [Parameterized Template Generator](https://github.com/fuinorg/srcgen4j-core/tree/master/src/main/java/org/fuin/srcgen4j/core/velocity "Parameterized Template Generator")

__CAUTION: *Project is in early stage*__

To successfully build the project add the following repositories to your [.m2/settings.xml](http://maven.apache.org/settings.html "Maven Settings"):

```xml
<repository>

	<repository>
	    <id>sonatype.oss.snapshots</id>
	    <name>Sonatype OSS Snapshot Repository</name>
	    <url>http://oss.sonatype.org/content/repositories/snapshots</url>
	    <releases>
	        <enabled>false</enabled>
	    </releases>
	    <snapshots>
	        <enabled>true</enabled>
			<updatePolicy>always</updatePolicy>
	    </snapshots>
	</repository>

    <repository>
        <id>eclipse-kepler</id>
        <layout>p2</layout>
        <url>http://download.eclipse.org/releases/kepler</url>
    </repository>

    <repository>
        <id>fuin-org-p2-libraries</id>
        <layout>p2</layout>
        <url>http://www.fuin.org/p2-repository/libraries</url>
    </repository>
    
</repository>
```
