package org.eclipse.sed.ifl.model.monitor.resource;

import java.util.HashMap;
import java.util.Map;

public class User extends Resource {

	public User(String fullName) {
		super(fullName, new HashMap<>());
	}

}
