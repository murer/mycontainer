package com.googlecode.mycontainer.commons.httpclient;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.mycontainer.commons.httpclient.JsonProtocol;

public class JsonProtocolTest {

	private JsonProtocol protocol;

	@Before
	public void setUp() {
		protocol = new JsonProtocol();
	}

	@Test
	public void testParseFormatCallback() {
		assertJsonProtocolParse("mypack.myobj.myfunc",
				"{\"id\":4,\"name\":\"myname\"}",
				"mypack.myobj.myfunc({id:4,name:'myname'})");
		assertJsonProtocolParse("mypack.myobj.myfunc",
				"{\"id\":4,\"name\":\"myname\"}",
				"mypack.myobj.myfunc({id:4,name:'myname'});");
		assertJsonProtocolParse("mypack.myobj.myfunc",
				"{\"id\":4,\"name\":\"myname\"}",
				"  	 mypack.myobj.myfunc 	 (	{id:4,name:'myname'}   )  ;   ");
		assertJsonProtocolParse("mypack.myobj.myfunc", "\"test\"",
				"   mypack.myobj.myfunc  (  'test'  ) ;  ");
		assertJsonProtocolParse("mypack.myobj.myfunc", "123",
				"   mypack.myobj.myfunc  (  123  )  ");
		assertJsonProtocolParse("mypack.myobj.myfunc", "123",
				"   mypack.myobj.myfunc  (  123  ) ; ");
		assertJsonProtocolParse("mypack.myobj.myfunc", "null",
				"   mypack.myobj.myfunc  (  null  ) ; ");
		assertJsonProtocolParse("mypack.myobj.myfunc", "true",
				"   mypack.myobj.myfunc  (  true  ) ; ");
	}

	@Test
	public void testParseFormatNotCallback() {
		assertJsonProtocolParse(null, "{\"id\":4,\"name\":\"myname\"}",
				"{id:4,name:'myname'}");
		assertJsonProtocolParse(null, "{\"id\":4,\"name\":\"myname\"}",
				"   {  id :   4 ,   name :   'myname'  }");
		assertJsonProtocolParse(null, "true", "   true  ");
		assertJsonProtocolParse(null, "123", "   123  ");
		assertJsonProtocolParse(null, "\"test\"", "   'test'  ");
		assertJsonProtocolParse(null, "null", "   null  ");
		assertJsonProtocolParse(null, "{}", "   {}  ");
	}

	private void assertJsonProtocolParse(String func, String json, String text) {
		protocol.parse(text);
		assertEquals(func, protocol.getCallback());
		assertEquals(json, protocol.formatJson());
	}

}
