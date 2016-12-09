/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Antoine Taillefer <ataillefer@nuxeo.com>
 */
package org.nuxeo.client.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.NuxeoClient;
import org.nuxeo.client.api.objects.Document;
import org.nuxeo.client.api.objects.Documents;
import org.nuxeo.client.api.objects.Operation;
import org.nuxeo.client.api.objects.blob.Blob;
import org.nuxeo.client.api.objects.upload.BatchFile;
import org.nuxeo.client.api.objects.upload.BatchUpload;
import org.nuxeo.common.utils.FileUtils;

public class TestJavaClient {

    protected static final String NUXEO_URL = "http://localhost:8080/nuxeo";

    protected static final String NUXEO_USER = "Administrator";

    protected static final String NUXEO_PASSWORD = "Administrator";

    protected NuxeoClient nuxeoClient;

    protected String testWorkspaceId;

    @Before
    public void connectAndInit() {
        nuxeoClient = new NuxeoClient(NUXEO_URL, NUXEO_USER, NUXEO_PASSWORD).timeout(60).schemas("dublincore", "file");

        // Create a test workspaces
        Document testWorkspace = new Document("testWorkspace", "Workspace");
        testWorkspace.set("dc:title", "Test Workspace");
        testWorkspace = nuxeoClient.repository().createDocumentByPath("/default-domain/workspaces", testWorkspace);
        assertEquals("/default-domain/workspaces/testWorkspace", testWorkspace.getPath());
        testWorkspaceId = testWorkspace.getId();
    }

    @After
    public void disposeAndCleanUp() {
        nuxeoClient.repository().deleteDocument(testWorkspaceId);
        nuxeoClient.logout();
    }

    @Test
    public void testDocuments() throws InterruptedException {

        // Create a Folder document by parent path
        Document document = new Document("newFolder", "Folder");
        document.set("dc:title", "The new folder");
        document.set("dc:description", "Folder created via the REST API");
        document = nuxeoClient.repository().createDocumentByPath("/default-domain/workspaces/testWorkspace", document);
        assertNotNull(document);
        assertEquals("document", document.getEntityType());
        assertEquals("Folder", document.getType());
        assertEquals("/default-domain/workspaces/testWorkspace/newFolder", document.getPath());
        assertEquals("The new folder", document.getTitle());
        assertEquals("The new folder", document.get("dc:title"));
        assertEquals("Folder created via the REST API", document.get("dc:description"));

        // Create a File document by parent id
        // TODO

        // Get a document by id
        // TODO

        // Get a document by path
        // TODO

        // Update a document
        // TODO

        // Delete a document
        // TODO
    }

