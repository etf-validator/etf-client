# ETF-Client Changelog


## 1.7.2 - 2021-07-15

### Added
- support for setting a Test Run label suffix
- access to the URL of the test run on the remote ETF instance
- close method on the EtfEndpoint interface to explicitly release resources

### Changed
- fix: do not provide AutoClosable interface for observed test runs



## 1.6.11 - 2021-06-09

### Changed
- fix: error when only one ETS and one Translation Template Bundle is installed



## 1.1 - 1.6 internal releases

### Added
- support for Translation Template Bundles
- support for checked Run Parameters



## 1.1.0 - 2020-02-16

### Added
- Access [messages](https://interactive-instruments.github.io/etf-client/javadoc/de/interactive_instruments/etf/client/TestStepResult.html) from Test Steps #2
- Support [Test Run Templates](https://interactive-instruments.github.io/etf-client/javadoc/de/interactive_instruments/etf/client/TestRunTemplate.html)
- Option to configure request [timeout](https://interactive-instruments.github.io/etf-client/javadoc/de/interactive_instruments/etf/client/EtfValidatorClient.html#timeout(java.time.Duration))

### Changed
- fix: parent reference in Translation Templates
- increase default timeout from 2 minutes to 5



## 1.0.1 - 2020-02-11

### Changed
- fix: empty token values in message translations cause exceptions #1



## 1.0.0 - 2019-12-11

- initial release
