package org.acme.recipes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.openrewrite.*;
import org.openrewrite.xml.*;
import org.openrewrite.xml.tree.Xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class ChangeLibrariesInJSP extends Recipe {

    static String libraryName;
    static String oldUrl;
    static String newUrl;

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
        XmlVisitor<ExecutionContext> xmlVisitor = new XmlVisitor<ExecutionContext>() {
            @Override
            public boolean isAcceptable(SourceFile sourceFile, ExecutionContext ctx) {
                String path = sourceFile.getSourcePath().toString();
                boolean isJsp = path.endsWith(".jsp");
                boolean isInWebInf = path.contains("WEB-INF");
                System.out.println("Checking file: " + path + " (isJsp: " + isJsp + ", isInWebInf: " + isInWebInf + ")");
                return isJsp && isInWebInf;
            }
            @Override
            public Xml visitTag(Xml.Tag tag, ExecutionContext ctx) {
                libraryName = "bootstrap.css";
                oldUrl = "<link rel=\"stylesheet\" href=\"<c:url value='webjars/bootstrap/3.3.7-1/css/bootstrap.min.css'/>\" />;";
                newUrl = "<link rel=\"stylesheet\" type=\"text/css\" href=\"resources/librerias/bootstrap/3.3.7-1/css/bootstrap.min.css\" />;";
                System.out.println("***********************************************************************");
                System.out.println("Visiting tag: " + tag.getName() + " in " + getClass().getSimpleName());
                String currentMarkup = "";
                System.out.println("tag.getContent(): " + tag.getContent());
                // Only process <link> tags
                if (!"link".equals(tag.getName())) {
                    return super.visitTag(tag, ctx);
                }
                // Get the href attribute
                Optional<Xml.Attribute> hrefAttr = findAttribute(tag, "href");
                if (!hrefAttr.isPresent()) {
                    return super.visitTag(tag, ctx);
                }
                String href = hrefAttr.get().getValueAsString();
                System.out.println("Found href: " + href);
                // Check if this is our target library
                if (!href.contains(libraryName)) {
                    return super.visitTag(tag, ctx);
                }
                System.out.println("Found matching library reference");
                if (tag.getContent().contains("href") && tag.getContent().contains(libraryName)) {
                    currentMarkup = tag.getContent().toString();
                    System.out.println("currentMarkup: " + currentMarkup.toString());
                    System.out.println("Modifying tag...");
                    if (!currentMarkup.contains(oldUrl)) {
                        return super.visitTag(tag, ctx);
                    }
                }
                try {
                    // Create new attributes list
                    List<Xml.Attribute> newAttributes = new ArrayList<>(tag.getAttributes());
                    // Create new href attribute
                    String newHref = href.replace(oldUrl, newUrl);
                    Xml.Tag newTag = Xml.Tag.build(newUrl);
                    System.out.println("New tag created: {}" + newTag.getContent());
                    System.out.println("***********************************************************************");
                    return newTag;
                } catch (Exception e) {
                    System.out.println("Error modifying tag: " + e.getMessage());
                    System.out.println("***********************************************************************");
                    ctx.getOnError().accept(e);
                    return tag;
                }
            }
            private Optional<Xml.Attribute> findAttribute(Xml.Tag tag, String name) {
                return tag.getAttributes().stream()
                        .filter(attr -> name.equals(attr.getKeyAsString()))
                        .findFirst();
            }
        };
        return Preconditions.check(new FindSourceFiles("**/*.jsp"), xmlVisitor);
    }

    /*
    private static class LibraryResourceVisitor extends XmlVisitor<ExecutionContext> {
        public LibraryResourceVisitor() {}
        @Override
        public boolean isAcceptable(SourceFile sourceFile, ExecutionContext ctx) {
            String path = sourceFile.getSourcePath().toString();
            boolean isJsp = path.endsWith(".jsp");
            boolean isInWebInf = path.contains("WEB-INF");
            System.out.println("Checking file: " + path + " (isJsp: " + isJsp + ", isInWebInf: " + isInWebInf + ")");
            return isJsp && isInWebInf;
        }
        @Override
        public Xml visitTag(Xml.Tag tag, ExecutionContext ctx) {
            libraryName = "bootstrap.css";
            oldUrl = "<link rel=\"stylesheet\" href=\"<c:url value='webjars/bootstrap/3.3.7-1/css/bootstrap.min.css'/>\" />;";
            newUrl = "<link rel=\"stylesheet\" type=\"text/css\" href=\"resources/librerias/bootstrap/3.3.7-1/css/bootstrap.min.css\" />;";
            System.out.println("***********************************************************************");
            System.out.println("Visiting tag: " + tag.getName() + " in " + getClass().getSimpleName());
            String currentMarkup = "";
            System.out.println("tag.getContent(): " + tag.getContent());
            // Only process <link> tags
            if (!"link".equals(tag.getName())) {
                return super.visitTag(tag, ctx);
            }
            // Get the href attribute
            Optional<Xml.Attribute> hrefAttr = findAttribute(tag, "href");
            if (!hrefAttr.isPresent()) {
                return super.visitTag(tag, ctx);
            }
            String href = hrefAttr.get().getValueAsString();
            System.out.println("Found href: " + href);
            // Check if this is our target library
            if (!href.contains(libraryName)) {
                return super.visitTag(tag, ctx);
            }
            System.out.println("Found matching library reference");
            if(tag.getContent().contains("href") && tag.getContent().contains(libraryName)){
                currentMarkup = tag.getContent().toString();
                System.out.println("currentMarkup: " + currentMarkup.toString());
                System.out.println("Modifying tag...");
                if (!currentMarkup.contains(oldUrl)) {
                    return super.visitTag(tag, ctx);
                }
            }
            try {
                // Create new attributes list
                List<Xml.Attribute> newAttributes = new ArrayList<>(tag.getAttributes());
                // Create new href attribute
                String newHref = href.replace(oldUrl, newUrl);
                Xml.Tag newTag = Xml.Tag.build(newUrl);
                System.out.println("New tag created: {}" + newTag.getContent());
                System.out.println("***********************************************************************");
                return newTag;
            } catch (Exception e) {
                System.out.println("Error modifying tag: " + e.getMessage());
                System.out.println("***********************************************************************");
                ctx.getOnError().accept(e);
                return tag;
            }
        }
        private Optional<Xml.Attribute> findAttribute(Xml.Tag tag, String name) {
            return tag.getAttributes().stream()
                    .filter(attr -> name.equals(attr.getKeyAsString()))
                    .findFirst();
        }
    }
     */
}