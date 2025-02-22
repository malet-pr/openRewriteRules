package org.acme.recipes;


import org.openrewrite.*;
import org.openrewrite.text.PlainText;
import org.openrewrite.text.PlainTextVisitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetOfResourcesRecipe extends Recipe {

    static ImportsReport report = new ImportsReport(new SetOfResourcesRecipe());
    private static final Map<String, Set<String>> uniquePatterns = new HashMap<>();

    /**
     * A human-readable display name for the recipe, initial capped with no period.
     * For example, "Find text". The display name can be assumed to be rendered in
     * documentation and other places where markdown is understood, so it is possible
     * to use stylistic markers like backticks to indicate types. For example,
     * "Find uses of `java.util.List`".
     *
     * @return The display name.
     */
    @Override
    public @NlsRewrite.DisplayName String getDisplayName() {
        return "Set of resources";
    }

    /**
     * A human-readable description for the recipe, consisting of one or more full
     * sentences ending with a period.
     * <p>
     * "Find methods by pattern." is an example. The description can be assumed to be rendered in
     * documentation and other places where markdown is understood, so it is possible
     * to use stylistic markers like backticks to indicate types. For example,
     * "Find uses of `java.util.List`.".
     *
     * @return The display name.
     */
    @Override
    public @NlsRewrite.Description String getDescription() {
        return "Set of resources that need to be replaced";
    }

    /**
     * @return LibraryResourceVisitor
     */
    @Override
    public PlainTextVisitor<ExecutionContext> getVisitor() {
        return new LibraryResourceVisitor();
    }

    private static class LibraryResourceVisitor extends PlainTextVisitor<ExecutionContext> {

        Set<String> libraries = Set.of(
                "bootstrap.min.css","bootstrap.min.js","jquery.min.js","jquery-ui.css","ui.jqgrid-bootstrap.css",
                "jquery-1.11.0.min.js","jquery-ui.min.js","grid.locale-es.js","jquery.jqGrid.js","datepicker-es.js",
                "jszip.min.js","jquery.format.js","recaptcha_api.js","jquery-ui-timepicker-addon.js","tpstyle.js",
                "bootstrap-session-timeout.min.js","easy-autocomplete.css","jquery.easy-autocomplete.js","tpstyle.css",
                "bootstrap-toggle.min.css","bootstrap-toggle.min.js","moment.js","bootstrap-multiselect.css",
                "bootstrap-multiselect.min.js","jquery.rateit.js","rateit.css","tpstyle-icons.ttf"
        );

        /**
         * @param sourceFile SourceFile
         * @param ctx ExecutionContext
         * @return true if file is a JSP located in WEB-INF, false otherwise
         */
        @Override
        public boolean isAcceptable(SourceFile sourceFile, ExecutionContext ctx) {
            if (sourceFile instanceof PlainText) {
                String path = sourceFile.getSourcePath().toString();
                return path.endsWith(".jsp") && path.contains("WEB-INF");
            }
            return false;
        }

        /**
         * @param text PlainText
         * @param ctx ExecutionContext
         * @return text
         */
        @Override
        public PlainText visitText(PlainText text, ExecutionContext ctx) {
            String content = text.getText();
            String[] lines = content.split("\n");
            for (String line : lines) {
                String trimmedLine = line.trim();
                for (String library : libraries) {
                    if (trimmedLine.contains(library) &&
                            !(trimmedLine.startsWith("<%--") || trimmedLine.startsWith("<!--"))) {
                        // Determine tag type
                        String tagType = trimmedLine.contains("<script") ? "script" :
                                trimmedLine.contains("<link") ? "link" : null;
                        if (tagType != null) {
                            // Initialize set for this library if it doesn't exist
                            uniquePatterns.computeIfAbsent(library, k -> new HashSet<>());
                            // If this is a new pattern for this library
                            if (uniquePatterns.get(library).add(trimmedLine)) {
                                // Only add to report if it's a new pattern
                                report.insertRow(ctx, new ImportsReport.Row(
                                        library,
                                        tagType,
                                        trimmedLine,
                                        uniquePatterns.get(library).size() // Pattern variant number
                                ));
                            }
                        }
                        break;
                    }
                }
            }
            return text;
        }
    }

}
