package sample;

public class Sample {

    public int doStuff() {

        Integer myInteger = new Integer(10);
        myInteger.compareTo(12);    myInteger.byteValue();
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
