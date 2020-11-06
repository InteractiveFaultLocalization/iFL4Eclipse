package main.java.org.eclipse.sed.ifl.commons.model.source;

public class MethodIdentity {
	private String name;
	private String signature;
	private String parentType;
	private String returnType;
	private String key;
	
	public MethodIdentity(String name, String signature, String parentType, String returnType, String key) {
		super();
		this.name = name;
		this.signature = signature;
		this.parentType = parentType;
		this.returnType = returnType;
		this.key = key;
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
	
	public String getReturnType() {
		return returnType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parentType == null) ? 0 : parentType.hashCode());
		result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
		result = prime * result + ((signature == null) ? 0 : signature.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodIdentity other = (MethodIdentity) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentType == null) {
			if (other.parentType != null)
				return false;
		} else if (!parentType.equals(other.parentType))
			return false;
		if (returnType == null) {
			if (other.returnType != null)
				return false;
		} else if (!returnType.equals(other.returnType))
			return false;
		if (signature == null) {
			if (other.signature != null)
				return false;
		} else if (!signature.equals(other.signature))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MethodIdentity [name=" + name + ", signature=" + signature + ", parentType=" + parentType + ", returnType=" + returnType + "]";
	}
	
	public String toCSVKey() {
		return signature;
	}

	public String getKey() {
		return key;
	}
}
