/*
 * Copyright 2008 Whohoo Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.googlecode.mycontainer.kernel.naming;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class MyContainerContext implements Context, Serializable {

	private static final long serialVersionUID = -8498064310465367403L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MyContainerContext.class);

	private final Map<String, Object> elements = new TreeMap<String, Object>();

	private final Map<String, Object> envs;

	private final NameParser nameParser = new MyNameParser();

	public MyContainerContext(Map<String, Object> envs) {
		this.envs = envs;
	}

	public Object addToEnvironment(String propName, Object propVal)
			throws NamingException {
		return this.envs.put(propName, propVal);
	}

	public void bind(Name name, Object obj) throws NamingException {
		String key = name.get(0);
		if (name.size() == 1) {
			if (elements.containsKey(key)) {
				throw new NameAlreadyBoundException(key);
			}
			if (LOG.isTraceEnabled()) {
				LOG.trace("Binding: " + key);
			}
			elements.put(key, obj);
			return;
		}

		name = name.getSuffix(1);

		Object o = lookupInstance(key, name);
		if (o != null && !(o instanceof Context)) {
			throw new NameAlreadyBoundException(key);
		}
		Context ctx = (Context) o;
		if (ctx == null) {
			ctx = createNewSubContext();
			if (LOG.isTraceEnabled()) {
				LOG.trace("Binding: " + key);
			}
			elements.put(key, ctx);
		}
		ctx.bind(name, obj);
	}

	protected MyContainerContext createNewSubContext() {
		return new MyContainerContext(envs);
	}

	public void bind(String name, Object obj) throws NamingException {
		bind(new CompositeName(name), obj);
	}

	public void close() throws NamingException {

	}

	public Name composeName(Name name, Name prefix) throws NamingException {
		throw new NamingException("not supported operation");
	}

	public String composeName(String name, String prefix)
			throws NamingException {
		throw new NamingException("not supported operation");
	}

	public Context createSubcontext(Name name) throws NamingException {
		MyContainerContext ctx = createNewSubContext();
		bind(name, ctx);
		return ctx;
	}

	public Context createSubcontext(String name) throws NamingException {
		return createSubcontext(new CompositeName(name));
	}

	private Context getContext(Name name) throws NamingException {
		if (name.size() == 1) {
			return this;
		}
		name = name.getPrefix(name.size() - 1);
		return (Context) lookup(name);
	}

	public void destroySubcontext(Name name) throws NamingException {
		Context ctx = getContext(name);
		String key = name.get(name.size());
		ctx.unbind(key);
	}

	public void destroySubcontext(String name) throws NamingException {
		destroySubcontext(new CompositeName(name));
	}

	public Hashtable<?, ?> getEnvironment() throws NamingException {
		return (Hashtable<?, ?>) envs;
	}

	public String getNameInNamespace() throws NamingException {
		throw new NamingException("not supported operation");
	}

	public NameParser getNameParser(Name name) throws NamingException {
		return nameParser;
	}

	public NameParser getNameParser(String name) throws NamingException {
		return getNameParser(new CompositeName(name));
	}

	public NamingEnumeration<NameClassPair> list(Name name)
			throws NamingException {
		throw new NamingException("not supported operation");
	}

	public NamingEnumeration<NameClassPair> list(String name)
			throws NamingException {
		return list(new CompositeName(name));
	}

	@SuppressWarnings("unchecked")
	public NamingEnumeration<Binding> listBindings(Name name)
			throws NamingException {
		if (name.isEmpty()) {
			List<Binding> bindings = new ArrayList<Binding>();
			for (Map.Entry<String, Object> entry : elements.entrySet()) {
				Binding binding = new Binding(entry.getKey(), entry.getValue());
				bindings.add(binding);
			}
			return new MyNamingEnumeration<Binding>(bindings.iterator());
		}
		Object obj = lookup(name);
		if (!(obj instanceof Context)) {
			throw new NamingException("Name is not context: " + name);
		}
		Context context = (Context) obj;
		NamingEnumeration<Binding> listBindings = context.listBindings("");
		return listBindings;
	}

	public NamingEnumeration<Binding> listBindings(String name)
			throws NamingException {
		return listBindings(new CompositeName(name));
	}

	public Object lookup(Name name) throws NamingException {
		if (name.isEmpty()) {
			return this;
		}

		String key = name.get(0);
		Name suffix = name.getSuffix(1);
		Object ret = lookupInstance(key, suffix);

		if (ret instanceof Context) {
			Context ctx = (Context) ret;
			if (!suffix.isEmpty()) {
				ret = ctx.lookup(suffix);
			}
		}

		if (ret == null) {
			throw new NameNotFoundException(name.toString());
		}

		return ret;
	}

	private Object lookupInstance(String key, Name suffix)
			throws NamingException {
		Object ret = elements.get(key);

		if (ret instanceof ObjectProvider) {
			ObjectProvider provider = (ObjectProvider) ret;
			ret = provider.provide(suffix);
		}

		return ret;
	}

	public Object lookup(String name) throws NamingException {
		return lookup(new CompositeName(name));
	}

	public Object lookupLink(Name name) throws NamingException {
		throw new NamingException("not supported operation");
	}

	public Object lookupLink(String name) throws NamingException {
		throw new NamingException("not supported operation");
	}

	public void rebind(Name name, Object obj) throws NamingException {
		unbind(name);
		bind(name, obj);
	}

	public void rebind(String name, Object obj) throws NamingException {
		rebind(new CompositeName(name), obj);
	}

	public Object removeFromEnvironment(String propName) throws NamingException {
		throw new NamingException("not supported operation");
	}

	public void rename(Name oldName, Name newName) throws NamingException {
		Object value = lookup(oldName);
		unbind(oldName);
		bind(newName, value);
	}

	public void rename(String oldName, String newName) throws NamingException {
		rename(new CompositeName(oldName), new CompositeName(newName));
	}

	public void unbind(Name name) throws NamingException {
		String key = name.get(0);
		if (name.size() == 1) {
			if (!elements.containsKey(key)) {
				throw new NameNotFoundException(key);
			}
			if (LOG.isTraceEnabled()) {
				LOG.trace("Unbinding: " + key);
			}
			elements.remove(key);
			return;
		}

		name = name.getSuffix(1);
		Context ctx = (Context) lookupInstance(key, name);
		if (ctx != null) {
			ctx.unbind(name);
		}
	}

	public void unbind(String name) throws NamingException {
		unbind(new CompositeName(name));
	}

}
