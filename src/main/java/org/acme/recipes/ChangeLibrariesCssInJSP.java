package org.acme.recipes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.SourceFile;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.text.PlainTextVisitor;
import org.openrewrite.text.PlainText;

import java.util.List;

public class ChangeLibrariesCssInJSP extends Recipe {

    private static String libraryName;
    private static List<String> oldUrls;
    private static String newUrl;

    // Recipe configuration is injected via the constructor
    @JsonCreator
    public ChangeLibrariesCssInJSP(
            @JsonProperty("libraryName") String libraryName,
            @JsonProperty("oldUrls") List<String> oldUrls,
            @JsonProperty("newUrl") String newUrl) {
        this.libraryName = libraryName;
        this.oldUrls = oldUrls;
        this.newUrl = newUrl;
    }

    @Override
    public String getDisplayName() {
        return "Change library references in JSP files";
    }

    @Override
    public String getDescription() {
        return "Updates library references in JSP files only";
    }

    @Override
    public PlainTextVisitor<ExecutionContext> getVisitor() {
        return new LibraryResourceVisitor();
    }

    private static class LibraryResourceVisitor extends PlainTextVisitor<ExecutionContext> {
        @Override
        public boolean isAcceptable(SourceFile sourceFile, ExecutionContext ctx) {
            String path = sourceFile.getSourcePath().toString();
            boolean isJsp = path.endsWith(".jsp");
            boolean isInWebInf = path.contains("WEB-INF");
            return isJsp && isInWebInf;
        }

        @Override
        public @Nullable PlainText visitText(PlainText text, ExecutionContext ctx) {
            String content = text.getText();
            // Check if the file contains our target
            if (!content.contains(libraryName)) {
                return text;
            }
            // Find all link tags containing our library
            String[] lines = content.split("\n");
            StringBuilder newContent = new StringBuilder();
            boolean madeChanges = false;
            // Find all link tags containing our library
            for (String line : lines) {
                String newLine = line;
                // Only process lines with <link> tags containing our library
                if (line.contains("<link") && line.contains(libraryName)) {
                    // Check against each old URL in the list
                    for (String oldUrl : oldUrls) {
                        if (line.contains(oldUrl)) {
                            newLine = line.replace(oldUrl, newUrl);
                            madeChanges = true;
                            break;
                        }
                    }
                }
                // Always append the line (original or modified)
                newContent.append(newLine);
                // Add the newline character to maintain the original structure
                // Don't add a trailing newline at the end if the original didn't have one
                if (newContent.length() < content.length()) {
                    newContent.append("\n");
                }
            }
            // Only return a new text object if changes were made
            if (!madeChanges) {
                return text;
            }
            return text.withText(newContent.toString());
        }
    }
}

