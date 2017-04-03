package com.dykman.languageserver.core;

import com.dykman.languageserver.ast.LeafFinderAstVisitor;
import com.dykman.languageserver.ast.MapperAstVisitor;
import org.apache.commons.collections4.ListUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LanguageServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageServer.class);

    private final CompilationUnit compilationUnit;
    private final Map<IBinding, SimpleName> bindingToDeclaration;
    private final Map<IBinding, List<SimpleName>> bindingToRefs;

    public LanguageServer(String source) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setCompilerOptions(JavaCore.getOptions());

        // Dummy values at this point
        parser.setUnitName("Blabla.java");
        String[] sources = { "." };
        String[] classpath = {};
        parser.setEnvironment(classpath, sources, new String[] {"UTF-8"}, true);

        parser.setSource(source.toCharArray());

        compilationUnit = (CompilationUnit) parser.createAST(null);

        MapperAstVisitor mapperAstVisitor = new MapperAstVisitor();
        compilationUnit.accept(mapperAstVisitor);
        bindingToDeclaration = mapperAstVisitor.getBindingToDeclaration();
        bindingToRefs = mapperAstVisitor.getBindingToRefs();
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

        final IBinding binding;

        // Look for a simple AST node
        if (node instanceof SimpleType) {
            binding = ((SimpleType) node).resolveBinding();
        } else if (node instanceof SimpleName) {
            binding = ((SimpleName) node).resolveBinding();
        } else {
            LOGGER.warn("Ignoring node '{}'", node);
            return Optional.empty();
        }

        // This is more or less the signature we need plus a bunch of stuff
        String signature = binding.toString();
        analysisResult.setToolTip(signature);

        // Reference positions
        List<Integer> referencePositions = ListUtils.emptyIfNull(bindingToRefs.get(binding)).stream()
                .map(SimpleName::getStartPosition)
                .collect(Collectors.toList());
        analysisResult.setReferencePositions(referencePositions);

        // Declaration position
        Optional.ofNullable(bindingToDeclaration.get(binding))
                .map(SimpleName::getStartPosition)
                .ifPresent(analysisResult::setDeclarationPosition);

        return Optional.of(analysisResult);
    }
}