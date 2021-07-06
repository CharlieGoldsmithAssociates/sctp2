# SCTP MIS API

API component of the SCTP2 platform

#### Development

- **API Documentation**: [OpenDoc](https://springdoc.org/) + Swagger UI

#### Building, Configuration & Deployment

Refer to [../README.md](../README.md)

#### API Documentation

We are using the Open API Standard. Spring boot exposes the following URLs:

- `${basepath}/api-doc`

  Outputs the machine-readable API documentation in JSON format. The output can be pasted and viewed/edited in 
  [Swagger Editor](https://editor.swagger.io/) or shared.

- `${basepath}/api-doc.yaml`

   Same as the first one but in YAML format.

- `${basepath}/swagger-ui.html`

   Human-readable comprehensive view of the entire API documentation.

> NOTE 

The above URLs are not accessible by default. To access the URLs, set the following properties to true in your 
configuration file.
 It's recommended to only enable them in development mode.

```yaml
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```