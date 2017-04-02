package com.dykman.languageserver;

import java.util.Collections;
import java.util.List;

public class AnalyzisResult {

    // TODO: can getter be @Nonnull?

    private String toolTip;
    private Integer declarationPosition;
    private List<Integer> referencePositions = Collections.emptyList();

    public String getToolTip() {
        return toolTip;
    }

    public AnalyzisResult setToolTip(String toolTip) {
        this.toolTip = toolTip;
        return this;
    }

    public int getDeclarationPosition() {
        return declarationPosition;
    }

    public AnalyzisResult setDeclarationPosition(int declarationPosition) {
        this.declarationPosition = declarationPosition;
        return this;
    }

    public List<Integer> getReferencePositions() {
        return referencePositions;
    }

    public AnalyzisResult setReferencePositions(List<Integer> referencePositions) {
        this.referencePositions = referencePositions;
        return this;
    }
}
