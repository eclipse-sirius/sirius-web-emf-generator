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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Initializes missing SVG resources for generated GIF icons while preserving existing SVG files.
 * @author cbrun
 */
public class SvgIconInitializer {
    private final String defaultSvgClasspath = "/Default.svg";

    private final String gifExtension = ".gif";

    public void initialize(Path projectRoot) {
        Path generatedIconsRoot = projectRoot.resolve("icons");
        if (!Files.isDirectory(generatedIconsRoot)) {
            return;
        }
        String defaultSvg = this.loadDefaultSvg();
        if (defaultSvg == null) {
            return;
        }
        Path resourcesIconsRoot = projectRoot.resolve("src/main/resources/icons");
        try (Stream<Path> paths = Files.walk(generatedIconsRoot)) {
            List<Path> gifs = paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(this.gifExtension))
                    .collect(Collectors.toList());
            for (Path gif : gifs) {
                Path svg = resourcesIconsRoot.resolve(this.swapExtension(generatedIconsRoot.relativize(gif), this.gifExtension, ".svg"));
                if (Files.exists(svg)) {
                    continue;
                }
                Path parent = svg.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                Files.writeString(svg, defaultSvg, StandardOpenOption.CREATE_NEW);
                System.out.println("Initialized missing SVG icon: " + svg);
            }
        } catch (IOException exception) {
            System.err.println("Failed to ensure SVG icons for " + projectRoot + " (" + exception.getMessage() + ')');
        }
        this.deleteDirectory(generatedIconsRoot);
    }

    private String loadDefaultSvg() {
        String defaultSvg = null;
        try (java.io.InputStream stream = this.getClass().getResourceAsStream(this.defaultSvgClasspath)) {
            if (stream == null) {
                System.err.println("Default.svg not found on classpath: " + this.defaultSvgClasspath);
            } else {
                defaultSvg = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException exception) {
            System.err.println("Failed to read Default.svg (" + exception.getMessage() + ')');
        }
        return defaultSvg;
    }

    private Path swapExtension(Path path, String fromExtension, String toExtension) {
        String fileName = path.getFileName().toString();
        if (!fileName.endsWith(fromExtension)) {
            return path;
        }
        String replacement = fileName.substring(0, fileName.length() - fromExtension.length()) + toExtension;
        Path parent = path.getParent();
        Path replacedPath = Path.of(replacement);
        if (parent != null) {
            replacedPath = parent.resolve(replacement);
        }
        return replacedPath;
    }

    private void deleteDirectory(Path directory) {
        if (!Files.exists(directory)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(directory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
                Files.deleteIfExists(path);
            }
            System.out.println("Removed generated icons directory: " + directory);
        } catch (IOException exception) {
            System.err.println("Failed to remove generated icons directory: " + directory + " (" + exception.getMessage() + ')');
        }
    }
}