    @Test
    public void testQueries() {
        // Create 2 File documents
        nuxeoClient.repository().createDocumentById(testWorkspaceId, new Document("file1", "File"));
        nuxeoClient.repository().createDocumentById(testWorkspaceId, new Document("file2", "File"));

        // Get descendants of Workspaces
        String query = "select * from Document where ecm:path startswith '/default-domain/workspaces'";
        Documents documents = nuxeoClient.repository().query(query, "10", "0", "50", "ecm:path", "asc", null);
        assertEquals(3, documents.size());

        // Get all the File documents
        // TODO

        // Content of a given Folder, using a page provider
        // TODO
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOperations() throws IOException {
        // Get an operation's description
        String response = nuxeoClient.get(NUXEO_URL + "/site/automation/Repository.GetDocument").body().string();
        assertEquals("Repository.GetDocument",
                ((Map<String, Object>) nuxeoClient.getConverterFactory().readJSON(response, Map.class)).get("id"));

        // Fetch a document
        // TODO
    }

    @Test
    public void testEnrichers() {
        // Define custom enrichers
        nuxeoClient = new NuxeoClient(NUXEO_URL, NUXEO_USER, NUXEO_PASSWORD).timeout(60)
                                                                            .schemas("dublincore")
                                                                            .enrichers("breadcrumb", "children");

        // Create 2 File documents
        nuxeoClient.repository().createDocumentById(testWorkspaceId, new Document("file1", "File"));
        nuxeoClient.repository().createDocumentById(testWorkspaceId, new Document("file2", "File"));

        // Check breadcrumb and children enrichers on a document
        // TODO
    }

    @Test
    public void testBatchUpload() {
        // Upload a file
        BatchUpload batchUpload = nuxeoClient.fetchUploadManager();
        assertNotNull(batchUpload);
        assertNotNull(batchUpload.getBatchId());
        File file = FileUtils.getResourceFileFromContext("sample.jpg");
        batchUpload = batchUpload.upload(file.getName(), file.length(), "jpg", batchUpload.getBatchId(), "0", file);
        assertNotNull(batchUpload);
        assertEquals(ConstantsV1.UPLOAD_NORMAL_TYPE, batchUpload.getUploadType());

        // Check the file in the batch
        BatchFile batchFile = batchUpload.fetchBatchFile("0");
        assertNotNull(batchFile);
        assertEquals(file.getName(), batchFile.getName());
        assertEquals(ConstantsV1.UPLOAD_NORMAL_TYPE, batchFile.getUploadType());

        // Upload another file and check batch files
        file = FileUtils.getResourceFileFromContext("blob.json");
        batchUpload.upload(file.getName(), file.length(), "json", batchUpload.getBatchId(), "1", file);
        List<BatchFile> batchFiles = batchUpload.fetchBatchFiles();
        assertNotNull(batchFiles);
        assertEquals(2, batchFiles.size());
        assertEquals("sample.jpg", batchFiles.get(0).getName());
        assertEquals("blob.json", batchFiles.get(1).getName());
    }

    @Test
    public void testBatchUploadChunks() {
        // Upload file chunks
        BatchUpload batchUpload = nuxeoClient.fetchUploadManager().enableChunk();
        assertNotNull(batchUpload);
        File file = FileUtils.getResourceFileFromContext("sample.jpg");
        batchUpload = batchUpload.upload(file.getName(), file.length(), "jpg", batchUpload.getBatchId(), "0", file);
        assertNotNull(batchUpload);
        assertEquals(ConstantsV1.UPLOAD_CHUNKED_TYPE, batchUpload.getUploadType());

        // Check the file
        BatchFile batchFile = batchUpload.fetchBatchFile("0");
        assertNotNull(batchFile);
        assertEquals(file.getName(), batchFile.getName());
        assertEquals(ConstantsV1.UPLOAD_CHUNKED_TYPE, batchFile.getUploadType());
        assertEquals(file.length(), batchFile.getSize());
        assertEquals(4, batchFile.getChunkCount());
        assertEquals(batchFile.getChunkCount(), batchFile.getUploadedChunkIds().length);
    }

    @Test
    public void testAttachBatchFilesToADoc() {
        // Upload a file
        BatchUpload batchUpload = nuxeoClient.fetchUploadManager();
        assertNotNull(batchUpload);
        File file = FileUtils.getResourceFileFromContext("sample.jpg");
        batchUpload = batchUpload.upload(file.getName(), file.length(), "jpg", batchUpload.getBatchId(), "0", file);
        assertNotNull(batchUpload);

        // Get a document and attach the batch file to it
        Document doc = new Document("file", "File");
        doc.setPropertyValue("dc:title", "new title");
        doc = nuxeoClient.repository().createDocumentById(testWorkspaceId, doc);
        assertNotNull(doc);
        doc.setPropertyValue("file:content", batchUpload.getBatchBlob());
        doc = doc.updateDocument();
        assertEquals("sample.jpg", ((Map) doc.get("file:content")).get("name"));
    }

    @Test
    public void testExecuteOperation() {
        // Upload a file
        BatchUpload batchUpload = nuxeoClient.fetchUploadManager();
        assertNotNull(batchUpload);
        File file = FileUtils.getResourceFileFromContext("sample.jpg");
        batchUpload = batchUpload.upload(file.getName(), file.length(), "jpg", batchUpload.getBatchId(), "0", file);
        assertNotNull(batchUpload);

        // Get a document and attach the batch file to it
        Document doc = new Document("file", "File");
        doc.setPropertyValue("dc:title", "new title");
        doc = nuxeoClient.repository().createDocumentById(testWorkspaceId, doc);
        assertNotNull(doc);
        Operation operation = nuxeoClient.automation("Blob.AttachOnDocument").param("document", doc);
        Blob blob = (Blob) batchUpload.execute(operation);
        assertNotNull(blob);
    }

}
