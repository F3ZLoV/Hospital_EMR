package com.example.hospitalemr.service;

import jakarta.xml.bind.JAXBElement;
import org.docx4j.wml.ContentAccessor;

import java.util.ArrayList;
import java.util.List;

public class Docx4jUtils {
    public static <T> List<T> getAllElementsOfType(Object obj, Class<T> type) {
        List<T> result = new ArrayList<>();
        if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();
        if (type.isAssignableFrom(obj.getClass())) result.add(type.cast(obj));

        if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getAllElementsOfType(child, type));
            }
        }
        return result;
    }
}
