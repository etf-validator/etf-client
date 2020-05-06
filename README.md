# ETF Java Client Library

[![European Union Public Licence 1.2](https://img.shields.io/badge/license-EUPL%201.2-blue.svg)](https://joinup.ec.europa.eu/software/page/eupl)
[![API JavaDoc](http://img.shields.io/badge/JavaDoc-API-green.svg)](https://etf-validator.github.io/etf-client/javadoc/index.html)


The ETF Java Client library provides functionality to:

- query the available Executable Test Suites
- filter the Executable Test Suites by Tags
- create Ad hoc test objects
- start, monitor and control test runs
- query the results in the ETF results structure, including translated messages

## Quickstart

```JAVA
// requires etf.client;
import de.interactive_instruments.etf.client.*;

// Create an endpoint object for the remote ETF instance
final EtfEndpoint etfEndpoint = EtfValidatorClient.create()
	.url(new URL("http://SERVICE/etf-webapp"))
	.locale(Locale.ENGLISH)
	.init();

// Get metadata Tag
final Tag metadataTag = etfEndpoint.tags().itemByLabel("Metadata (TG version 2.0) - BETA").get();

// Get a collection of Executable Test Suites with the Metadata Tag
final EtsCollection metadataTestSuites = etfEndpoint.executableTestSuites().itemsByTag(metadataTag);

// Create a new Test Object
final String METADATA_TEST_URL =
		"https://raw.githubusercontent.com/jonherrmann/community/master/examples/Dataset_metadata_2.0_example.xml";
final AdHocTestObject testObject = etfEndpoint.newAdHocTestObject().fromDataSet(new URL(METADATA_TEST_URL));

// Start the Test Run
final TestRun testRun = metadataTestSuites.execute(testObject);

// Wait until the Test Run finishes and get the result (blocking call)
final TestRunResult result = testRun.result();

// Output the results
for (final TestResult testResult : result) {
	System.out.println(testResult.label()+" - "+testResult.resultStatus());
}

```

See the API [documentation](https://etf-validator.github.io/etf-client/javadoc/index.html) for additional details.

## Mapping the results to your own model

### ETF result structure

The results are available in a hierarchical structure.

- Test Run Result : the root level.
- Test Modules: for some ETS types they are hidden in the HTML report.
The ETF Java Client library will skip these items, if they are traversed using the
[Iterable](https://docs.oracle.com/javase/8/docs/api/java/lang/Iterable.html) interface.
- Test Cases
- Test Steps: Based on the ETS type, these can also be hidden or
possess additional information that are attached to it.
- Test Assertions: the lowest level result element representing the result of an atomic test.

The result items implement the Iterable interface. The call at a certain level of the result
structure includes all sub-elements in the result during iteration.

### Result Status Codes

The result status codes are describe
[here](https://etf-validator.github.io/etf-client/javadoc/de/interactive_instruments/etf/client/ResultStatus.html#PASSED).


## Getting the library

The library is written in Java 11. The latest version can be downloaded using the etf snapshot repository.

### Gradle dependency configuration

```groovy
repositories {
	maven {
		url "https://services.interactive-instruments.de/etfdev-af/snapshot"
		// our repository requires authentication
		credentials {
			username 'etf-public-dev'
			password 'etf-public-dev'
		}
	}
}
// ...
dependencies {
	implementation group: 'de.interactive_instruments.etf', name: 'etf-client', version: '1.1.1-SNAPSHOT'
}
```

### Maven dependency configuration

```xml
<project>
<!-- ... -->
	<repositories>
		<repository>
		<id>etf-repo</id>
		<url>https://services.interactive-instruments.de/etfdev-af/snapshot</url>
		</repository>
	</repositories>
	<!-- ... -->
	<dependencies>
		<dependency>
		<groupId>de.interactive_instruments.etf</groupId>
		<artifactId>etf-client</artifactId>
		<version>1.1.1-SNAPSHOT</version>
		</dependency>
	</dependencies>
<!-- ... -->
</project>
```

settings.xml :

```xml
<settings>
	<servers>
		<server>
			<id>etf-repo</id>
			<username>etf-public-dev</username>
			<password>etf-public-dev</password>
		</server>
	</servers>
</settings>
```

## Sponsors

This library was funded by [terrestris](https://www.terrestris.de/en/) and the
german [Federal Agency for Cartography (BKG)](https://www.bkg.bund.de/EN/Home/home.html) as part of the development of the GDI-DE Testsuite.
