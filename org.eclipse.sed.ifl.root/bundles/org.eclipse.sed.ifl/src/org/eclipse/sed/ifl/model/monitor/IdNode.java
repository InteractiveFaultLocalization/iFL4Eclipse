package org.eclipse.sed.ifl.model.monitor;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.sed.ifl.model.monitor.resource.Resource;
import org.eclipse.sed.ifl.util.Maps;

public class IdNode extends Node {
	
	public IdNode(Map<String, Object> properties) {
		super(Maps.<String, Object>builder().put("id", determineMacAddress()).build());
	}

	@Override
	protected String getLabel() {
		// TODO Auto-generated method stub
		return "id";
	}
	
	@Override
	public Vertex createNode(GraphTraversalSource g) {
		Vertex idNode = super.createNode(g);
		
		// TODO
		
		return idNode;
	}

	
	private static String determineMacAddress() {
		byte[] macAddress = null;
		try {
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface ni = NetworkInterface.getByInetAddress(ip);
			macAddress = ni.getHardwareAddress();
		} catch (UnknownHostException e) {
			System.out.println("Could not determine ip address\n");
		} catch (SocketException e) {
			System.out.println("Could not access Socket\n");
		}
		return new String(macAddress);
	}
	
}
