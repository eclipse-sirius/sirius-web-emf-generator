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

import java.nio.file.Path;

/**
 * Model Project location resolver.
 * @author cbrun
 */
public class ProjectLocationResolver {
    public ProjectLocation resolve(Path repositoryRoot, Path genmodelPath) {
        ProjectLocation location = null;
        Path absoluteGenmodel = genmodelPath.toAbsolutePath().normalize();
        Path absoluteRoot = repositoryRoot.toAbsolutePath().normalize();
        if (absoluteGenmodel.startsWith(absoluteRoot)) {
            Path relativeToRoot = absoluteRoot.relativize(absoluteGenmodel);
            int sourceIndex = this.lastIndexOfSegment(relativeToRoot, "src");
            if (sourceIndex >= 0 && absoluteRoot.getFileName() != null) {
                Path projectRoot = absoluteRoot;
                if (sourceIndex != 0) {
                    projectRoot = absoluteRoot.resolve(relativeToRoot.subpath(0, sourceIndex));
                }
                String projectName = projectRoot.getFileName().toString();
                location = new ProjectLocation(projectName, this.toLinuxPath(projectRoot.relativize(absoluteGenmodel)), projectRoot);
            }
        }
        return location;
    }

    private int lastIndexOfSegment(Path path, String segment) {
        for (int index = path.getNameCount() - 1; index >= 0; index--) {
            if (segment.equals(path.getName(index).toString())) {
                return index;
            }
        }
        return -1;
    }

    private String toLinuxPath(Path path) {
        StringBuilder result = new StringBuilder();
        for (Path segment : path) {
            if (!result.isEmpty()) {
                result.append('/');
            }
            result.append(segment);
        }
        return result.toString();
    }
}
