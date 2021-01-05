package site.autzone.event.handler.cfg;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import site.autzone.event.handler.task.EventLoop;

@JsonIgnoreProperties(value = { "runningLoops" })
public class EventTaskProperties {
  private String consumerKey;
  private Boolean enable;
  private String p;
  private String name;
  private Integer fetchers;
  private Integer size;
  private Integer interval;
  private List<TaskArgProperties> args;
  private List<EventLoop> runningLoops = new ArrayList<EventLoop>();

  public String getConsumerKey() {
    return consumerKey;
  }

  public void setConsumerKey(String consumerKey) {
    this.consumerKey = consumerKey;
  }

  public Boolean getEnable() {
    return enable;
  }

  public void setEnable(Boolean enable) {
    this.enable = enable;
  }

  public String getP() {
    return p;
  }

  public void setP(String p) {
    this.p = p;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getFetchers() {
    return fetchers;
  }

  public void setFetchers(Integer fetchers) {
    this.fetchers = fetchers;
  }

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public Integer getInterval() {
    return interval;
  }

  public void setInterval(Integer interval) {
    this.interval = interval;
  }

  public List<TaskArgProperties> getArgs() {
    return args;
  }

  public void setArgs(List<TaskArgProperties> args) {
    this.args = args;
  }

  public List<EventLoop> getRunningLoops() {
    return runningLoops;
  }

  public void setRunningLoops(List<EventLoop> runningLoops) {
    this.runningLoops = runningLoops;
  }
}
