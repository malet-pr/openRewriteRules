package org.acme.recipes;

import com.google.errorprone.refaster.annotation.AfterTemplate;
import com.google.errorprone.refaster.annotation.BeforeTemplate;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.NlsRewrite;
import org.openrewrite.Recipe;

@Value
@EqualsAndHashCode(callSuper = false)
public class BootstrapCss extends Recipe {

    @BeforeTemplate
    boolean changeBootsrapCss(String string) {
        String bootstrapCss = "<link rel=\"stylesheet\" href=\"<c:url value='webjars/bootstrap/3.3.7-1/css/bootstrap.min.css'/>\" />;";
        return string.equals(bootstrapCss);
    }

    @AfterTemplate
    boolean optimizedMethod(String string) {
        String bootstrapCss = "<link rel=\"stylesheet\" type=\"text/css\" href=\"resources/librerias/bootstrap/3.3.7-1/css/bootstrap.min.css\" />;";
        return string.equals(bootstrapCss);
    }

    @Override
    public @NlsRewrite.DisplayName String getDisplayName() {
        return "Link to bootstrap css";
    }

    @Override
    public @NlsRewrite.Description String getDescription() {
        return "Updates the link to bootstrap css in all jsp pages";
    }
}