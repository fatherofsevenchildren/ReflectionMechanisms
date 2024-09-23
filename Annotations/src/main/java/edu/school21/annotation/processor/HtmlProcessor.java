package edu.school21.annotation.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;


@SupportedAnnotationTypes("edu.school21.annotation.processor.HtmlForm")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class HtmlProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedElements
                = roundEnv.getElementsAnnotatedWith(HtmlForm.class);
        for (Element element : annotatedElements) {
            HtmlForm htmlForm = element.getAnnotation(HtmlForm.class);
            try {
                FileObject fileObject = processingEnv.getFiler().createResource(
                        StandardLocation.CLASS_OUTPUT, "", htmlForm.fileName());
                try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
                    writer.println("<form action = \"" + htmlForm.action() + "\" method = \"" + htmlForm.method() + "\">");
                    List<? extends Element> annotatedFields
                            = element.getEnclosedElements();
                    for (Element elementField : annotatedFields) {
                        HtmlInput htmlInput = elementField.getAnnotation(HtmlInput.class);
                        if (htmlInput != null) {
                            writer.println("<input type = \"" + htmlInput.type() + "\" name = \"" + htmlInput.name() + "\" placeholder = \"" + htmlInput.placeholder() + "\">");
                        }
                    }
                    writer.println("</form>");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}
