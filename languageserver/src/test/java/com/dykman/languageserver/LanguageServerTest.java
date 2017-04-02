package com.dykman.languageserver;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;

public class LanguageServerTest {

    private static final String SAMPLE_JAVA_FILE = "Sample.java";

    private LanguageServer languageServer;

    @BeforeClass
    public void setUp() throws URISyntaxException, IOException {
        String source = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(SAMPLE_JAVA_FILE));
        languageServer = new LanguageServer(source, SAMPLE_JAVA_FILE);
    }

    @Test
    public void testMethodInvocation() {
        AnalyzisResult analyzisResult = languageServer.position(205).get();

        // TODO: cleanup space
        assertEquals(analyzisResult.getToolTip(), "public int doStuff() ");
        assertEquals(analyzisResult.getDeclarationPosition(), 55);
        assertEquals(analyzisResult.getReferencePositions(), Collections.singletonList(202));
    }
}
