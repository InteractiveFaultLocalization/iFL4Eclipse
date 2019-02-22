package org.eclipse.sed.ifl.model.monitor.event;

import java.util.Map;

import org.eclipse.sed.ifl.model.monitor.resource.LineInfo;
import org.eclipse.sed.ifl.model.monitor.resource.Resource;
import org.eclipse.sed.ifl.model.source.ICodeChunkLocation;

public class NavigationEvent extends Event {
	private LineInfo target;

	public NavigationEvent(ICodeChunkLocation target) {
		super(Map.of());
		this.target = new LineInfo(
			target.getAbsolutePath() + ":" + target.getBegining().getOffset(),
			Map.of(
				"absolute_path", target.getAbsolutePath(),
				"position", target.getBegining().getOffset()));
	}
	
	@Override
	protected Map<Resource, String> createResources() {
		return Map.of(target, "target");
	}
}
