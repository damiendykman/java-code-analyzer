package com.dykman.languageserver;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class LanguageServerTest {

    private static final String SAMPLE_JAVA_FILE = "Sample.java";

    private LanguageServer languageServer;

    @BeforeClass
    public void setUp() throws URISyntaxException, IOException {
        String source = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(SAMPLE_JAVA_FILE));
        languageServer = new LanguageServer(source);
    }

    @Test
    public void testMethodInvocation() {
        AnalyzisResult analyzisResult = languageServer.position(205).get();

        // TODO: cleanup space
        checkAnalyzisResult(analyzisResult, "public int doStuff() ", 55, Collections.singletonList(202));
    }

    private void checkAnalyzisResult(AnalyzisResult actual, String toolTip, int declPos, List<Integer> refPos) {
        assertEquals(actual.getToolTip(), toolTip);
        assertEquals(actual.getDeclarationPosition(), declPos);
        assertEquals(actual.getReferencePositions(), refPos);
    }
}
