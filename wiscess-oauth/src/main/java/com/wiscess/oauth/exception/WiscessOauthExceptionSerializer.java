package com.wiscess.oauth.exception;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@SuppressWarnings("serial")
public class WiscessOauthExceptionSerializer extends StdSerializer<WiscessOAuth2Exception> {
    protected WiscessOauthExceptionSerializer() {
        super(WiscessOAuth2Exception.class);
    }

    @Override
    public void serialize(WiscessOAuth2Exception e, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        e.getResponse().serializeResponse(e, generator);
        generator.writeEndObject();
    }

}
