package org.eclipse.sed.ifl.model.monitor;

import org.eclipse.sed.ifl.model.EmptyModel;
import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public class ActivityMonitorModel extends EmptyModel {
	@Override
	public void init() {
		try (GraphTraversalSource g = traversal().withRemote(DriverRemoteConnection.using("localhost", 8182, "g"))) {
			var bla = g.addV("event").property("type", "something").tryNext();
			var foo = g.V().toList();
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.init();
	}
}
