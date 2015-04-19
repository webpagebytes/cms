package com.webpagebytes.cms.engine;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.WPBStopWatch;

public class WPBAdminDataStorageDecorator implements WPBAdminDataStorage {
	private static final Logger log = Logger.getLogger(WPBAdminDataStorageDecorator.class.getName());
	protected WPBAdminDataStorage instance;
	
	public WPBAdminDataStorageDecorator(WPBAdminDataStorage instance)
	{
		this.instance = instance;		
	}
	@Override
	public void initialize(Map<String, String> params) throws WPBIOException { 
		WPBStopWatch stopWatch = WPBStopWatch.newInstance();
		instance.initialize(params);
		log.log(Level.INFO, String.format("WPBAdminDataStorage:initialize took %d ms", stopWatch.stop()));
	}
	
	@Override
	public <T> void delete(String recordid, Class<T> dataClass)
			throws WPBIOException {
		WPBStopWatch stopWatch = WPBStopWatch.newInstance();
		instance.delete(recordid, dataClass);
		log.log(Level.INFO, String.format("WPBAdminDataStorage:delete(recordid, %s) took %d ms", dataClass.getSimpleName(), stopWatch.stop()));
	}
	
	@Override
	public <T> void delete(Class<T> dataClass, String property,
			AdminQueryOperator operator, Object parameter)
			throws WPBIOException {
		WPBStopWatch stopWatch = WPBStopWatch.newInstance();
		instance.delete(dataClass, property, operator, parameter);
		log.log(Level.INFO, String.format("WPBAdminDataStorage:delete(%s,%s,operator,parameter) took %d ms", dataClass.getSimpleName(), property, stopWatch.stop()));
	}
	
	@Override
	public <T> List<T> getAllRecords(Class<T> dataClass) throws WPBIOException {
		WPBStopWatch stopWatch = WPBStopWatch.newInstance();
		List<T> result = instance.getAllRecords(dataClass);
		log.log(Level.INFO, String.format("WPBAdminDataStorage:getAllRecords(%s) took %d ms", dataClass.getSimpleName(), stopWatch.stop()));
		return result;
	}
	
	@Override
	public <T> List<T> getAllRecords(Class<T> dataClass, String property,
			AdminSortOperator operator) throws WPBIOException {
		WPBStopWatch stopWatch = WPBStopWatch.newInstance();
		List<T> result = instance.getAllRecords(dataClass, property, operator);
		log.log(Level.INFO, String.format("WPBAdminDataStorage:getAllRecords(%s,%s,operator) took %d ms", dataClass.getSimpleName(), property, stopWatch.stop()));
		return result;
	}
	
	@Override
	public <T> T add(T record) throws WPBIOException {
		WPBStopWatch stopWatch = WPBStopWatch.newInstance();
		T result = instance.add(record);
		log.log(Level.INFO, String.format("WPBAdminDataStorage:add (%s) took %d ms", record.getClass().getSimpleName(), stopWatch.stop()));
		return result;
	}
	
	@Override
	public <T> T addWithKey(T record) throws WPBIOException {
		WPBStopWatch stopWatch = WPBStopWatch.newInstance();
		T result = instance.addWithKey(record);
		log.log(Level.INFO, String.format("WPBAdminDataStorage:addWithKey (%s) took %d ms", record.getClass().getSimpleName(), stopWatch.stop()));
		return result;
	}
	
	@Override
	public <T> T get(String recordid, Class<T> dataClass) throws WPBIOException {
		WPBStopWatch stopWatch = WPBStopWatch.newInstance();
		T result = instance.get(recordid, dataClass);
		log.log(Level.INFO, String.format("WPBAdminDataStorage:get(%s, %s) took %d ms", recordid, dataClass.getSimpleName(), stopWatch.stop()));
		return result;
	}
	
	@Override
	public <T> T update(T record) throws WPBIOException {
		WPBStopWatch stopWatch = WPBStopWatch.newInstance();
		T result = instance.update(record);
		log.log(Level.INFO, String.format("WPBAdminDataStorage:update (%s) took %d ms", record.getClass().getSimpleName(), stopWatch.stop()));
		return result;
	}
	
	@Override
	public <T> List<T> query(Class<T> dataClass, String property,
			AdminQueryOperator operator, Object parameter)
			throws WPBIOException {
		WPBStopWatch stopWatch = WPBStopWatch.newInstance();
		List<T> result = instance.query(dataClass, property, operator, parameter);
		log.log(Level.INFO, String.format("WPBAdminDataStorage:query(%s, %s) took %d ms", dataClass.getSimpleName(), property, stopWatch.stop()));
		return result;
	}
	
	@Override
	public <T> List<T> queryEx(Class<T> dataClass, Set<String> propertyNames,
			Map<String, AdminQueryOperator> operators,
			Map<String, Object> values) throws WPBIOException {
		WPBStopWatch stopWatch = WPBStopWatch.newInstance();
		List<T> result = instance.queryEx(dataClass, propertyNames, operators, values);
		log.log(Level.INFO, String.format("WPBAdminDataStorage:queryEx(%s, propertyNames) took %d ms", dataClass.getSimpleName(), stopWatch.stop()));
		return result;
	}
	
	@Override
	public <T> List<T> queryWithSort(Class<T> dataClass, String property,
			AdminQueryOperator operator, Object parameter, String sortProperty,
			AdminSortOperator sortOperator) throws WPBIOException {
		WPBStopWatch stopWatch = WPBStopWatch.newInstance();
		List<T> result = instance.queryWithSort(dataClass, property, operator, parameter, sortProperty, sortOperator);
		log.log(Level.INFO, String.format("WPBAdminDataStorage:queryWithSort(%s,%s,..,%s) took %d ms", dataClass.getSimpleName(), property, sortProperty, stopWatch.stop()));
		return result;
	}
	
	@Override
	public void addStorageListener(WPBAdminDataStorageListener listener) {
		instance.addStorageListener(listener);
	}
	@Override
	public void removeStorageListener(WPBAdminDataStorageListener listener) {
		instance.removeStorageListener(listener);
	}
	@Override
	public void stopNotifications() {
		instance.stopNotifications();
	}
	@Override
	public void startNotifications() {
		instance.startNotifications();
	}
	@Override
	public boolean isNotificationActive() {
		// TODO Auto-generated method stub
		return instance.isNotificationActive();
	}
	@Override
	public <T> void deleteAllRecords(Class<T> dataClass) throws WPBIOException {
		WPBStopWatch stopWatch = WPBStopWatch.newInstance();
		instance.deleteAllRecords(dataClass);
		log.log(Level.INFO, String.format("WPBAdminDataStorage:deleteAllRecords(%s) took %d ms", dataClass.getSimpleName(), stopWatch.stop()));

	}
	@Override
	public String getUniqueId() {
		// TODO Auto-generated method stub
		return instance.getUniqueId();
	}

}
