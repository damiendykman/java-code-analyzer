package me.dykman.languageserver.core;

import java.util.Collections;
import java.util.List;

public class AnalysisResult {

    private String toolTip;
    private Integer declarationPosition;
    private List<Integer> referencePositions = Collections.emptyList();

    public String getToolTip() {
        return toolTip;
    }

    public AnalysisResult setToolTip(String toolTip) {
        this.toolTip = toolTip;
        return this;
    }

    public Integer getDeclarationPosition() {
        return declarationPosition;
    }

    public AnalysisResult setDeclarationPosition(int declarationPosition) {
        this.declarationPosition = declarationPosition;
        return this;
    }

    public List<Integer> getReferencePositions() {
        return referencePositions;
    }

    public AnalysisResult setReferencePositions(List<Integer> referencePositions) {
        this.referencePositions = referencePositions;
        return this;
    }
}
