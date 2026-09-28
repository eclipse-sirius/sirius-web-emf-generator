/*******************************************************************************
 * Copyright (c) 2026 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/

package org.eclipse.sirius.web.emf.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.eclipse.sirius.web.emf.generator.internal.GeneratedEditProjectCleaner;
import org.eclipse.sirius.web.emf.generator.internal.GeneratedModelProjectCleaner;

/**
 * Checks resource preservation and removal of generated Eclipse project metadata.
 *
 * @author lfasani
 */
public class GeneratedEditProjectCleanerTest {
    private static final String PLUGIN_PROPERTIES = "plugin.properties";

    private static final String BUILD_PROPERTIES = "build.properties";

    private static final String PLUGIN_XML = "plugin.xml";

    @TempDir
    private Path temporaryFolder;

    @Test
    public void mergesPropertiesInitializesIconsAndRemovesGeneratedMetadata() throws Exception {
        Path project = Files.createDirectory(this.temporaryFolder.resolve("edit"));
        Files.writeString(project.resolve(PLUGIN_PROPERTIES), "existing=generated\nadded=value\n");
        Files.createDirectories(project.resolve("src/main/resources/icons"));
        Files.writeString(project.resolve("src/main/resources/plugin.properties"), "existing=custom");
        Files.createDirectories(project.resolve("icons/nested"));
        Files.writeString(project.resolve("icons/nested/icon.gif"), "gif");
        Files.writeString(project.resolve(BUILD_PROPERTIES), "");
        Files.writeString(project.resolve(PLUGIN_XML), "");

        new GeneratedEditProjectCleaner().cleanup(project);

        String properties = Files.readString(project.resolve("src/main/resources/plugin.properties"));
        assertThat(properties).contains("existing=custom");
        assertThat(properties).contains("added=value");
        assertThat(project.resolve("src/main/resources/icons/nested/icon.svg")).isRegularFile();
        assertThat(project.resolve("icons")).doesNotExist();
        assertThat(project.resolve(BUILD_PROPERTIES)).doesNotExist();
        assertThat(project.resolve(PLUGIN_XML)).doesNotExist();
        assertThat(project.resolve(PLUGIN_PROPERTIES)).doesNotExist();
    }

    @Test
    public void removesGeneratedModelMetadata() throws Exception {
        Path project = Files.createDirectory(this.temporaryFolder.resolve("model"));
        Files.writeString(project.resolve(BUILD_PROPERTIES), "");
        Files.writeString(project.resolve(PLUGIN_XML), "");
        Files.writeString(project.resolve(PLUGIN_PROPERTIES), "");

        new GeneratedModelProjectCleaner().cleanup(project);

        assertThat(project.resolve(BUILD_PROPERTIES)).doesNotExist();
        assertThat(project.resolve(PLUGIN_XML)).doesNotExist();
        assertThat(project.resolve(PLUGIN_PROPERTIES)).doesNotExist();
    }
}
