package com.kdt.taskflow.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlexibleStringListDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        if (node == null || node.isNull()) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode elem : node) {
                if (elem != null && !elem.isNull()) {
                    result.add(elem.asText());
                }
            }
        } else if (node.isObject()) {
            // Handle { strengths: ["...", "..."] } or other nested structures
            if (node.has("strengths") && node.get("strengths").isArray()) {
                for (JsonNode elem : node.get("strengths")) {
                    if (elem != null && !elem.isNull()) {
                        result.add(elem.asText());
                    }
                }
            } else {
                Iterator<JsonNode> elements = node.elements();
                while (elements.hasNext()) {
                    JsonNode elem = elements.next();
                    if (elem.isArray()) {
                        for (JsonNode subElem : elem) {
                            if (subElem != null && !subElem.isNull()) {
                                result.add(subElem.asText());
                            }
                        }
                    } else if (!elem.isNull()) {
                        result.add(elem.asText());
                    }
                }
            }
        } else if (node.isTextual()) {
            String text = node.asText().trim();
            if (text.startsWith("{") && text.endsWith("}")) {
                // Postgres array literal: {"a","b"} or {"a", "b"} or {a,b}
                String inner = text.substring(1, text.length() - 1).trim();
                if (!inner.isEmpty()) {
                    String[] parts = inner.split(",");
                    for (String part : parts) {
                        String cleaned = part.trim();
                        if (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length() >= 2) {
                            cleaned = cleaned.substring(1, cleaned.length() - 1);
                        }
                        if (!cleaned.isEmpty()) {
                            result.add(cleaned);
                        }
                    }
                }
            } else if (text.contains("\n")) {
                for (String line : text.split("\n")) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) {
                        result.add(trimmed);
                    }
                }
            } else if (!text.isEmpty()) {
                result.add(text);
            }
        }

        return result;
    }
}
