package org.eclipse.sed.ifl.model.score.history;

import java.time.LocalDateTime;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class Monument<TScore extends Defineable<Double>, TSubject extends IMethodDescription, TCause extends IUserFeedback> {
	private TScore newScore;
	private TScore oldScore;
	private TSubject subject;
	private TCause cause;
	private LocalDateTime creation = LocalDateTime.now();
	
	public LocalDateTime getCreation() {
		return creation;
	}
	
	public TScore getNewScore() {
		return newScore;
	}
	public TScore getOldScore() {
		return oldScore;
	}
	public TSubject getSubject() {
		return subject;
	}
	public TCause getCause() {
		return cause;
	}
	
	public double getChange() {
		return newScore.getValue() - oldScore.getValue();
	}
	
	public Monument(TScore newScore, TScore oldScore, TSubject subject, TCause cause) {
		super();
		this.newScore = newScore;
		this.oldScore = oldScore;
		this.subject = subject;
		this.cause = cause;
	}
}
