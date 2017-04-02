package com.dykman.languageserver.ast;

import org.eclipse.jdt.core.dom.*;


public class LeafFinderAstVisitor extends ASTVisitor {

    private final int position;
    private ASTNode bestNode;
    private int smallestLength = Integer.MAX_VALUE;

    public LeafFinderAstVisitor(int position) {
        this.position = position;
    }

    public ASTNode getLeafNode() {
        return bestNode;
    }

    @Override
    public void preVisit(ASTNode node) {
        // TODO: check edge cases
        if (bestNode == null && node.getLength() < position) {
            // Break early
            throw new RuntimeException(String.format("position too big: %s (length = %s)",
                                                     position, node.getLength()));
        }
        int length = node.getLength();
        int start = node.getStartPosition();
        int end = start + length;

        if (length < smallestLength && start < position && position < end) {
            bestNode = node;
            smallestLength = length;
        }
    }
}
