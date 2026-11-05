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
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.eclipse.sirius.web.emf.generator.internal.GenmodelFinder;

/**
 * Checks stable GenModel discovery while excluding compiled resources.
 *
 * @author lfasani
 */
public class GenmodelFinderTest {
    @TempDir
    private Path temporaryFolder;

    @Test
    public void findsMatchingGenmodelsInStableOrder() throws Exception {
        Path root = Files.createDirectory(this.temporaryFolder.resolve("repository"));
        Path source = Files.createDirectories(root.resolve("module/src/main"));
        Files.writeString(source.resolve("b.genmodel"), "");
        Files.writeString(source.resolve("a.genmodel"), "");
        Files.writeString(root.resolve("ignored.genmodel"), "");

        List<Path> found = new GenmodelFinder().find(root, "**/src/**.genmodel");

        assertThat(found).containsExactly(source.resolve("a.genmodel"), source.resolve("b.genmodel"));
    }

    @Test
    public void ignoresCompiledGenmodels() throws Exception {
        Path root = Files.createDirectory(this.temporaryFolder.resolve("repository"));
        Path source = Files.createDirectories(root.resolve("module/src/main/resources/model"));
        Path compiled = Files.createDirectories(root.resolve("module/target/classes/model"));
        Path sourceGenmodel = Files.writeString(source.resolve("domain.genmodel"), "");
        Files.writeString(compiled.resolve("domain.genmodel"), "");

        List<Path> found = new GenmodelFinder().find(root, "**/src/**.genmodel");

        assertThat(found).containsExactly(sourceGenmodel);
    }
}
