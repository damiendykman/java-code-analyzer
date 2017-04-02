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
    public void testMethodInvocation() {
        AnalysisResult analysisResult = languageServer.position(position(15, 14)).get();
        // TODO/WARNING: 8 vs. 9
        checkAnalysisResult(analysisResult, "public int doStuff()", 5, 15, Arrays.asList(15, 8));
    }

    private void checkAnalysisResult(AnalysisResult actual, String toolTip,
                                     int declLine, int declColumn,
                                     List<Integer> refLineColumns) {
        assertEquals(actual.getToolTip(), toolTip);
        assertEquals(actual.getDeclarationPosition(), position(declLine, declColumn));

        final List<Pair<Integer, Integer>> refLineColumnPairs = actual.getReferencePositions().stream()
                .map(this::lineColumn)
                .collect(Collectors.toList());

        // Transform to set of pair because pair order does not matter
        assertEquals(refLineColumnPairs, lineColumnPairs(refLineColumns));
    }

    private Set<Pair<Integer, Integer>> lineColumnPairs(List<Integer> lineColumns) {
        Set<Pair<Integer, Integer>> lineColumnPairs = new HashSet<>();
        for (int i = 0; i < lineColumns.size() / 2; i += 2) {
            lineColumnPairs.add(new ImmutablePair<>(lineColumns.get(i), lineColumns.get(i+1)));
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
