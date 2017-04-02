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

        String actualToolTip = "public int compareTo(java.lang.Integer)";
        Pair<Integer, Integer> actualDeclaration = null;
        List<Integer> actualRefs = Arrays.asList(8, 19);

        AnalysisResult analysisResult = languageServer.position(position(8, 23)).get();
        checkAnalysisResult(analysisResult, actualToolTip, actualDeclaration, actualRefs);
    }

    @Test
    public void testVariable() {
        String actualToolTip = "java.lang.Integer myInteger[pos: unused][id:0]";
        // TODO: shouldn't be null....
        Pair<Integer, Integer> actualDeclaration = null;
        List<Integer> actualRefs = Arrays.asList(8, 9, 8, 37);

        // Variable declaration
        AnalysisResult analysisResult = languageServer.position(position(7, 22)).get();
        // TODO: better signature
        checkAnalysisResult(analysisResult, actualToolTip, actualDeclaration, actualRefs);

        // Variable reference
        analysisResult = languageServer.position(position(8, 12)).get();
        checkAnalysisResult(analysisResult, actualToolTip, actualDeclaration, actualRefs);
    }

    @Test
    public void testType() {
        // TODO: better signature
        String actualToolTip = "(id=NoId)\n" +
                               "public class sample.Sample\n" +
                               "\textends java.lang.Object\n" +
                               "/*   fields   */\n" +
                               "int i\n" +
                               "/*   methods   */\n" +
                               "public void <init>() \n" +
                               "public int doStuff() \n" +
                               "public static void main(java.lang.String[]) \n" +
                               "int someMethod()";
        Pair<Integer, Integer> actualDeclaration = new ImmutablePair<>(3,14);
        List<Integer> actualRefs = Arrays.asList(22, 9, 22, 29);

        // Type declaration
        AnalysisResult analysisResult = languageServer.position(position(3, 16)).get();
        checkAnalysisResult(analysisResult, actualToolTip, actualDeclaration, actualRefs);
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

        final Set<Pair<Integer, Integer>> actualRefLineColumnPairs = actual.getReferencePositions().stream()
                .map(this::lineColumn)
                .collect(Collectors.toSet());

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
