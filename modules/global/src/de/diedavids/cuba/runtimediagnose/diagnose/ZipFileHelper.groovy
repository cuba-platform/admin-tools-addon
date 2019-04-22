/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.diedavids.cuba.runtimediagnose.diagnose

import groovy.transform.CompileStatic
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@Component
@CompileStatic
class ZipFileHelper {

    String readFileContentFromArchive(String filename, ZipFile diagnoseZipFile) {
        readFileFromArchive(filename, diagnoseZipFile).text
    }

    InputStream readFileFromArchive(String filename, ZipFile diagnoseZipFile) {
        ZipEntry foundFile = diagnoseZipFile.entries().find { ZipEntry zipEntry -> zipEntry.name == filename } as ZipEntry
        foundFile ? diagnoseZipFile.getInputStream(foundFile) : createEmptyInputStream()

    }

    byte[] createZipFileForEntries(Map<String, String> fileEntries) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(byteArrayOutputStream)
        zipOutputStream.method = ZipArchiveOutputStream.STORED
        zipOutputStream.encoding = StandardCharsets.UTF_8.name()

        fileEntries.each { String fileName, String fileContent ->
            addArchiveEntryToZipFile(zipOutputStream, fileName, fileContent?.bytes)
        }

        IOUtils.closeQuietly(zipOutputStream)

        byteArrayOutputStream.toByteArray()

    }

    protected InputStream createEmptyInputStream() {
        new ByteArrayInputStream(''.getBytes('UTF-8'))
    }

    protected void addArchiveEntryToZipFile(ZipArchiveOutputStream zipOutputStream, String fileName, byte[] fileContent) {

        byte[] correctFileContent = fileContent ?: [] as byte[]
        ArchiveEntry resultArchiveEntry = createArchiveEntry(fileName, correctFileContent)

        zipOutputStream.putArchiveEntry(resultArchiveEntry)
        zipOutputStream.write(correctFileContent)
        zipOutputStream.closeArchiveEntry()
    }

    protected ArchiveEntry createArchiveEntry(String name, byte[] data) {
        ZipArchiveEntry zipEntry = new ZipArchiveEntry(name)
        zipEntry.size = data.length
        zipEntry.compressedSize = zipEntry.size
        CRC32 crc32 = new CRC32()
        crc32.update(data,0, data.length)
        zipEntry.crc = crc32.value

        zipEntry
    }
}
