package edu.school21.annotation.processor;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;


@SupportedAnnotationTypes("edu.school21.annotation.processor.HtmlForm")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class HtmlProcessor extends AbstractProcessor {

    private static final String FORM_TEMPLATE =
            "<form action=\"%s\" method=\"%s\">\n%s\n</form>";

    private static final String INPUT_TEMPLATE =
            "<input type=\"%s\" name=\"%s\" placeholder=\"%s\">";

    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedElements
                = roundEnv.getElementsAnnotatedWith(HtmlForm.class);
        for (Element element : annotatedElements) {
            createHtmlFile(element);
        }
        return false;
    }

    private void createHtmlFile(final Element element) {
        HtmlForm htmlForm = element.getAnnotation(HtmlForm.class);
        try {
            FileObject fileObject = processingEnv.getFiler().createResource(
                    StandardLocation.CLASS_OUTPUT, "", htmlForm.fileName());
            try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
                StringBuilder inputs = new StringBuilder();
                List<? extends Element> annotatedFields = element.getEnclosedElements();
                for (Element elementField : annotatedFields) {
                    HtmlInput htmlInput = elementField.getAnnotation(HtmlInput.class);
                    if (htmlInput != null) {
                        inputs.append(String.format(INPUT_TEMPLATE,
                                htmlInput.type(),
                                htmlInput.name(),
                                htmlInput.placeholder()));
                        inputs.append("\n");
                    }
                }
                writer.println(String.format(FORM_TEMPLATE,
                        htmlForm.action(),
                        htmlForm.method(),
                        inputs.toString()));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
