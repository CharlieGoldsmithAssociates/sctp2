This is the core component of the SCTP 2 platform. All dependent projects will have to meet the 
    specified requirements.

#### Requirements

- **Runtime Environment**: [Java 16](https://adoptopenjdk.net/?variant=openjdk16&jvmVariant=hotspot)
- **Database**: [MySQL 8](https://dev.mysql.com/downloads/mysql/)

## Development

- **Language**: Java

#### Flyway database migration file naming strategy

All database migrations are handled in this component

Flyway `out of order` option is on by default. Use the following naming convention:
`Vmajor.minor.patch.timestampMilliseconds__Description_of_migration.sql`. The unix timestamp value can be obtained from running
the following in *jshell* console: ``System.currentTimeMillis()``
or by using this [HTML file](tools/dbMigrationTimestampGenerator.html).

Using timestamps prevents potential merge conflicts.

When creating views, tables, functions and procedures, make sure to check for their existence first.
The idea is to make migrations idempotent and avoid migration conflicts.