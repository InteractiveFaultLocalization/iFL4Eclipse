package org.eclipse.sed.ifl.model.user.identification;

public class DefaultUser implements IUser {

	@Override
	public String getRealName() {
		return "Default User";
	}

	@Override
	public String getUserID() {
		return "default";
	}

}
