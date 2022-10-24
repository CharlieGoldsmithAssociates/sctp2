/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2022, CGATechnologies
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.cga.sctp.data;

import lib.gintec_rdl.spector.Spector;
import lib.gintec_rdl.spector.TypeInfo;
import org.cga.sctp.core.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This service validates uploaded files
 */
@Service
public class UploadFileValidator extends BaseService {

    private final Path root;

    public UploadFileValidator() {
        root = Path.of(System.getProperty("java.io.tmpdir"), "sctp_uploads").normalize().toAbsolutePath();
        File rootFile = root.toFile();
        if (!rootFile.exists()) {
            if (!rootFile.mkdirs()) {
                throw new RuntimeException(format("failed to initialize tmp directory for file validation: %s", rootFile));
            }
        }
    }

    public static class UploadedFile {
        /**
         * <p>Path to where the file has been</p>
         */
        private final File file;
        private boolean staged;
        private TypeInfo typeInfo;

        UploadedFile(File file) {
            this.file = file;
        }

        public boolean delete() {
            return file.delete();
        }

        public boolean isStaged() {
            return staged;
        }

        public File getFile() {
            return file;
        }

        public void transferTo(File file) throws IOException {
            this.transferTo(file.getAbsoluteFile().toPath().normalize().toAbsolutePath());
        }

        public void transferTo(Path path) throws IOException {
            Path src = file.toPath().normalize().toAbsolutePath();
            Files.copy(src, path, StandardCopyOption.REPLACE_EXISTING);
        }

        public synchronized boolean hasType(String... types) {
            if (typeInfo == null) {
                typeInfo = Spector.inspect(file);
            }
            if (typeInfo == null) {
                return false;
            }
            for (String type : types) {
                if (typeInfo.getMime().equalsIgnoreCase(type)) {
                    return true;
                }
            }
            return false;
        }

        public TypeInfo getTypeInfo() {
            return typeInfo;
        }

        @Override
        public String toString() {
            return "{file: " + file.getAbsolutePath() + ", typeInfo: "
                    + (typeInfo != null ? typeInfo.toString() : "<null>") + "}";
        }
    }

    public Map<String, UploadedFile> convertMultipartFiles(Map<String, MultipartFile> files) {
        Map<String, UploadedFile> uploadedFiles = new LinkedHashMap<>();
        for (String name : files.keySet()) {
            MultipartFile file = files.get(name);
            uploadedFiles.put(name, convertMultipartFile(file));
        }
        return uploadedFiles;
    }

    public UploadedFile convertMultipartFile(MultipartFile file) {
        String filename = format("%d_%d.tmp", Thread.currentThread().getId(), System.currentTimeMillis());
        Path path = root.resolve(filename);
        UploadedFile uploadedFile = new UploadedFile(path.toFile());
        try {
            file.transferTo(root.resolve(filename));
            uploadedFile.staged = true;
        } catch (Exception e) {
            uploadedFile.staged = false;
            LOG.error("Error copying multipart file to validation directory", e);
        }
        return uploadedFile;
    }
}
