package site.autzone.event.handler.datasource;

import javax.sql.DataSource;
import site.autzone.sqlbee.executor.DeleteExecute;
import site.autzone.sqlbee.executor.InsertExecute;
import site.autzone.sqlbee.executor.QueryExecute;
import site.autzone.sqlbee.executor.UpdateExecute;

public interface IRouteDb {
	DataSource route(String beanName);
	DataSource routeByKey(String key);
	QueryExecute query(String key);
	InsertExecute insert(String key);
	DeleteExecute delete(String key);
	UpdateExecute update(String key);
	QueryExecute query();
	InsertExecute insert();
	DeleteExecute delete();
	UpdateExecute update();
}
