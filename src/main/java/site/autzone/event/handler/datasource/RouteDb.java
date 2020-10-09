package site.autzone.event.handler.datasource;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import site.autzone.sqlbee.executor.DeleteExecute;
import site.autzone.sqlbee.executor.InsertExecute;
import site.autzone.sqlbee.executor.QueryExecute;
import site.autzone.sqlbee.executor.UpdateExecute;

@Service
public class RouteDb implements IRouteDb {
	@Autowired
	QueryExecute queryExecute;
	@Autowired
	InsertExecute insertExecute;
	@Autowired
	DeleteExecute deleteExecute;
	@Autowired
	UpdateExecute updateExecute;
	
	@Autowired
	ApplicationContext applicationContext;

	@Override
	public DataSource route(String beanName) {
		return applicationContext.getBean(beanName, DataSource.class);
	}

	@Override
	public DataSource routeByKey(String key) {
		if(key == null) {
			return route("datasource");
		}
		return route("datasource"+key);
	}
	
	public QueryExecute query() {
		queryExecute.setDataSource(routeByKey(null));
		return queryExecute;
	}
	
	public InsertExecute insert() {
		insertExecute.setDataSource(routeByKey(null));
		return insertExecute;
	}
	
	public DeleteExecute delete() {
		deleteExecute.setDataSource(routeByKey(null));
		return deleteExecute;
	}
	
	public UpdateExecute update() {
		updateExecute.setDataSource(routeByKey(null));
		return updateExecute;
	}
	
	
	public QueryExecute query(String key) {
		queryExecute.setDataSource(routeByKey(key));
		return queryExecute;
	}
	
	public InsertExecute insert(String key) {
		insertExecute.setDataSource(routeByKey(key));
		return insertExecute;
	}
	
	public DeleteExecute delete(String key) {
		deleteExecute.setDataSource(routeByKey(key));
		return deleteExecute;
	}
	
	public UpdateExecute update(String key) {
		updateExecute.setDataSource(routeByKey(key));
		return updateExecute;
	}
}
