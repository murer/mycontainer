#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.mycontainer.rs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("helloworld")
public class SampleRS {

	@GET
	public Response helloWorld() {
		return Response.ok("Hello World!").build();
	}
}
