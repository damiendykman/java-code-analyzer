package com.dykman.languageserver;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;

public class TestUtils {

    /**
     * Return a position given a line and a column.
     * Using the convention that 1st column is 1 (eg: like Intellij but unlike Emacs)
     */
    public static int position(String str, int line, int column) {
        // WARNING: should check line and column within bounds and throw

        // Sum the lengths of the lines above cursor
        int fullLineOffset = Arrays.asList(str.split("\n")).stream()
                .limit(line - 1)
                .mapToInt(l -> l.length() + 1) // Adding '\n' to each line
                .sum();
        return fullLineOffset + column;
    }

    /**
     * Return a line and column given a position
     * Using the convention that 1st column is 1 (eg: like Intellij but unlike Emacs)
     */
    public static Pair<Integer, Integer> lineColumn(String str, int pos) {
        int line = 1;
        Integer column = null;
        int fullLineOffset = 0;
        for (String lineStr : str.split("\n")) {
            if (lineStr.length() + 1 >= pos - fullLineOffset) {
                // On this line
                column = pos - fullLineOffset;
                break;
            } else {
                // Not on this line
                fullLineOffset += lineStr.length() + 1;
                line++;
            }
        }
        if (column == null) {
            throw new RuntimeException("Out of bound");
        }

        return new ImmutablePair<>(line, column);
    }

    @Test
    private void testBothWays() {
        String str = "hello\n\nworld\n";
        checkBothWays(str, 1, 1, 1);
        checkBothWays(str, 5, 1, 5);
        checkBothWays(str, 6, 1, 6);
        checkBothWays(str, 7, 2, 1);
        checkBothWays(str, 8, 3, 1);
        checkBothWays(str, 9, 3, 2);
    }

    private void checkBothWays(String str, int position, int line, int column) {
        assertEquals(position(str, line, column), position);
        assertEquals(lineColumn(str, position), new ImmutablePair<>(line, column));
    }
}
