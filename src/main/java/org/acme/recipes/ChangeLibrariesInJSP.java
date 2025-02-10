package org.acme.recipes;

import org.intellij.lang.annotations.Language;
import org.openrewrite.*;
import org.openrewrite.xml.*;
import org.openrewrite.xml.tree.Xml;

public class ChangeLibrariesInJSP extends Recipe {

    //String libraryName;
    static String oldPath;
    @Language("xml")
    static String newPath;

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
        return new LibraryResourceVisitor();
    }

    private static class LibraryResourceVisitor<ResourceMapping> extends XmlVisitor<ExecutionContext> {
        @Override
        public Xml visitTag(Xml.Tag tag, ExecutionContext ctx) {
            String currentMarkup = tag.printTrimmed(getCursor());  // needs a Cursor object
            if (currentMarkup.contains(oldPath)) {
                try {
                    return Xml.Tag.build(newPath);
                } catch (Exception e) {
                    ctx.getOnError().accept(e);
                    return tag;
                }
            }
            return super.visitTag(tag, ctx);
        }
    }
}