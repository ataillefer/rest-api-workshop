<div style="text-align: right;">
  <a href="README.md">Summary</a>
</div>
# Nuxeo REST API Overview

See https://doc.nuxeo.com/nxdoc/rest-api/

```
http://servername:8080/nuxeo/api/v1/*
```

- Complete API accessible via HTTP / HTTPS

- Send HTTP, get JSON

- POST, GET, PUT, DELETE

- Various clients: Java, JavaScript, Python, .NET, etc.

- Playground:

  https://doc.nuxeo.com/nxdoc/use-nuxeo-api-playground-to-discover-the-api/

  https://nuxeo.github.io/api-playground/

## Authentication

### Basic
```
Authorization: Basic QWRtaW5pc3RyYXRvcjpBZG1pbmlzdHJhdG9y
```

### Token based
```
X-Authentication-Token: 1107d714-321a-4f9a-a704-4d862dafeaa5
```

## Endpoints

### Resources

#### Documents
```
/nuxeo/api/v1/id/{docId}
/nuxeo/api/v1/path/{docPath}
```

#### Users
```
/nuxeo/api/v1/user/{userId}
```

#### Queries
```
/nuxeo/api/v1/query?query={query}
/nuxeo/api/v1/query/{pageProviderName}?queryParams={params}
```

### Command: Operation or Chain

```
/nuxeo/api/v1/automation/{operationId}
```

- Use existing operations
  - https://demo.nuxeo.com/nuxeo/site/automation/doc
  - https://nuxeo.github.io/api-playground/#/commands

- Add a custom operation

### Batch Upload

```
/nuxeo/api/v1/upload/{batchId}
```

Full list: https://nightly.nuxeo.com/nuxeo/api/v1/doc/

## Additional Features

### Content Enrichers

Enrich content via request headers
```
GET /nuxeo/api/v1/path/{docPath}
X-NXenrichers.document: breadcrumb
```

### Web Adapters

Transform the resource
```
GET /nuxeo/api/v1/path/{docPath}/@blob/{xpath}/@convert?format=pdf
```

### Pipe a Command Call on a Resource
```
POST /nuxeo/api/v1/path/{docPath}/@op/{operationId}
```

## Error Management

https://doc.nuxeo.com/nxdoc/web-exceptions-errors/
