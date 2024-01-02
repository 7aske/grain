package com._7aske.grain.web.http.codec.json;

import com._7aske.grain.web.http.codec.json.nodes.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class JsonWriter {
    private final boolean prettyPrint;
    private int indentSize = 2;

    public JsonWriter(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public JsonWriter(boolean prettyPrint, int indentSize) {
        this.prettyPrint = prettyPrint;
        this.indentSize = indentSize;
    }

    public String write(JsonNode node) throws IOException {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             Writer writer = new PrintWriter(outputStream)) {
            write(node, writer, 0);
            writer.flush();
            return outputStream.toString(StandardCharsets.UTF_8);
        }
    }

    public void write(JsonNode node, OutputStream outputStream) throws IOException {
        try (Writer writer = new OutputStreamWriter(outputStream)) {
            write(node, writer, 0);
        }
    }

    private void write(JsonNode node, Writer writer, int indent) throws IOException {
        if (node == null || node instanceof JsonNullNode) {
            writer.write("null");
            return;
        }

        if (node instanceof JsonObjectNode object) {
            writeObject(object, writer, indent + 1);
        } else if (node instanceof JsonArrayNode array) {
            writeArray(array, writer, indent + 1);
        } else if (node instanceof JsonStringNode string) {
            writeString(string, writer, indent + 1);
        } else if (node instanceof JsonBooleanNode bool) {
            writeBoolean(bool, writer, indent + 1);
        } else if (node instanceof JsonNumberNode num) {
            writeNumber(num, writer, indent + 1);
        } else {
            throw new IllegalStateException("Unexpected value: " + node.getClass().getName());
        }
    }

    private void writeNumber(JsonNumberNode num, Writer writer, int i) throws IOException {
        writer.write(num.getValue().toString());
    }

    private void writeBoolean(JsonBooleanNode bool, Writer writer, int i) throws IOException {
        writer.write(bool.getValue().toString());
    }

    private void writeString(JsonStringNode string, Writer writer, int i) throws IOException {
        writer.write("\"");
        writer.write(string.getValue().toString());
        writer.write("\"");
    }

    private void writeArray(JsonArrayNode array, Writer writer, int indent) throws IOException {
        writer.write("[");
        if (prettyPrint) {
            writer.write("\n");
        }

        for (int i = 0; i < array.size(); i++) {
            if (prettyPrint) {
                indent(writer, indent);
            }

            write(array.get(i), writer, indent);

            if (i == array.size() - 1) {
                if (prettyPrint) {
                    writer.write("\n");
                }
                break;
            }

            if (prettyPrint) {
                writer.write(",\n");
            } else {
                writer.write(",");
            }
        }

        if (prettyPrint) {
            indent(writer, indent - 1);
        }

        writer.write("]");
    }

    private void writeObject(JsonObjectNode object, Writer writer, int indent) throws IOException {
        writer.write("{");
        if (prettyPrint) {
            writer.write("\n");
        }

        Set<String> keys = object.keySet();
        int i = 0;
        for (String key : keys) {
            if (prettyPrint) {
                indent(writer, indent);
            }

            writer.write("\"");
            writer.write(key);
            writer.write("\": ");
            write(object.get(key), writer, indent);

            if (i == keys.size() - 1) {
                if (prettyPrint) {
                    writer.write("\n");
                }
                break;
            }

            if (prettyPrint) {
                writer.write(",\n");
            } else {
                writer.write(",");
            }
            i++;
        }

        if (prettyPrint) {
            indent(writer, indent - 1);
        }

        writer.write("}");

    }

    private void indent(Writer writer, int indent) throws IOException {
        for (int i = 0; i < indent; i++) {
            for (int j = 0; j < indentSize; j++) {
                writer.write(" ");
            }
        }
    }
}
