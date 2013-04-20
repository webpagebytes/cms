package com.webbricks.datautility;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

public class WBDefaultGaeDataFactory implements WBGaeDataFactory {
	public Entity createEntity(Key key)
	{
		return new Entity(key);
	}
	public Entity createEntity(String kind, Long id)
	{
		return new Entity(kind, id);
	}
	public Entity createEntity(String kind)
	{
		return new Entity(kind);
	}
	public Entity createEntity(String kind, String name)
	{
		return new Entity(kind, name); 
	}
	public Key createKey(String kind, Long id)
	{
		return KeyFactory.createKey(kind, id);
	}
	public Key createKey(String kind, String name)
	{
		return KeyFactory.createKey(kind, name);
	}
	public DatastoreService createDatastoreService()
	{
		return DatastoreServiceFactory.getDatastoreService();
	}
	public Query createQuery(String kind)
	{
		return new Query(kind);
	}
}
