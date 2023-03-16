# ETF-Client Changelog

## 1.9.1 - 2022-03-16

### Changed
- fix: Eid Mapping

## 1.9.0 - 2022-12-15

### Changed
- feat: support status query of tmp and persistent data space

## 1.8.0 - 2022-12-13

### Added
- feat: retryOnConnectionReset

## 1.7.6 - 2022-12-09

### Changed
- fix: possible deadlock on endpoint close()


## 1.7.5 - 2022-05-17

### Changed
- fix: ReferenceError not thrown

## 1.7.4 - 2021-09-03

### Added
- CD Test driver support

## 1.7.3 - 2021-07-16

### Added
- support for setting a Test Run label suffix
- access to the URL of the test run on the remote ETF instance
- close method on the EtfEndpoint interface to explicitly release resources

### Changed
- fix: do not provide AutoClosable interface for observed test runs
- fix: check that at least Java Version 11.0.8 is used



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
