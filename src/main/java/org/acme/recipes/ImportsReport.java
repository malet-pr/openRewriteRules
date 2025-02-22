package org.acme.recipes;

import lombok.Value;
import org.openrewrite.Column;
import org.openrewrite.DataTable;
import org.openrewrite.Recipe;

public class ImportsReport extends DataTable<ImportsReport.Row> {

    public ImportsReport(Recipe recipe) {
        super(recipe,
                "Imports report",
                "List of link and script tags in JSP files.");
    }

    @Value
    public static class Row {
        @Column(displayName = "Library",
                description = "Library Name.")
        String libraryName;
        @Column(displayName = "Tag",
                description = "Tag type (script or link).")
        String tag;
        @Column(displayName = "Pattern",
                description = "The exact tag pattern used to import this library.")
        String tagContent;
        @Column(displayName = "Variant",
                description = "Pattern variant number for this library.")
        int variantNumber;
    }

}

