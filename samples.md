<div style="text-align: right;">
  <a href="README.md">Summary</a>
</div>
# Nuxeo REST API Samples

## Environment

**Server**: DEV instance or local instance: http://localhost:8080/nuxeo/

**Client**:
  - Firefox RESTClient / Chrome Postman
  - curl: https://doc.nuxeo.com/nxdoc/using-curl/

## Resource Endpoints

https://doc.nuxeo.com/nxdoc/rest-api-entity-types/

https://doc.nuxeo.com/nxdoc/document-resources-endpoints/

### Path and Id

**Create a Folder document**
```
POST /nuxeo/api/v1/path/default-domain/workspaces
Content-Type: application/json
{
  "entity-type": "document",
  "name": "newFolder",
  "type": "Folder",
  "properties": {
    "dc:title": "The new folder",
    "dc:description": "Folder created via the REST API"
  }
}
```

**Create a File document**
```
POST /nuxeo/api/v1/path/default-domain/workspaces/newFolder
Content-Type: application/json
{
  "entity-type": "document",
  "name":"newFile",
  "type": "File",
  "properties": {
    "dc:title": "The new file",
    "dc:description": "File created via the REST API"
  }
}
```

**Get a Document by Path or Id**
```
GET /nuxeo/api/v1/path/default-domain/workspaces/newFolder/newFile

GET /nuxeo/api/v1/id/a2ba2ae3-7ec2-4ab1-8cf1-76bda01af86d
```
Request additional schemas:
```
X-NXProperties: dublincore[,uid,...]
X-NXProperties: *
```

**Delete a Document**
```
DELETE /nuxeo/api/v1/path/default-domain/workspaces/newFolder/newFile
```

**Update a File Document**
```
PUT /nuxeo/api/v1/path/default-domain/workspaces/newFolder/newFile
Content-Type: application/json
{
  "entity-type": "document",
  "properties": {
    "dc:title": "The updated file",
    "dc:description": "File updated via the REST API"
  }
}
```

### Query

https://doc.nuxeo.com/nxdoc/query-endpoint/

**Descendants of Workspaces**
```
GET /nuxeo/site/api/v1/query?query=select * from Document
    where ecm:path startswith '/default-domain/workspaces'
    order by ecm:path&pageSize=2&currentPageIndex=0
```

**All the File documents**
```
GET /nuxeo/site/api/v1/query?query=select * from Document where ecm:primaryType = 'File'
```

**Content of a given Folder, using a page provider**
```
GET /nuxeo/site/api/v1/query/document_content?queryParams=c1cb005a-16b3-41bc-a0d8-0e2c3bbb2cae
```

## Command Endpoint: Using Automation

https://doc.nuxeo.com/nxdoc/command-endpoint/

https://doc.nuxeo.com/nxdoc/filtering-exposed-operations/

**List all available operations**
```
GET /nuxeo/site/automation
```

**Get an operation's description**
```
GET /nuxeo/site/automation/Repository.GetDocument
```

**Fetch a Document**
```
POST /nuxeo/site/automation/Repository.GetDocument
Content-Type: application/json
{
  "params": {
    "value": "/default-domain/workspaces/newFolder/newFile"
  },
  "context": {}
}
```

## Batch Upload

https://doc.nuxeo.com/nxdoc/blob-upload-for-batch-processing/

https://doc.nuxeo.com/nxdoc/how-to-upload-a-file-in-nuxeo-platform-using-rest-api-batch-processing-endpoint/



## Extended Features

### Enrichers

https://doc.nuxeo.com/nxdoc/content-enricher/

```
X-NXenrichers.document: enricher1,enricher2,enricher3

/nuxeo/site/api/v1/path/default-domain/workspaces?enrichers.document=enricher1,enricher2
```

Default enrichers:
- breadcrumb
- permissions
- acls
- children
- preview
- thumbnail
- favorites
- ...

**Get a folder with its breadcrumb and children**
```
GET /nuxeo/api/v1/path/default-domain/workspaces/newFolder
X-NXenrichers.document: breadcrumb,children
```

### Web Adapters

https://doc.nuxeo.com/nxdoc/web-adapters-for-the-rest-api/

```
/nuxeo/api/v1/id/{docId}/@adapter/parameters

/nuxeo/api/v1/path/{documentPath}/@adapter/parameters
```

Default web adapters:
- acls- audit
- blob
- children
- convert
- pp
- search
- ...

**Getting the children of a given document**
```
GET /nuxeo/site/api/v1/path/default-domain/workspaces/newFolder/@children?currentPageIndex=0&pagesize=20&maxResults=100
```

**Searching documents by fulltext**
```
GET /nuxeo/site/api/v1/path/default-domain/workspaces/newFolder/@search?fullText=nuxeo&orderBy=dc:title
```

**Bridging Operations and Automation Chains**
```
POST /nuxeo/site/api/v1/path/default-domain/workspaces/newFolder/newFile/@op/Document.SetProperty
{
  "params": {
    "xpath": "dc:description",
    "value": "Updated description"
  }
}
```

**Piping**
```
GET /nuxeo/api/v1/path/default-domain/workspaces/newFolder/newFile/@blob/file:content/@convert?format=pdf
```

### Special HTTP Headers

https://doc.nuxeo.com/nxdoc/special-http-headers/
