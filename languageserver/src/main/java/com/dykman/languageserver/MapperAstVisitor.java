package com.dykman.languageserver;

import org.eclipse.jdt.core.dom.*;

import java.util.*;

public class MapperAstVisitor extends ASTVisitor {

    private final Map<IMethodBinding, List<SimpleName>> methodBindingToRefs = new HashMap<>();
    private final Map<IMethodBinding, SimpleName> methodBindingToDeclaration = new HashMap<>();

    public Map<IMethodBinding, List<SimpleName>> getMethodBindingToRefs() {
        return methodBindingToRefs;
    }

    public Map<IMethodBinding, SimpleName> getMethodBindingToDeclaration() {
        return methodBindingToDeclaration;
    }

    @Override
    public boolean visit(SimpleName node) {
        boolean isDeclaration = node.isDeclaration();
        IBinding binding = node.resolveBinding();
        if (binding instanceof IMethodBinding) {
            IMethodBinding methodBinding = (IMethodBinding) binding;
            if (isDeclaration) {
                if (methodBindingToDeclaration.get(methodBinding) != null) {
                    throw new IllegalStateException(String.format("SimpleName '%s' already set", node));
                }
                methodBindingToDeclaration.put(methodBinding, node);
            } else {
                List<SimpleName> simpleNames = Optional.ofNullable(methodBindingToRefs.get(methodBinding))
                        .orElseGet(ArrayList::new);
                simpleNames.add(node);
                methodBindingToRefs.put(methodBinding, simpleNames);
            }
        }

        return super.visit(node);
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        // TODO: needed?
        return super.visit(node);
    }

    @Override
    public boolean visit(VariableDeclarationFragment node) {
        // TODO: needed?
        return super.visit(node);
    }
}
