package com.googlecode.mycontainer.gae.test;

import java.util.List;

public interface MessageService {

	public abstract void deleteById(Long id);

	public abstract void create(Message message);

	public abstract List<Message> getAll();

}