package me.dykman.languageserver.util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

/**
 * Util class to convert a position in a file to and from line & column.
 */
public class PositionUtil {

    /**
     * Return a position given a line and a column.
     * Using the convention that 1st column is 1 (eg: like Intellij but unlike Emacs)
     */
    public static int position(String str, int line, int column) {
        // WARNING: should check line and column within bounds and throw

        // Sum the lengths of the lines above cursor
        int fullLineOffset = Arrays.stream(str.split("\n"))
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
}
