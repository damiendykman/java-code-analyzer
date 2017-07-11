package me.dykman.languageserver.util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.testng.annotations.Test;


import static org.testng.Assert.assertEquals;

public class PositionUtilTest {

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
        assertEquals(PositionUtil.position(str, line, column), position);
        assertEquals(PositionUtil.lineColumn(str, position), new ImmutablePair<>(line, column));
    }
}
