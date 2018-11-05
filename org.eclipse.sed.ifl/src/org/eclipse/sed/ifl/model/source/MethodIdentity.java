package org.eclipse.sed.ifl.model.source;

public class MethodIdentity {
	private String name;
	private String signature;
	private String parentType;
	
	public MethodIdentity(String name, String signature, String parentType) {
		super();
		this.name = name;
		this.signature = signature;
		this.parentType = parentType;
	}

	public String getName() {
		return name;
	}
	
	public String getSignature() {
		return signature;
	}
	
	public String getParentType() {
		return parentType;
	}
}
