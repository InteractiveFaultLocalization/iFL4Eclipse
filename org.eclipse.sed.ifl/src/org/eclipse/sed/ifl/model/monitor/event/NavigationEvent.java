package org.eclipse.sed.ifl.model.monitor.event;

import java.util.Map;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.model.monitor.resource.Resource;
import org.eclipse.sed.ifl.model.monitor.resource.ScoreListEntry;
import org.eclipse.sed.ifl.model.source.IMethodDescription;

public class NavigationEvent extends Event {
	private ScoreListEntry target;

	public NavigationEvent(IMethodDescription target, Score score) {
		super(Map.of("score", score.isDefinit() ? score.getValue() : "unidefined"));
		this.target = new ScoreListEntry(target.getId().getKey(), Map.of());
	}
	
	@Override
	protected Map<Resource, String> createResources() {
		return Map.of(target, "target");
	}
}
