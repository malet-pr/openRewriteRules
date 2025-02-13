package org.acme.recipes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.SourceFile;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.text.PlainTextVisitor;
import org.openrewrite.text.PlainText;

public class ChangeLibrariesJsInJSP extends Recipe {

    private static String libraryName;
    private static String oldUrl;
    private static String newUrl;

    // Recipe configuration is injected via the constructor
    @JsonCreator
    public ChangeLibrariesJsInJSP(
            @JsonProperty("libraryName") String libraryName,
            @JsonProperty("oldUrl") String oldUrl,
            @JsonProperty("newUrl") String newUrl) {
        this.libraryName = libraryName;
        this.oldUrl = oldUrl;
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
                if (line.contains("<script")  && line.contains(libraryName)) {
                    // Create new line with replacement
                    String newLine = line;
                    if(oldUrl != null && line.contains(oldUrl)) {
                        newLine = line.replace(oldUrl, newUrl);
                    }
                    madeChanges = true;
                    newContent.append(newLine).append("\n");
                } else {
                    newContent.append(line).append("\n");
                }
            }
            if (!madeChanges) {
                return text;
            }
            return text.withText(newContent.toString());
        }
    }
}

