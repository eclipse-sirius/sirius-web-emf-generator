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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.eclipse.sirius.web.emf.generator.internal.GeneratorArguments;
import org.eclipse.sirius.web.emf.generator.internal.GeneratorArgumentsParser;

/**
 * Checks generator command-line defaults, explicit options, and invalid input.
 *
 * @author lfasani
 */
public class GeneratorArgumentsParserTest {
    private static final String REPOSITORY = "repository";

    @TempDir
    private Path temporaryFolder;

    @Test
    public void parsesPlainPathAndPattern() throws Exception {
        Path root = Files.createDirectory(this.temporaryFolder.resolve(REPOSITORY));

        GeneratorArguments arguments = new GeneratorArgumentsParser().parse(new String[] {
                root.toString(), "--genmodel-pattern=**/*.genmodel" });

        assertThat(arguments.repositoryRoot()).isEqualTo(root);
        assertThat(arguments.genmodelPattern()).isEqualTo("**/*.genmodel");
        assertThat(arguments.format()).isTrue();
    }

    @Test
    public void parsesFileUri() throws Exception {
        Path root = Files.createDirectory(this.temporaryFolder.resolve(REPOSITORY));

        GeneratorArguments arguments = new GeneratorArgumentsParser().parse(new String[] { root.toUri().toString() });

        assertThat(arguments.repositoryRoot()).isEqualTo(root);
        assertThat(arguments.genmodelPattern()).isEqualTo("**/src/**.genmodel");
        assertThat(arguments.format()).isTrue();
    }

    @Test
    public void parsesExplicitFormatValues() throws Exception {
        Path root = Files.createDirectory(this.temporaryFolder.resolve(REPOSITORY));

        GeneratorArguments enabled = new GeneratorArgumentsParser().parse(new String[] { root.toString(), "--format=true" });
        GeneratorArguments disabled = new GeneratorArgumentsParser().parse(new String[] { root.toString(), "--format=false" });

        assertThat(enabled.format()).isTrue();
        assertThat(disabled.format()).isFalse();
    }

    @Test
    public void rejectsBlankPattern() throws Exception {
        Path root = Files.createDirectory(this.temporaryFolder.resolve(REPOSITORY));
        assertThatThrownBy(() -> new GeneratorArgumentsParser().parse(new String[] { root.toString(), "--genmodel-pattern=" }))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void rejectsFormatWithoutValue() throws Exception {
        Path root = Files.createDirectory(this.temporaryFolder.resolve(REPOSITORY));
        assertThatThrownBy(() -> new GeneratorArgumentsParser().parse(new String[] { root.toString(), "--format" }))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void rejectsInvalidFormatValue() throws Exception {
        Path root = Files.createDirectory(this.temporaryFolder.resolve(REPOSITORY));
        assertThatThrownBy(() -> new GeneratorArgumentsParser().parse(new String[] { root.toString(), "--format=maybe" }))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
