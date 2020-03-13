package org.eclipse.sed.ifl.model.user.interaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.identification.IUser;

public class CustomUserFeedback implements IUserFeedback {
	
	public CustomUserFeedback(int selectedPercent, int contextPercent, int otherPercent, List<IMethodDescription> subjects, IUser user) {
		super();
		this.selectedPercent = selectedPercent;
		this.contextPercent = contextPercent;
		this.otherPercent = otherPercent;
		this.subjects.addAll(subjects);
		this.user = user;
	}

	private int selectedPercent;
	public int getSelectedPercent() {
		return selectedPercent;
	}

	public int getContextPercent() {
		return contextPercent;
	}

	public int getOtherPercent() {
		return otherPercent;
	}

	private int contextPercent;
	private int otherPercent;
	
	private IUser user;
	
	@Override
	public IUser getUser() {

		return user;
	}

	@Override
	public Option getChoise() {

		return null;
	}

	private List<IMethodDescription> subjects = new ArrayList<>();
	
	@Override
	public List<IMethodDescription> getSubjects() {
		return Collections.unmodifiableList(subjects);
	}

}
