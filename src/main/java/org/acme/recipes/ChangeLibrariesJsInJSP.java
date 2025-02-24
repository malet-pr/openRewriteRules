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

public class ChangeLibrariesJsInJSP extends Recipe {

    private static String libraryName;
    private static List<String> oldUrls;
    private static String newUrl;

    // Recipe configuration is injected via the constructor
    @JsonCreator
    public ChangeLibrariesJsInJSP(
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
            for (String line : lines) {
                if (line.contains("<script")  && line.contains(libraryName)) {
                    // Check against each old URL in the list
                    String newLine = line;
                    boolean replaced = false;
                    for (String oldUrl : oldUrls) {
                        if (line.contains(oldUrl)) {
                            newLine = line.replace(oldUrl, newUrl);
                            replaced = true;
                            madeChanges = true;
                            break;
                        }
                    }
                    newContent.append(newLine).append("\n");
                }
            }
            if (!madeChanges) {
                return text;
            }
            return text.withText(newContent.toString());
        }
    }
}

