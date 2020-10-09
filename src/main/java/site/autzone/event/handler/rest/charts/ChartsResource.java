package site.autzone.event.handler.rest.charts;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.autzone.event.handler.charts.AxisPointer;
import site.autzone.event.handler.charts.BaseOption;
import site.autzone.event.handler.charts.Legend;
import site.autzone.event.handler.charts.MPoint;
import site.autzone.event.handler.charts.MarkPoint;
import site.autzone.event.handler.charts.Option;
import site.autzone.event.handler.charts.Series;
import site.autzone.event.handler.charts.Title;
import site.autzone.event.handler.charts.Tooltip;
import site.autzone.event.handler.charts.XAxis;
import site.autzone.event.handler.charts.YAxis;
import site.autzone.event.handler.datasource.IRouteDb;

@RestController
@RequestMapping("/api")
public class ChartsResource {
	private final static String V1BASE = "v1";
	
	@Autowired
	IRouteDb db;
	
	@GetMapping(V1BASE+"/charts/item")
	public Option itemChart() throws SQLException {
		Option option = new Option();
		BaseOption baseOption = new BaseOption();
		Title title = new Title();
		title.setText("任务项图示");
		title.setSubtext("系统运行情况概览图");
		baseOption.setTitle(title);
		Legend legend = new Legend();
		legend.setData(getItemLegend());
		baseOption.setLegend(legend);
		Tooltip tooltip = new Tooltip();
		tooltip.setShow("true");
		tooltip.setFormatter("任务Key:{a}<br />批次号:{b}<br />数目:{c}");
		AxisPointer axisPointer = new AxisPointer();
		axisPointer.setType("shadow");
		tooltip.setTrigger("axis");
		tooltip.setAxisPointer(axisPointer);
		baseOption.setTooltip(tooltip);
		XAxis xAxis = new XAxis();
		xAxis.setData(getItemXAxis());
		baseOption.setxAxis(xAxis);
		YAxis yAxis = new YAxis();
		yAxis.setData(getItemYAxis());
		baseOption.setyAxis(yAxis);
		List<Series> series = getItemSeries(legend, xAxis);
		baseOption.setSeries(series);
		option.setBaseOption(baseOption);
		return option;
	}

	private List<Series> getItemSeries(Legend legend, XAxis xAxis) throws SQLException {
		List<Series> series = new ArrayList<Series>();
		for(Object leg : legend.getData()) {
			Series serie = new Series();
			MarkPoint markPoint = new MarkPoint();
			List<MPoint> markPoints = new ArrayList<>();
			MPoint mpMax = new MPoint();
			mpMax.setName("最多的数据");
			mpMax.setType("max");
			
			MPoint mpMin = new MPoint();
			mpMin.setName("最少的数据");
			mpMin.setType("min");
			markPoints.add(mpMax);
			markPoints.add(mpMin);
			markPoint.setData(markPoints);
			serie.setMarkPoint(markPoint);
			serie.setName(String.valueOf(leg));
			serie.setType("bar");
			List<Object> ret = new ArrayList<Object>();
			for(Object batchId : xAxis.getData()) {
				Map<String, Object> result = db.query().queryResult("SELECT count(1) COUNT "
						+ "FROM smart_item WHERE batch_id_ = ? and consumer_key_ = ?",
						batchId, leg);
				ret.add(result.get("COUNT"));
			}
			serie.setData(ret);
			series.add(serie);
		}
		Series totalSeries = new Series();
		totalSeries.setName("总数");
		totalSeries.setType("line");
		List<Object> retTotal = new ArrayList<Object>();
		for(Object batchId : xAxis.getData()) {
			int totalNum = 0;
			for(Object leg : legend.getData()) {
				Map<String, Object> result = db.query().queryResult("SELECT count(1) COUNT "
						+ "FROM smart_item WHERE batch_id_ = ? and consumer_key_ = ?",
						batchId, leg);
				totalNum += Integer.parseInt(String.valueOf(result.get("COUNT")));
			}
			retTotal.add(totalNum);
		}
		totalSeries.setData(retTotal);
		series.add(totalSeries);
		return series;
	}

	private List<Object> getItemYAxis() {
		return null;
	}

	private List<Object> getItemXAxis() throws SQLException {
		List<Map<String, Object>> results = db.query().queryResults
		("SELECT BATCH_ID_ FROM smart_item group by batch_id_");
		if(results == null || results.size() == 0) {
			return new ArrayList<>();
		}else {
			List<Object> legend = new ArrayList<Object>();
			for(Map<String, Object> map : results) {
				legend.add(map.get("BATCH_ID_"));
			}
			return legend;
		}
	}

	private List<Object> getItemLegend() throws SQLException {
		List<Map<String, Object>> results = db.query().queryResults
		("select consumer_key_ from smart_item group by consumer_key_");
		if(results == null || results.size() == 0) {
			return new ArrayList<>();
		}else {
			List<Object> legend = new ArrayList<Object>();
			for(Map<String, Object> map : results) {
				legend.add(map.get("CONSUMER_KEY_"));
			}
			return legend;
		}
	}
}
