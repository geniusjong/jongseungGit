package com.cvo.web.common.data.parse;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.cvo.web.common.data.Box;

@SuppressWarnings("serial")
public class BoxDeserializer extends StdDeserializer<Box> {

	/**
	 * Instantiates a new box deserializer.
	 */
	public BoxDeserializer() {
		super(Box.class);
	}

	/* (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
	 */
	@Override
	public Box deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException, JsonProcessingException {
		if(JsonToken.START_OBJECT.equals(jsonParser.getCurrentToken())) {
			JsonDeserializer<Object> deserializer = ctx.findRootValueDeserializer(ctx.constructType(Box.class));
			Box box = (Box) deserializer.deserialize(jsonParser, ctx);
			return box;
		} else {
			throw ctx.mappingException("Expected to see an object or array, found " + jsonParser.getCurrentToken());
		}
	}

}
