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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.io.File;

@Configuration
public class DatastoreConfiguration {
    private final File stagingDirectory;
    private final File recipientPhotoDirectory;

    public DatastoreConfiguration(
            @NotNull
            @Value("${targeting.pictures:data/recipient_photos}") File imageDir,
            @NotNull
            @Value("${imports.staging:data/imports/staging}") File stagingDir) {
        imageDir = imageDir.getAbsoluteFile().toPath().normalize().toFile().getAbsoluteFile();
        stagingDir = stagingDir.getAbsoluteFile().toPath().normalize().toFile().getAbsoluteFile();
        this.stagingDirectory = initializeDirectory(stagingDir);
        this.recipientPhotoDirectory = initializeDirectory(imageDir);
    }

    private File initializeDirectory(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            if (!directory.mkdirs()) {
                throw new RuntimeException("Failed to initialize directory " + directory);
            }
        }
        return directory;
    }

    public File getStagingDirectory() {
        return stagingDirectory;
    }

    public File getRecipientPhotoDirectory() {
        return recipientPhotoDirectory;
    }
}
