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

import java.util.Map;

import org.eclipse.emf.codegen.ecore.templates.edit.Plugin;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

/**
 * Retains EMF's plugin template and adds SVG-aware resource lookup before Java merging.
 *
 * @author mcharfadi
 */
public class EditPluginTemplate {
    // JETEmitter reflects on this field and create(String) to select the line delimiter.
    @SuppressWarnings({ "checkstyle:MemberName", "checkstyle:VisibilityModifier" })
    public final String NL;

    public EditPluginTemplate() {
        this(System.lineSeparator());
    }

    private EditPluginTemplate(String lineDelimiter) {
        this.NL = lineDelimiter;
    }

    public static EditPluginTemplate create(String lineDelimiter) {
        return new EditPluginTemplate(lineDelimiter);
    }

    public String generate(Object argument) {
        String source = Plugin.create(this.NL).generate(argument);
        ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
        parser.setCompilerOptions(Map.of(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_21));
        parser.setSource(source.toCharArray());
        CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
        TypeDeclaration pluginType = (TypeDeclaration) compilationUnit.types().get(0);
        ASTRewrite rewrite = ASTRewrite.create(compilationUnit.getAST());
        ListRewrite methods = rewrite.getListRewrite(pluginType, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
        for (MethodDeclaration method : this.imageLookupMethods()) {
            methods.insertLast(ASTNode.copySubtree(compilationUnit.getAST(), method), null);
        }
        Document document = new Document(source);
        try {
            rewrite.rewriteAST(document, null).apply(document);
        } catch (BadLocationException exception) {
            throw new IllegalStateException("Cannot add SVG image lookup to the edit plugin", exception);
        }
        return document.get();
    }

    private MethodDeclaration[] imageLookupMethods() {
        String methods = """
                /**
                 * Locates an image without decoding it, including SVG resources.
                 * @generated
                 */
                @Override
                protected Object doGetImage(String key) throws java.io.IOException {
                    java.net.URL url = new java.net.URL(this.getBaseURL(), "icons/" + key + extensionFor(key));
                    try (java.io.InputStream inputStream = url.openStream()) {
                        return url;
                    }
                }

                /**
                 * Preserves explicit image extensions and defaults extensionless keys to GIF.
                 * @generated
                 */
                protected static String extensionFor(String key) {
                    int index = key.lastIndexOf('.');
                    if (index != -1) {
                        String extension = key.substring(index + 1);
                        if ("png".equalsIgnoreCase(extension) || "gif".equalsIgnoreCase(extension) || "bmp".equalsIgnoreCase(extension)
                                || "ico".equalsIgnoreCase(extension) || "jpg".equalsIgnoreCase(extension) || "jpeg".equalsIgnoreCase(extension)
                                || "tif".equalsIgnoreCase(extension) || "tiff".equalsIgnoreCase(extension) || "svg".equalsIgnoreCase(extension)) {
                            return "";
                        }
                    }
                    return ".gif";
                }
                """;
        ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
        parser.setCompilerOptions(Map.of(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_21));
        parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
        parser.setSource(methods.toCharArray());
        TypeDeclaration declarations = (TypeDeclaration) parser.createAST(null);
        return declarations.getMethods();
    }
}
