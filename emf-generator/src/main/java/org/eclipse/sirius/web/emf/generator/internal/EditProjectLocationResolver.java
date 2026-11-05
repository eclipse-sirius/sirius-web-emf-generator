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

package org.eclipse.sirius.web.emf.generator.internal;

import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;

/**
 * Edit Project location resolver.
 * @author cbrun
 */
public class EditProjectLocationResolver {
    public EditProjectLocation resolve(ProjectLocation projectLocation, GenModel genModel) {
        EditProjectLocation resolved = null;
        if (projectLocation != null && projectLocation.projectRoot().getParent() != null) {
            Path parentDir = projectLocation.projectRoot().getParent();
            resolved = this.fromDirectory(genModel.getEditProjectDirectory(), parentDir);
            if (resolved == null) {
                String pluginId = genModel.getEditPluginID();
                if (pluginId != null && !pluginId.isBlank()) {
                    resolved = new EditProjectLocation(pluginId, parentDir.resolve(pluginId));
                }
            }
        }
        return resolved;
    }

    private EditProjectLocation fromDirectory(String directory, Path parentDir) {
        EditProjectLocation resolved = null;
        if (directory != null && !directory.isBlank()) {
            Path directoryPath = Path.of(directory);
            if (directoryPath.isAbsolute() && Files.exists(directoryPath)) {
                Path projectRoot = this.projectRoot(directoryPath);
                if (projectRoot != null) {
                    resolved = new EditProjectLocation(projectRoot.getFileName().toString(), projectRoot);
                }
            } else {
                resolved = this.fromRelativeDirectory(directory, parentDir);
            }
        }
        return resolved;
    }

    private EditProjectLocation fromRelativeDirectory(String directory, Path parentDir) {
        EditProjectLocation resolved = null;
        String normalized = directory.replace('\\', '/').trim();
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (!normalized.isEmpty()) {
            Path relative = Path.of(normalized);
            if (relative.getNameCount() > 0) {
                resolved = new EditProjectLocation(relative.getName(0).toString(), parentDir.resolve(relative.getName(0)));
            }
        }
        return resolved;
    }

    private Path projectRoot(Path path) {
        for (int index = 0; index < path.getNameCount(); index++) {
            if ("src".equals(path.getName(index).toString()) && index > 0) {
                Path prefix = path.subpath(0, index);
                if (path.getRoot() != null) {
                    prefix = path.getRoot().resolve(prefix);
                }
                return prefix;
            }
        }
        return path;
    }
}
