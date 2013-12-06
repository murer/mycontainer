#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.mycontainer.rs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class App extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		return new HashSet<Class<?>>();
	}

	@Override
	public Set<Object> getSingletons() {
		Set<Object> objects = new HashSet<Object>();
		objects.add(new SampleRS());
		return objects;
	}
}
