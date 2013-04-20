package com.webbricks.datautility;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.DatastoreService;;

public interface WBGaeDataFactory {
	public abstract Entity createEntity(Key key);
	public abstract Entity createEntity(String kind, Long id);
	public abstract Entity createEntity(String kind);
	public abstract Entity createEntity(String kind, String name);
	public abstract Key createKey(String kind, Long id);
	public abstract Key createKey(String kind, String name);
	public abstract DatastoreService createDatastoreService();
	public abstract Query createQuery(String kind);
}
