package org.eclipse.sed.ifl.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.sed.ifl.control.score.Score;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ElementDeserializer extends StdDeserializer<Map<?, ?>>{

	private static final long serialVersionUID = -7607485245438141278L;

	public ElementDeserializer() { 
        this(null); 
    } 
	
	protected ElementDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Map<?, ?> deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		Map<ScoreLoaderEntry, Score> entries = new HashMap<>();
		JsonNode node = jp.getCodec().readTree(jp);
	
		node.elements().forEachRemaining(methodNode -> {
			
			Score score = new Score(methodNode.get("score").asDouble());
			boolean interactivity = methodNode.get("interactivity").asText().contains("enabled") ? true : false;
			ScoreLoaderEntry entry = new ScoreLoaderEntry(methodNode.get("signature").asText(), methodNode.get("detailsLink") != null ? methodNode.get("detailsLink").asText() : null, interactivity);	
			
			entries.put(entry, score);
		});
		
        return entries;
	}	
}
