package org.eclipse.sed.ifl.util;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.control.score.Score;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ElementSerializer extends StdSerializer<Map<?, ?>>{

	private static final long serialVersionUID = 3250710585532381575L;

	public ElementSerializer() {
		super(Map.class, false);
	}

	@Override
	public void serialize(Map<?, ?> arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException {
		
		arg1.writeStartObject();
		
		for (Entry<?, ?> child : arg0.entrySet()) {
			serializeCodeElement(child, arg1, arg2);
		}
		
		arg1.writeEndObject();		
	}
	
	private void serializeCodeElement(Entry<?, ?> codeElement, JsonGenerator generator, SerializerProvider provider) throws IOException {
		generator.writeObjectFieldStart("method");
		IMethodDescription key = (IMethodDescription)codeElement.getKey();
		Score value = (Score)codeElement.getValue();
		generator.writeStringField("name", key.getId().getName());
		if (value.isDefinit()) {
			generator.writeNumberField("score", value.getValue());
		} else {
			generator.writeStringField("score", "undefined");
		}
		generator.writeStringField("signature", key.getId().getSignature());
		generator.writeStringField("parentType", key.getId().getParentType());
		generator.writeStringField("path", key.getLocation().getAbsolutePath());
		generator.writeNumberField("position", key.getLocation().getBegining().getOffset());
		generator.writeNumberField("contextSize", key.getContext().size());
		if (key.isInteractive()) {
			generator.writeStringField("interactivity", "User feedback enabled");
		} else {
			generator.writeStringField("interactivity", "User feedback disabled");
		}
		if (value.getLastAction() != null) {
			generator.writeStringField("lastAction", value.getLastAction().getChange().name());
		} else {
			generator.writeStringField("lastAction", "None");
		}
		generator.writeEndObject();
	}
}
