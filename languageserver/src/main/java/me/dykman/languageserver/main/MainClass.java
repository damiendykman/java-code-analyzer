package me.dykman.languageserver.main;

import me.dykman.languageserver.core.AnalysisResult;
import me.dykman.languageserver.core.LanguageServer;
import me.dykman.languageserver.util.PositionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Main class (to be ran using java -jar ...)
 */
public class MainClass {

    private static ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private static void usage(String msg) {
        System.err.println(msg);
        System.err.println("Usage: ... <LOCATION>|<LINE:COLUM> <JAVA_FILE_PATH>");
        System.err.println("   example: java -jar language-server-x.y.jar 60 /tmp/Sample.java");
        System.err.println("   example: java -jar language-server-x.y.jar 5:19 /tmp/Sample.java");
        System.exit(1);
    }

    private static String sourceFileAsString(File sourceFile) {
        try {
            return Files.toString(sourceFile, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(String.format("File: '%s' not found", sourceFile.getAbsolutePath()), e);
        }
    }

    private static String toJsonString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            usage("Incorrect number of args");
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

        // Parse location
        final int location;
        final String[] split = args[0].split(":", Integer.MAX_VALUE);
        if (split.length == 2) {
            // Format 'line:column'
            int line;
            int column;
            try {
                 line = Integer.valueOf(split[0]);
                 column = Integer.valueOf(split[1]);
            } catch (NumberFormatException e) {
                usage(String.format("Invalid line:column '%s'", args[0]));
                return;
            }
            location = PositionUtil.position(source, line, column);
        } else if (split.length == 1) {
            // Format 'location'
            try {
                location = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                usage(String.format("Invalid location '%s'", args[0]));
                return;
            }
        } else {
            // Unknown format
            usage(String.format("Invalid location|line:column '%s'", args[0]));
            return;
        }

        // Create language server
        LanguageServer languageServer = new LanguageServer(source);

        // Run analysis
        final Optional<AnalysisResult> analysisResult;
        try {
            analysisResult = languageServer.position(location);
        } catch (RuntimeException e) {
            System.err.println(String.format("Something went wrong: %s", e.getMessage()));
            System.exit(1);
            return;
        }

        // Output result
        if (analysisResult.isPresent()) {
            System.out.println(toJsonString(analysisResult.get()));
        } else {
            System.err.println(String.format("Got nothing for location %s", location));
        }
    }
}
