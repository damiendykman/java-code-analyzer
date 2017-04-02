package com.dykman.languageserver;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MapperAstVisitor extends ASTVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperAstVisitor.class);

    private final Map<IMethodBinding, List<SimpleName>> methodBindingToRefs = new HashMap<>();
    private final Map<IMethodBinding, SimpleName> methodBindingToDeclaration = new HashMap<>();
    private final Map<IVariableBinding, List<SimpleName>> variableBindingToRefs = new HashMap<>();
    private final Map<IVariableBinding, SimpleName> variableBindingToDeclaration = new HashMap<>();

    public Map<IMethodBinding, List<SimpleName>> getMethodBindingToRefs() {
        return methodBindingToRefs;
    }

    public Map<IMethodBinding, SimpleName> getMethodBindingToDeclaration() {
        return methodBindingToDeclaration;
    }

    public Map<IVariableBinding, List<SimpleName>> getVariableBindingToRefs() {
        return variableBindingToRefs;
    }

    public Map<IVariableBinding, SimpleName> getVariableBindingToDeclaration() {
        return variableBindingToDeclaration;
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
        } else if (binding instanceof IVariableBinding) {
            IVariableBinding variableBinding = (IVariableBinding) binding;
            if (isDeclaration) {
                if (variableBindingToDeclaration.get(variableBinding) != null) {
                    throw new IllegalStateException(String.format("SizmpleName '%s' already set", node));
                }
                variableBindingToDeclaration.put(variableBinding, node);
            } else {
                List<SimpleName> simpleNames = Optional.ofNullable(variableBindingToRefs.get(variableBinding))
                        .orElseGet(ArrayList::new);
                simpleNames.add(node);
                variableBindingToRefs.put(variableBinding, simpleNames);
            }
        } else if (binding instanceof TypeBinding) {
            // TODO
        } else if (binding instanceof PackageBinding) {
            // TODO
        } else {
            LOGGER.warn("Unsupported binding: {}", binding);
        }

        return super.visit(node);
    }

//    void add(IBinding binding, ) {
//        if (bindingindingToDeclaration.get(methodBinding) != null) {
//            throw new IllegalStateException(String.format("SimpleName '%s' already set", node));
//        }
//        bindingToDeclaration.put(methodBinding, node);
//    }

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
