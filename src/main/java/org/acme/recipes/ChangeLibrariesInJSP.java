package org.acme.recipes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.openrewrite.*;
import org.openrewrite.xml.*;
import org.openrewrite.xml.tree.Xml;
import java.util.*;

public class ChangeLibrariesInJSP extends Recipe {
    private final List<ResourceMapping> mappings;

    public ChangeLibrariesInJSP(List<ResourceMapping> mappings) {
        this.mappings = mappings;
    }

    @Override
    public String getDisplayName() {
        return "Replace third-party library resource tags";
    }

    @Override
    public String getDescription() {
        return "Replace webjars and JSTL-based library imports with direct resource paths";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new LibraryResourceVisitor(mappings);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ResourceMapping {
        private String libraryName;
        private String oldPath;
        private String newPath;

    }

    private static class LibraryResourceVisitor extends XmlVisitor<ExecutionContext> {
        private final List<ResourceMapping> mappings;

        public LibraryResourceVisitor(List<ResourceMapping> mappings) {
            this.mappings = mappings;
        }

        @Override
        public Xml visitTag(Xml.Tag tag, ExecutionContext ctx) {
            String currentMarkup = tag.printTrimmed(getCursor());  // needs a Cursor object
            for (ResourceMapping mapping : mappings) {
                if (currentMarkup.contains(mapping.getOldPath())) {
                    try {
                        return Xml.Tag.build(mapping.getNewPath());
                    } catch (Exception e) {
                        ctx.getOnError().accept(e);
                        return tag;
                    }
                }
            }
            return super.visitTag(tag, ctx);
        }
    }
}