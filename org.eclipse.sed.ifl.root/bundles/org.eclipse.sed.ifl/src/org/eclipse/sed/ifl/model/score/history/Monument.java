package org.eclipse.sed.ifl.model.score.history;

import java.time.LocalDateTime;

import org.eclipse.sed.ifl.ide.gui.icon.ScoreStatus;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

import main.java.org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public class Monument<TScore extends Defineable<Double>, TSubject extends IMethodDescription, TCause extends IUserFeedback> {
	private TScore newScore;
	private TScore oldScore;
	private TSubject subject;
	private TCause cause;
	private ScoreStatus changeDirection;
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
	
	private double getChangeDifference() {
		return newScore.getValue() - oldScore.getValue();
	}
	
	public ScoreStatus getChange() {
		return changeDirection;
	}
	
	public Monument(TScore newScore, TScore oldScore, TSubject subject, TCause cause) {
		super();
		this.newScore = newScore;
		this.oldScore = oldScore;
		this.subject = subject;
		this.cause = cause;
		if(getChangeDifference() > 0.0) {
			this.changeDirection = ScoreStatus.INCREASED;
		} else if (getChangeDifference() < 0.0) {
			this.changeDirection = ScoreStatus.DECREASED;
		} else if (getChangeDifference() == 0.0) {
			this.changeDirection = ScoreStatus.UNDEFINED;
		}
	}
}
