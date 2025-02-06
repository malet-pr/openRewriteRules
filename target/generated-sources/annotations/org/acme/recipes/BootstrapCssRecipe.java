package org.acme.recipes;

import org.jspecify.annotations.NullMarked;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Preconditions;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.search.*;
import org.openrewrite.java.template.Primitive;
import org.openrewrite.java.template.function.*;
import org.openrewrite.java.template.internal.AbstractRefasterJavaVisitor;
import org.openrewrite.java.tree.*;

import javax.annotation.Generated;
import java.util.*;

import static org.openrewrite.java.template.internal.AbstractRefasterJavaVisitor.EmbeddingOption.*;

/**
 * OpenRewrite recipe created for Refaster template {@code BootstrapCss}.
 */
@SuppressWarnings("all")
@NullMarked
@Generated("org.openrewrite.java.template.processor.RefasterTemplateProcessor")
public class BootstrapCssRecipe extends Recipe {

    /**
     * Instantiates a new instance.
     */
    public BootstrapCssRecipe() {}

    @Override
    public String getDisplayName() {
        return "Refaster template `BootstrapCss`";
    }

    @Override
    public String getDescription() {
        return "Recipe created for the following Refaster template:\n```java\n@Value\n@EqualsAndHashCode(callSuper = false)\npublic final class BootstrapCss extends Recipe {\n    \n    @BeforeTemplate\n    boolean changeBootsrapCss(String string) {\n        String bootstrapCss = \"<link rel=\\\"stylesheet\\\" href=\\\"<c:url value=\\'webjars/bootstrap/3.3.7-1/css/bootstrap.min.css\\'/>\\\" />;\";\n        return string.equals(bootstrapCss);\n    }\n    \n    @AfterTemplate\n    boolean optimizedMethod(String string) {\n        String bootstrapCss = \"<link rel=\\\"stylesheet\\\" type=\\\"text/css\\\" href=\\\"resources/librerias/bootstrap/3.3.7-1/css/bootstrap.min.css\\\" />;\";\n        return string.equals(bootstrapCss);\n    }\n    \n    @Override\n    @NlsRewrite.DisplayName\n    public String getDisplayName() {\n        return \"Link to bootstrap css\";\n    }\n    \n    @Override\n    @NlsRewrite.Description\n    public String getDescription() {\n        return \"Updates the link to bootstrap css in all jsp pages\";\n    }\n    \n    @java.lang.Override\n    @java.lang.SuppressWarnings(value = \"all\")\n    public java.lang.String toString() {\n        return \"BootstrapCss()\";\n    }\n    \n    @java.lang.Override\n    @java.lang.SuppressWarnings(value = \"all\")\n    public boolean equals(final java.lang.Object o) {\n        if (o == this) return true;\n        if (!(o instanceof BootstrapCss)) return false;\n        final BootstrapCss other = (BootstrapCss)o;\n        if (!other.canEqual((java.lang.Object)this)) return false;\n        return true;\n    }\n    \n    @java.lang.SuppressWarnings(value = \"all\")\n    protected boolean canEqual(final java.lang.Object other) {\n        return other instanceof BootstrapCss;\n    }\n    \n    @java.lang.Override\n    @java.lang.SuppressWarnings(value = \"all\")\n    public int hashCode() {\n        final int result = 1;\n        return result;\n    }\n}\n```\n.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        JavaVisitor<ExecutionContext> javaVisitor = new AbstractRefasterJavaVisitor() {
            final JavaTemplate changeBootsrapCss = JavaTemplate
                    .builder("String bootstrapCss = \"<link rel=\\\"stylesheet\\\" href=\\\"<c:url value=\\'webjars/bootstrap/3.3.7-1/css/bootstrap.min.css\\'/>\\\" />;\"")
                    .build();
            final JavaTemplate optimizedMethod = JavaTemplate
                    .builder("String bootstrapCss = \"<link rel=\\\"stylesheet\\\" type=\\\"text/css\\\" href=\\\"resources/librerias/bootstrap/3.3.7-1/css/bootstrap.min.css\\\" />;\"")
                    .build();

            @Override
            public J visitMethodInvocation(J.MethodInvocation elem, ExecutionContext ctx) {
                JavaTemplate.Matcher matcher;
                if ((matcher = changeBootsrapCss.matcher(getCursor())).find()) {
                    return embed(
                            optimizedMethod.apply(getCursor(), elem.getCoordinates().replace(), matcher.parameter(0)),
                            getCursor(),
                            ctx,
                            SHORTEN_NAMES, SIMPLIFY_BOOLEANS
                    );
                }
                return super.visitMethodInvocation(elem, ctx);
            }

        };
        return Preconditions.check(
                new UsesMethod<>("java.lang.String equals(..)", true),
                javaVisitor
        );
    }
}

