package com.dykman.languageserver;

import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

public class Server {

    private static void usage(String msg) {
        System.err.println(msg);
        System.err.println("Usage: $0 <LOCATION> <JAVA_FILE_PATH>");
        System.err.println("   example: $0 15 /tmp/Sample.java");
        System.exit(1);
    }

    private static String sourceFileAsString(File sourceFile) {
        try {
            return Files.toString(sourceFile, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(String.format("File: '%s' not found", sourceFile.getAbsolutePath()), e);
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            usage("Incorrect number of args");
            return;
        }

        // Parse location
        final int location;
        try {
            location = Integer.valueOf(args[0]);
        } catch (NumberFormatException e) {
            usage(String.format("Invalid int '%s'", args[0]));
            return;
        }

        // Get source
        final String source;
        try {
            source = sourceFileAsString(new File(args[1]));
        } catch (RuntimeException e) {
            usage(e.getMessage());
            return;
        }

        LanguageServer languageServer = new LanguageServer(source);

        final Optional<AnalysisResult> analysisResult;
        try {
            analysisResult = languageServer.position(location);
        } catch (RuntimeException e) {
            usage(String.format("Something went wrong: %s", e.getMessage()));
            return;
        }

        if (analysisResult.isPresent()) {
            System.out.println(analysisResult.get());
        } else {
            System.err.println(String.format("Got nothing for location %s", location));
        }
    }
}
