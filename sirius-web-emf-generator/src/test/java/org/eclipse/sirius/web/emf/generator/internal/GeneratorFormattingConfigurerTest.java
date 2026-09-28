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

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.Test;

/**
 * Checks whether generation options and formatter preferences follow the formatting flag.
 *
 * @author lfasani
 */
public class GeneratorFormattingConfigurerTest {
    @Test
    public void enablesFormattingAndLoadsFormatterOptions() {
        Generator generator = new Generator();

        new GeneratorFormattingConfigurer().configure(generator, new ResourceSetImpl(), null, true);

        assertThat(generator.getOptions().codeFormatting).isTrue();
        assertThat(generator.getOptions().commentFormatting).isTrue();
        assertThat(generator.getOptions().codeFormatterOptions).isNotNull();
    }

    @Test
    public void disablesFormattingWithoutLoadingFormatterOptions() {
        Generator generator = new Generator();

        new GeneratorFormattingConfigurer().configure(generator, new ResourceSetImpl(), null, false);

        assertThat(generator.getOptions().codeFormatting).isFalse();
        assertThat(generator.getOptions().commentFormatting).isFalse();
        assertThat(generator.getOptions().codeFormatterOptions).isNullOrEmpty();
    }
}
