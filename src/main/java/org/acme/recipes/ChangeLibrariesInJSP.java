package org.acme.recipes;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.SourceFile;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.text.PlainTextVisitor;
import org.openrewrite.text.PlainText;

public class ChangeLibrariesInJSP extends Recipe {

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
            if(isJsp && isInWebInf) {
                System.out.println("Checking file: " + path + " (isJsp:" + isJsp + " , isInWebInf:" + isInWebInf + " )");
            }
            return isJsp && isInWebInf;
        }

        @Override
        public @Nullable PlainText visitText(PlainText text, ExecutionContext ctx) {
            System.out.println("Inside visitingText method");
            String libraryName = "bootstrap.min.css";
            String oldUrl1 = "<link rel=\"stylesheet\" href=\"<c:url value='webjars/bootstrap/3.3.7-1/css/bootstrap.min.css'/>\" />";
            String oldUrl2 = "<link rel=\"stylesheet\" href=\"<c:url value='webjars/bootstrap/3.3.7-1/css/bootstrap.min.css'/>\"/>";
            String newUrl = "<link rel=\"stylesheet\" type=\"text/css\" href=\"resources/librerias/bootstrap/3.3.7-1/css/bootstrap.min.css\" />";
            System.out.println("Processing file: " + getCursor().getPath());
            String content = text.getText();
            // Check if the file contains our target
            if (!content.contains(libraryName)) {
                return text;
            }
            System.out.println("Found file containing library reference");
            // Find all link tags containing our library
            String[] lines = content.split("\n");
            StringBuilder newContent = new StringBuilder();
            boolean madeChanges = false;
            // Find all link tags containing our library
            for (String line : lines) {
                if (line.contains("<link")  && line.contains(libraryName)) {
                    System.out.println("Original line: ["+line+"]");
                    System.out.println("Contains oldUrl1 pattern? " + line.contains(oldUrl1));
                    System.out.println("Contains oldUrl2 pattern? " + line.contains(oldUrl2));
                    // Create new line with replacement
                    String newLine = line;
                    // Try both trim and non-trim versions
                    //String trimmedLine = line.trim();
                    //System.out.println("Trimmed line: ["+trimmedLine+"]");
                    if(line.contains(oldUrl1)) {
                        newLine = line.replace(oldUrl1, newUrl);
                    }else if(line.contains(oldUrl2)) {
                        newLine = line.replace(oldUrl2, newUrl);
                    }
                    madeChanges = true;
                    System.out.println("Modified line: ["+newLine+"]");
                    newContent.append(newLine).append("\n");
                } else {
                    newContent.append(line).append("\n");
                }
            }
            if (!madeChanges) {
                return text;
            }
            System.out.println("Changes made to file, returning modified content");
            return text.withText(newContent.toString());
        }
    }
}

