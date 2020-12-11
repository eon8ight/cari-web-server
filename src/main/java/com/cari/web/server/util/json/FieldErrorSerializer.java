package com.cari.web.server.util.json;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.FieldError;

@JsonComponent
public class FieldErrorSerializer extends StdSerializer<FieldError> {

    private static final long serialVersionUID = 4602695429907126627L;

    public FieldErrorSerializer() {
        this((Class<FieldError>) null);
    }

    public FieldErrorSerializer(Class<FieldError> t) {
        super(t);
    }

    protected FieldErrorSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    protected FieldErrorSerializer(JavaType type) {
        super(type);
    }

    protected FieldErrorSerializer(StdSerializer<?> src) {
        super(src);
    }

    @Override
    public void serialize(FieldError value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("field", value.getField());
        gen.writeStringField("message", value.getDefaultMessage());
        gen.writeEndObject();
    }
}
