package com.dykman.languageserver;

import com.google.common.io.Files;
import org.apache.commons.collections4.ListUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LanguageServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageServer.class);

    final CompilationUnit compilationUnit;
    final Map<IMethodBinding, SimpleName> methodBindingToDeclaration;
    final Map<IMethodBinding, List<SimpleName>> methodBindingToRefs;
    final Map<IVariableBinding, SimpleName> variableBindingToDeclaration;
    final Map<IVariableBinding, List<SimpleName>> variableBindingToRefs;

    public LanguageServer(String source) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setCompilerOptions(JavaCore.getOptions());
        // Dummy name at this point
        parser.setUnitName("Blabla.java");

        String[] sources = {"/Users/damiendykman/git/damiendykman/compilation/languageserver/src/main/java"};
        // TODO: needed?
        String[] classpath = {"/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/rt.jar"};
        parser.setEnvironment(classpath, sources, new String[] {"UTF-8"}, true);

        parser.setSource(source.toCharArray());

        // TODO: add error if any
        compilationUnit = (CompilationUnit) parser.createAST(null);

        MapperAstVisitor mapperAstVisitor = new MapperAstVisitor();
        compilationUnit.accept(mapperAstVisitor);
        methodBindingToRefs = mapperAstVisitor.getMethodBindingToRefs();
        methodBindingToDeclaration = mapperAstVisitor.getMethodBindingToDeclaration();
        variableBindingToRefs = mapperAstVisitor.getVariableBindingToRefs();
        variableBindingToDeclaration = mapperAstVisitor.getVariableBindingToDeclaration();
    }

    // TODO: move
    private String sourceFileAsString(File sourceFile) {
        try {
            return Files.toString(sourceFile, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(String.format("File: '%s' not found", sourceFile.getAbsolutePath()), e);
        }
    }

    public Optional<AnalysisResult> position(int position) {
        LeafFinderAstVisitor leafFinderAstVisitor = new LeafFinderAstVisitor(position);
        compilationUnit.accept(leafFinderAstVisitor);

        // Get deepest AST node for position
        final ASTNode node = leafFinderAstVisitor.getLeafNode();

        return analyzeNode(node);
    }

    private Optional<AnalysisResult> analyzeNode(ASTNode node) {
        final AnalysisResult analysisResult = new AnalysisResult();

        if (node instanceof SimpleName) {
            SimpleName simpleName = (SimpleName) node;
            final IBinding binding = simpleName.resolveBinding();

            // This is more or less the signature we need (caveat, might have trailing spaces)
            final String signature = binding.toString().trim();
            analysisResult.setToolTip(signature);

            if (binding instanceof IMethodBinding) {
                IMethodBinding methodBinding = (IMethodBinding) binding;

                // Reference positions
                List<Integer> referencePositions = ListUtils.emptyIfNull(methodBindingToRefs.get(methodBinding)).stream()
                        .map(SimpleName::getStartPosition)
                        .collect(Collectors.toList());
                analysisResult.setReferencePositions(referencePositions);

                // Declaration position
                Optional.ofNullable(methodBindingToDeclaration.get(methodBinding))
                        .map(SimpleName::getStartPosition)
                        .ifPresent(analysisResult::setDeclarationPosition);
            } else if (binding instanceof IVariableBinding) {
                IVariableBinding variableBinding = (IVariableBinding) binding;

                // Reference positions
                List<Integer> referencePositions = ListUtils.emptyIfNull(variableBindingToRefs.get(variableBinding)).stream()
                        .map(SimpleName::getStartPosition)
                        .collect(Collectors.toList());
                analysisResult.setReferencePositions(referencePositions);

                // Declaration position
                Optional.ofNullable(variableBindingToDeclaration.get(variableBinding))
                        .map(SimpleName::getStartPosition)
                        .ifPresent(analysisResult::setDeclarationPosition);
            } else {
                LOGGER.warn("Ignoring binding {}", binding);
            }
            return Optional.of(analysisResult);
        }

        throw new IllegalStateException(String.format("Ignoring node %s", node));
    }
}
