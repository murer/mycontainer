package com.googlecode.mycontainer.commons.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class XMLQueryTest {

	@Test
	public void testClone() {
		XMLQuery q = new XMLQuery();
		q = q.append("<r><a><inner>1</inner><inner>2</inner></a><b v=\"v1\"/><c v=\"v2\">i</c></r>");
		assertEquals("<r><a><inner>1</inner><inner>2</inner></a><b v=\"v1\"/><c v=\"v2\">i</c></r>", q.toXML());

		XMLQuery q1 = q.clone();
		XMLQuery q2 = q.clone();
		assertEquals("<r><a><inner>1</inner><inner>2</inner></a><b v=\"v1\"/><c v=\"v2\">i</c></r>", q1.toXML());
		assertEquals("<r><a><inner>1</inner><inner>2</inner></a><b v=\"v1\"/><c v=\"v2\">i</c></r>", q2.toXML());
		assertFalse(q1.equals(q2));

		assertEquals(2, q1.find("//inner").size());
	}

	@Test
	public void testFind() {
		XMLQuery q = new XMLQuery(
				"<r><a><inner>1</inner><inner>2</inner></a><b v=\"v1\"/><c v=\"v2\">i</c><d><inner>3</inner></d></r>");

		assertEquals(
				"<r><a><inner>1</inner><inner>2</inner></a><b v=\"v1\"/><c v=\"v2\">i</c><d><inner>3</inner></d></r>",
				q.clone().find("//r").toXML());
		assertEquals(
				"<r><a><inner>1</inner><inner>2</inner></a><b v=\"v1\"/><c v=\"v2\">i</c><d><inner>3</inner></d></r>",
				q.clone().find("./r").toXML());
		assertEquals(
				"<r><a><inner>1</inner><inner>2</inner></a><b v=\"v1\"/><c v=\"v2\">i</c><d><inner>3</inner></d></r>",
				q.clone().find("r").toXML());
		assertEquals(
				"<r><a><inner>1</inner><inner>2</inner></a><b v=\"v1\"/><c v=\"v2\">i</c><d><inner>3</inner></d></r>",
				q.clone().find("/r").toXML());
		assertEquals("<a><inner>1</inner><inner>2</inner></a>", q.clone().find("//a").toXML());
		assertEquals("<inner>1</inner><inner>2</inner><inner>3</inner>", q.clone().find("//inner").toXML());
		assertEquals("<inner>2</inner>", q.clone().find("//inner[2]").toXML());
		assertEquals("<inner>1</inner><inner>2</inner>", q.clone().find("//a/inner").toXML());
		assertEquals("<inner>1</inner><inner>2</inner>", q.clone().find("//a").find("./inner").toXML());
		assertEquals("<inner>1</inner><inner>2</inner>", q.clone().find("//a").find(".//inner").toXML());
		assertEquals("<inner>1</inner><inner>2</inner><inner>3</inner>", q.clone().find("//r").find(".//inner").toXML());

		assertEquals("<b v=\"v1\"/><c v=\"v2\">i</c>", q.clone().find("//*[@v]").toXML());
		assertEquals("[v=\"v1\", v=\"v2\"]", q.clone().find("//*/@v").toString());
		assertEquals("", q.clone().find("//*/@v").find("./a").toXML());

		assertEquals("<b v=\"v1\"/>", q.clone().find("//b").find(".").toXML());
		assertEquals("[v=\"v1\"]", q.clone().find("//b").find("./@*").toString());
		assertEquals("[v=\"v1\"]", q.clone().find("//b").find("@*").toString());

	}

	@Test
	public void testContent() {
		XMLQuery q = new XMLQuery("<r><a><inner x=\"v1\" y=\"v2\">1</inner></a></r>");
		assertEquals("v1", q.clone().find("//@x").text());
		assertEquals("v2", q.clone().find("//@y").text());
		assertEquals("1", q.clone().find("//inner/text()").text());

	}

	@Test
	public void testFindMixed() {
		XMLQuery q = new XMLQuery("<r><a>mixed<b>start<c>middle</c>end</b></a></r>");
		assertNull(q.clone().find("//c").text());
		assertEquals("middle", q.clone().find("//c//text()").text());
		assertNull(q.clone().find("//b").text());
		assertEquals("start end", q.clone().find("//b/node()").text());
		assertEquals("start middle end", q.clone().find("//b//node()").text());
		assertNull(q.clone().find("//a").text());
		assertEquals("mixed", q.clone().find("//a/node()").text());
		assertEquals("mixed", q.clone().find("//a/text()").text());
		assertEquals("mixed start middle end", q.clone().find("//a//node()").text());
		assertEquals("mixed start middle end", q.clone().find("//a//text()").text());
	}

}
