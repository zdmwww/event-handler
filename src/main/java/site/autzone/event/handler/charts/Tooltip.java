package site.autzone.event.handler.charts;

public class Tooltip {
    private String show;
    private String formatter;
    private String trigger;
    private AxisPointer axisPointer;
	public String getShow() {
		return show;
	}
	public void setShow(String show) {
		this.show = show;
	}
	public String getFormatter() {
		return formatter;
	}
	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}
	public String getTrigger() {
		return trigger;
	}
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
	public AxisPointer getAxisPointer() {
		return axisPointer;
	}
	public void setAxisPointer(AxisPointer axisPointer) {
		this.axisPointer = axisPointer;
	}
}
