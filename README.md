# SCTP-2

## Project Objectives

* The Social Cash Transfer Programme aims to reduce poverty and hunger, and increase school enrolment.
* It is designed to provide bi-monthly unconditional cash transfers to more than 300,000 households categorised as poor, severely impoverished and labour constrained.
* The Management Information System aims to support new and existing operational needs of the programme.

## Environment Requirements

- **Runtime Environment**: [Java 16](https://adoptopenjdk.net/?variant=openjdk16&jvmVariant=hotspot)
- **Database**: [MySQL 8](https://dev.mysql.com/downloads/mysql/)

## Project Sub-modules

This is a multi-mode maven project.

1. [sctp-core](/sctp-core)
   
   This component contains core/common models and services used by all dependent components.
   
2. [sctp-api](/sctp-api)

    API component.
    
3. [sctp-mis](/sctp-mis)

    Management Information System component for system administration, reporting, and monitoring.

### Building

To quickly build the application, type ``mvn clean package``. A Jar file will be created under the ``target/`` directory.
To skip running tests during build, use ``mvn -DskipTests clean package``.

### Deployment

#### Requirements

- **Runtime Environment**: [Java 16](https://adoptopenjdk.net/?variant=openjdk16&jvmVariant=hotspot)
- **Database**: [MySQL 8](https://dev.mysql.com/downloads/mysql/)

### Known MySQL Issues

- You must enable binary logging before running/deploying any of the binaries (MIS or API) against a fresh database.
To do so, run the following with a root account

```mysql-sql
SET GLOBAL log_bin_trust_function_creators = 1;
```

This is explained here for more information https://dev.mysql.com/doc/refman/8.0/en/stored-programs-logging.html

#### Configuration

Spring supports different configuration methods. The most widely used method is to create a configuration file
that corresponds to the target profile of deployment.

#### Running

The command to run the application: ``java -jar mis-api-x.x.x.jar -spring.profiles.active=prod``. The application will look for a file named
`application-prod.properties` or `application-prod.yaml` for the production profile.

To run the application using any other profile (dev, test, etc), you can specify the profile using the
following command: `java -jar mis-api-x.x.x.jar -Dspring.profiles.active=dev`. Again, this assumes that you have
a file name `application-dev.yaml` in the same directory as the application.

It is also possible to use a custom profile name, i.e `java -jar mis-api-x.x.x.jar -Dspring.profiles.active=foo`, as
long as there's a corresponding configuration file.

Again, this is one of the many ways of configuring a Spring Boot application, which allows the build, configuration,
and deployment to be automated (DevOps).

To use externalized properties (i.e environment variables), you can do this in your configuration file:

```yaml
property:
    foo: some-literal1
    key3: "another literal2"
    bar: ${SOME_ENV_VAR}
```

For more Spring Boot configuration reference, see:

- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/2.4.5/reference/html/index.html)
- [Common Application properties](https://docs.spring.io/spring-boot/docs/2.4.5/reference/html/appendix-application-properties.html#common-application-properties)

## License

```text
Copyright (C) 2021 CGA Technologies, a trading name of Charlie Goldsmith Associates Ltd
All rights reserved, released under the BSD-3 licence.

CGA Technologies develop and use this software as part of its work but the software 
itself is open-source software; you can redistribute it and/or modify it under the 
terms of the BSD licence below:

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

For more information please see http://opensource.org/licenses/BSD-3-Clause
```
