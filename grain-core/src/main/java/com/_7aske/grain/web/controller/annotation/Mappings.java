package com._7aske.grain.web.controller.annotation;

import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.web.http.HttpMethod;

import java.lang.reflect.AnnotatedElement;

public class Mappings {
    private Mappings() {}

    /**
     * Extracts handler path from any of the valid @RequestMapping annotations.
     *
     * @param element to extract the path from
     * @return extracted request handler path. Throws if the annotation is not found
     */
    public static <T extends AnnotatedElement> String getAnnotatedHttpPath(T element) {
        if (element.isAnnotationPresent(RequestMapping.class))
            return element.getAnnotation(RequestMapping.class).value();
        if (element.isAnnotationPresent(GetMapping.class))
            return element.getAnnotation(GetMapping.class).value();
        if (element.isAnnotationPresent(PostMapping.class))
            return element.getAnnotation(PostMapping.class).value();
        if (element.isAnnotationPresent(PutMapping.class))
            return element.getAnnotation(PutMapping.class).value();
        if (element.isAnnotationPresent(DeleteMapping.class))
            return element.getAnnotation(DeleteMapping.class).value();
        if (element.isAnnotationPresent(PatchMapping.class))
            return element.getAnnotation(PatchMapping.class).value();
        if (element.isAnnotationPresent(HeadMapping.class))
            return element.getAnnotation(HeadMapping.class).value();
        if (element.isAnnotationPresent(TraceMapping.class))
            return element.getAnnotation(TraceMapping.class).value();

        throw new GrainRuntimeException("Method not annotated with a valid @RequestMapping annotation");
    }


    /**
     * Extracts http handler method from any of the valid @RequestMapping annotations.
     *
     * @param element to extract the http method from
     * @return extracted request handler path. Throws if the annotation is not found
     */
    public static <T extends AnnotatedElement> HttpMethod[] getAnnotatedHttpMethods(T element) {
        if (element.isAnnotationPresent(RequestMapping.class))
            return element.getAnnotation(RequestMapping.class).method();
        if (element.isAnnotationPresent(GetMapping.class))
            return element.getAnnotation(GetMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
        if (element.isAnnotationPresent(PostMapping.class))
            return element.getAnnotation(PostMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
        if (element.isAnnotationPresent(PutMapping.class))
            return element.getAnnotation(PutMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
        if (element.isAnnotationPresent(DeleteMapping.class))
            return element.getAnnotation(DeleteMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
        if (element.isAnnotationPresent(PatchMapping.class))
            return element.getAnnotation(PatchMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
        if (element.isAnnotationPresent(HeadMapping.class))
            return element.getAnnotation(HeadMapping.class).annotationType().getAnnotation(RequestMapping.class).method();
        if (element.isAnnotationPresent(TraceMapping.class))
            return element.getAnnotation(TraceMapping.class).annotationType().getAnnotation(RequestMapping.class).method();

        throw new GrainRuntimeException("Method not annotated with a valid @RequestMapping annotation");
    }
}
