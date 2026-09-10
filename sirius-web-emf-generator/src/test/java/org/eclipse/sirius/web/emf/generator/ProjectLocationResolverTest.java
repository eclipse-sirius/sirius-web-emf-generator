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

import org.eclipse.sirius.web.emf.generator.internal.ProjectLocation;
import org.eclipse.sirius.web.emf.generator.internal.ProjectLocationResolver;

/**
 * Checks project-root discovery and rejection of GenModels outside source folders.
 *
 * @author lfasani
 */
public class ProjectLocationResolverTest {
    private static final String GENMODEL_FILENAME = "model.genmodel";

    private static final String PROJECT_NAME = "my-project";

    @TempDir
    private Path temporaryFolder;

    @Test
    public void resolvesProjectLocationWithoutAssumingRepositoryFolders() throws Exception {
        Path root = Files.createDirectory(this.temporaryFolder.resolve("repository"));
        Path genmodel = Files.createDirectories(root.resolve("any/nesting/my-project/src/main/resources"))
                .resolve(GENMODEL_FILENAME);

        ProjectLocation location = new ProjectLocationResolver().resolve(root, genmodel);

        assertThat(location.projectName()).isEqualTo(PROJECT_NAME);
        assertThat(location.projectRelativePath()).isEqualTo("src/main/resources/model.genmodel");
        assertThat(location.projectRoot()).isEqualTo(root.resolve("any/nesting/my-project"));
    }

    @Test
    public void returnsNullOutsideRepository() throws Exception {
        Path root = Files.createDirectory(this.temporaryFolder.resolve("repository"));
        assertThat(new ProjectLocationResolver().resolve(root, Path.of("C:/outside/model.genmodel"))).isNull();
    }

    @Test
    public void resolvesProjectDirectlyBelowTheRepositoryRoot() throws Exception {
        Path root = Files.createDirectory(this.temporaryFolder.resolve("arbitrary-root"));
        Path genmodel = Files.createDirectories(root.resolve("metamodel-mm/src/main/resources/model"))
                .resolve("metamodel.genmodel");

        ProjectLocation location = new ProjectLocationResolver().resolve(root, genmodel);

        assertThat(location.projectName()).isEqualTo("metamodel-mm");
        assertThat(location.projectRelativePath()).isEqualTo("src/main/resources/model/metamodel.genmodel");
        assertThat(location.projectRoot()).isEqualTo(root.resolve("metamodel-mm"));
    }

    @Test
    public void resolvesTheRepositoryRootWhenItIsTheProject() throws Exception {
        Path projectRoot = Files.createDirectory(this.temporaryFolder.resolve(PROJECT_NAME));
        Path genmodel = Files.createDirectories(projectRoot.resolve("src/main/resources/model"))
                .resolve(GENMODEL_FILENAME);

        ProjectLocation location = new ProjectLocationResolver().resolve(projectRoot, genmodel);

        assertThat(location.projectName()).isEqualTo(PROJECT_NAME);
        assertThat(location.projectRelativePath()).isEqualTo("src/main/resources/model/model.genmodel");
        assertThat(location.projectRoot()).isEqualTo(projectRoot);
    }

    @Test
    public void returnsNullWhenTheGenmodelIsNotBelowASourceFolder() throws Exception {
        Path root = Files.createDirectory(this.temporaryFolder.resolve("root-without-source-folder"));
        Path genmodel = Files.createDirectories(root.resolve("my-project/model"))
                .resolve(GENMODEL_FILENAME);

        assertThat(new ProjectLocationResolver().resolve(root, genmodel)).isNull();
    }
}
