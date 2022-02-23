package org.eclipse.sed.ifl.scorelib;

public class ScoreVariables {
	private int executedPass, executedFail, nonExecutedPass, nonExecutedFail;

	public int getExecutedPass() {
		return executedPass;
	}

	public void setExecutedPass(int executedPass) {
		this.executedPass = executedPass;
	}

	public int getExecutedFail() {
		return executedFail;
	}

	public void setExecutedFail(int executedFail) {
		this.executedFail = executedFail;
	}

	public int getNonExecutedPass() {
		return nonExecutedPass;
	}

	public void setNonExecutedPass(int nonExecutedPass) {
		this.nonExecutedPass = nonExecutedPass;
	}

	public int getNonExecutedFail() {
		return nonExecutedFail;
	}

	public void setNonExecutedFail(int nonExecutedFail) {
		this.nonExecutedFail = nonExecutedFail;
	}
}
