package com.dykman.languageserver;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        AnalyzisResult analyzisResult = languageServer.position(position(15, 14)).get();
        checkAnalyzisResult(analyzisResult, "public int doStuff()", 5, 15, Arrays.asList(15, 8));
    }

    private void checkAnalyzisResult(AnalyzisResult actual, String toolTip,
                                     int declLine, int declColumn,
                                     List<Integer> refLineColumns) {
        assertEquals(actual.getToolTip(), toolTip);
        assertEquals(actual.getDeclarationPosition(), position(declLine, declColumn));

        final List<Integer> actualLineColumRefs = actual.getReferencePositions().stream()
                .map(this::lineColumn)
                .flatMap(pair -> Stream.of(pair.getLeft(), pair.getRight()))
                .collect(Collectors.toList());
        assertEquals(actualLineColumRefs, refLineColumns);
    }

    private int position(int line, int column) {
        return TestUtils.position(source, line, column);
    }

    private Pair<Integer, Integer> lineColumn(int pos) {
        return TestUtils.lineColumn(source, pos);
    }
}
