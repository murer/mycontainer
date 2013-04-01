package com.googlecode.mycontainer.gae.test;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultIterator;

public class MessageServiceDaS implements MessageService {

	public List<Message> getAll() {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("Message");
		query.addSort("text");
		QueryResultIterable<Entity> msgs = ds.prepare(query)
				.asQueryResultIterable(withLimit(10));
		QueryResultIterator<Entity> it = msgs.iterator();
		List<Message> ret = new ArrayList<Message>();
		while (it.hasNext()) {
			Entity entity = it.next();
			Message msg = convert(entity);
			ret.add(msg);
		}
		return ret;
	}

	private Message convert(Entity entity) {
		Message ret = new Message();
		ret.setId(entity.getKey().getId());
		ret.setText((String) entity.getProperty("text"));
		return ret;
	}

	public void create(Message message) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity("Message");
		entity.setProperty("text", message.getText());
		ds.put(entity);
	}

	public void deleteById(Long id) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		ds.delete(KeyFactory.createKey("Message", id));
	}
}
