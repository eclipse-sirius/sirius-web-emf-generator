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

import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.eclipse.sirius.web.emf.generator.internal.EditProjectLocation;
import org.eclipse.sirius.web.emf.generator.internal.EditProjectLocationResolver;
import org.eclipse.sirius.web.emf.generator.internal.ProjectLocation;

/**
 * Checks edit-project locations resolved from relative and absolute source directories.
 *
 * @author lfasani
 */
public class EditProjectLocationResolverTest {
    private static final String MODEL_PROJECT = "model";

    private static final String EDIT_PROJECT = "edit";

    @TempDir
    private Path temporaryFolder;

    @Test
    public void resolvesRelativeEditProjectDirectory() throws Exception {
        Path backend = Files.createDirectory(this.temporaryFolder.resolve("backend"));
        ProjectLocation model = new ProjectLocation(MODEL_PROJECT, "src/model.genmodel", backend.resolve(MODEL_PROJECT));
        GenModel genModel = this.genModelWithDirectory("edit/src/main/java");

        EditProjectLocation edit = new EditProjectLocationResolver().resolve(model, genModel);

        assertThat(edit.projectName()).isEqualTo(EDIT_PROJECT);
        assertThat(edit.projectRoot()).isEqualTo(backend.resolve(EDIT_PROJECT));
    }

    @Test
    public void resolvesAbsoluteEditSourceDirectory() throws Exception {
        Path backend = Files.createDirectory(this.temporaryFolder.resolve("backend"));
        Path editSource = Files.createDirectories(backend.resolve("edit/src/main/java"));
        ProjectLocation model = new ProjectLocation(MODEL_PROJECT, "src/model.genmodel", backend.resolve(MODEL_PROJECT));
        GenModel genModel = this.genModelWithDirectory(editSource.toString());

        EditProjectLocation edit = new EditProjectLocationResolver().resolve(model, genModel);

        assertThat(edit.projectName()).isEqualTo(EDIT_PROJECT);
        assertThat(edit.projectRoot()).isEqualTo(backend.resolve(EDIT_PROJECT));
    }

    private GenModel genModelWithDirectory(String directory) {
        return (GenModel) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { GenModel.class },
                (proxy, method, arguments) -> {
                    if ("getEditProjectDirectory".equals(method.getName())) {
                        return directory;
                    }
                    if ("getEditPluginID".equals(method.getName())) {
                        return null;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
    }
}
