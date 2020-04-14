package org.eclipse.sed.ifl.model.monitor;


import org.eclipse.sed.ifl.util.Maps;


public class IdNode extends Node {
	
	public IdNode(String macAddress, String userId, String scenarioId, String generatedId) {
		super(Maps.<String, Object>builder()
				.put("mac", macAddress)
				.put("userID", userId)
				.put("scenarioID", scenarioId)
				.put("generatedID", generatedId)
				.build());
	}
	
	@Override
	protected String getLabel() {
		return "id";
	}
	
	@Override
	public String toString() {
		return String.format("Event [getType()=%s, getCreation()=%s]"
				,getType(), getCreation());
	}

	
	
}
