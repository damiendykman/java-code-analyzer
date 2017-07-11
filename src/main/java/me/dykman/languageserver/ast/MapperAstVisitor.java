package me.dykman.languageserver.ast;

import org.eclipse.jdt.core.dom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * AST visitor to create the following maps:
 * - binding to declaration (if any)
 * - binding to reference nodes
 */
public class MapperAstVisitor extends ASTVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperAstVisitor.class);

    private final Map<IBinding, List<SimpleName>> bindingToRefs = new HashMap<>();
    private final Map<IBinding, SimpleName> bindingToDeclaration = new HashMap<>();

    public Map<IBinding, List<SimpleName>> getBindingToRefs() {
        return bindingToRefs;
    }

    public Map<IBinding, SimpleName> getBindingToDeclaration() {
        return bindingToDeclaration;
    }

    @Override
    public boolean visit(SimpleName node) {
        boolean isDeclaration = node.isDeclaration();
        IBinding binding = node.resolveBinding();

        if (binding instanceof IMethodBinding
            || binding instanceof IVariableBinding
            || binding instanceof ITypeBinding
            || binding instanceof IPackageBinding) {
            if (isDeclaration) {
                if (bindingToDeclaration.get(binding) != null) {
                    throw new IllegalStateException(String.format("SimpleName '%s' already set", node));
                }
                bindingToDeclaration.put(binding, node);
            } else {
                List<SimpleName> simpleNames = Optional.ofNullable(bindingToRefs.get(binding))
                        .orElseGet(ArrayList::new);
                simpleNames.add(node);
                bindingToRefs.put(binding, simpleNames);
            }
        } else {
            LOGGER.warn("Unsupported binding: {}", binding);
        }

        return super.visit(node);
    }
}
