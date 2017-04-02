package sample;

public class Sample {

    public int doStuff() {

        Integer j = new Integer(10);
        j.compareTo(12);
        return 0;
    }

    int i = 0;

    int someMethod() {
        doStuff();
        int i = 0;
            doStuff();
        return 0;
    }

    // Supported:
    // - variable
    // - type
    // - method
}
