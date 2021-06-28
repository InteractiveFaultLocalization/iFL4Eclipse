package org.eclipse.sed.ifl.commons.model.source;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class MethodTest {
	
	private MethodIdentity methodIdentity;
	private CodeChunkLocation codeChunkLocation;

	@Before
	public void setUp() throws Exception {
		methodIdentity = new MethodIdentity("", "", "", "", "");
		codeChunkLocation = new CodeChunkLocation("", new Position(0), new Position(0));
	}

	@Test
	public void testAddLine() {
		IMethodDescription method = new Method(methodIdentity, codeChunkLocation);
		Score score1 = new Score(1.0);
		Score score2 = new Score(1.5);
		method.addLine(1, score1);
		method.addLine(2, score2);
		Map<Line, Score> lines = method.getLines();
		assertEquals(2, lines.size());
		Line line1 = new Line(1, method);
		Line line2 = new Line(2, method);
		assertTrue(lines.containsKey(line1));
		assertTrue(lines.containsKey(line2));
	}
	
	@Test
	public void testRemoveLine() {
		IMethodDescription method = new Method(methodIdentity, codeChunkLocation);
		Score score1 = new Score(1.0);
		Score score2 = new Score(1.5);
		method.addLine(1, score1);
		method.addLine(2, score2);
		Map<Line, Score> lines = method.getLines();
		assertEquals(2, lines.size());
		method.removeLine(2);
		Line line1 = new Line(1, method);
		Line line2 = new Line(2, method);
		assertTrue(lines.containsKey(line1));
		assertFalse(lines.containsKey(line2));
	}
	
	@Test
	public void testUpdateLine() {
		IMethodDescription method = new Method(methodIdentity, codeChunkLocation);
		Score score1 = new Score(1.0);
		Score score2 = new Score(1.5);
		Score score3 = new Score(2.0);
		method.addLine(1, score1);
		method.addLine(2, score2);
		Map<Line, Score> lines = method.getLines();
		assertEquals(2, lines.size());
		Line line1 = new Line(1, method);
		Line line2 = new Line(2, method);
		assertTrue(lines.containsKey(line1));
		assertTrue(lines.containsKey(line2));
		assertEquals(score1, lines.get(line1));
		assertEquals(score2, lines.get(line2));
		method.addLine(1, score3);
		assertTrue(lines.containsKey(line1));
		assertTrue(lines.containsKey(line2));
		assertNotEquals(score1, lines.get(line1));
		assertEquals(score2, lines.get(line2));
		assertEquals(score3, lines.get(line1));
	}

}
