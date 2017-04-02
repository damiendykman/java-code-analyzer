package com.dykman.languageserver;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

public class LanguageServerTest {

    private static final String SAMPLE_JAVA_FILE = "Sample.java";

    private LanguageServer languageServer;
    private String source;

    @BeforeMethod
    public void setUp() throws URISyntaxException, IOException {
        source = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(SAMPLE_JAVA_FILE));
        languageServer = new LanguageServer(source);
    }

    @Test
    public void testLocalMethodInvocation() {
        // Method invocation of method declared locally
        AnalysisResult analysisResult = languageServer.position(position(15, 14)).get();
        checkAnalysisResult(analysisResult, "public int doStuff()",
                            new ImmutablePair<>(5, 16),
                            Arrays.asList(15, 9, 17, 13));
    }

    @Test
    public void testExternalMethodInvocation() {
        // Method invocation of method declared externally
        AnalysisResult analysisResult = languageServer.position(position(8, 23)).get();
        checkAnalysisResult(analysisResult,
                            "public int compareTo(java.lang.Integer)",
                            null,
                            Arrays.asList(7, 17));
    }

    @Test
    // TODO: better test name
    public void testVariable() {
        // Method invocation of method declared externally
        AnalysisResult analysisResult = languageServer.position(position(7, 22)).get();
        // TODO: better signature
        checkAnalysisResult(analysisResult,
                            "java.lang.Integer myInteger[pos: unused][id:0]",
                            null,
                            Arrays.asList(8, 9, 8, 37));
    }

    private void checkAnalysisResult(AnalysisResult actual, String toolTip,
                                     Pair<Integer, Integer> declLineColum,
                                     List<Integer> refLineColumns) {
        assertEquals(actual.getToolTip(), toolTip);

        if (declLineColum != null) {
            // Offset by 1 (column vs. string index)
            assertEquals(actual.getDeclarationPosition().intValue(),
                         position(declLineColum.getLeft(), declLineColum.getRight()) - 1);
        }

        final List<Pair<Integer, Integer>> actualRefLineColumnPairs = actual.getReferencePositions().stream()
                .map(this::lineColumn)
                .collect(Collectors.toList());

        // Transform to set of pair because pair order does not matter
        assertEquals(actualRefLineColumnPairs, lineColumnPairs(refLineColumns));
    }

    private Set<Pair<Integer, Integer>> lineColumnPairs(List<Integer> lineColumns) {
        Set<Pair<Integer, Integer>> lineColumnPairs = new HashSet<>();
        for (int i = 0; i < lineColumns.size(); i += 2) {
            // Offset by 1 (column vs. string index)
            lineColumnPairs.add(new ImmutablePair<>(lineColumns.get(i), lineColumns.get(i + 1) - 1));
        }
        return lineColumnPairs;
    }

    private int position(int line, int column) {
        return TestUtils.position(source, line, column);
    }

    private Pair<Integer, Integer> lineColumn(int pos) {
        return TestUtils.lineColumn(source, pos);
    }
}
