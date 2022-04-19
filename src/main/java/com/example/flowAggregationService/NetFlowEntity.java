package com.example.flowAggregationService;

import java.util.Objects;
import org.json.JSONObject;

public class NetFlowEntity {

  private String src_app;

  private String dest_app;

  private String vpc_id;

  private int bytes_tx;

  private int bytes_rx;

  private int hour;

  public NetFlowEntity(String src_app, String dest_app, String vpc_id, int bytes_tx, int bytes_rx,
      int hour) {
    this.src_app = src_app;
    this.dest_app = dest_app;
    this.vpc_id = vpc_id;
    this.bytes_tx = bytes_tx;
    this.bytes_rx = bytes_rx;
    this.hour = hour;
  }

  public String getSrc_app() {
    return src_app;
  }

  public void setSrc_app(String src_app) {
    this.src_app = src_app;
  }

  public String getDest_app() {
    return dest_app;
  }

  public void setDest_app(String dest_app) {
    this.dest_app = dest_app;
  }

  public String getVpc_id() {
    return vpc_id;
  }

  public void setVpc_id(String vpc_id) {
    this.vpc_id = vpc_id;
  }

  public int getBytes_tx() {
    return bytes_tx;
  }

  public void setBytes_tx(int bytes_tx) {
    this.bytes_tx = bytes_tx;
  }

  public int getBytes_rx() {
    return bytes_rx;
  }

  public void setBytes_rx(int bytes_rx) {
    this.bytes_rx = bytes_rx;
  }

  public int getHour() {
    return hour;
  }

  public void setHour(int hour) {
    this.hour = hour;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof NetFlowEntity)) {
      return false;
    }
    NetFlowEntity netFlowEntity = (NetFlowEntity) o;
    return getSrc_app().equals(netFlowEntity.getSrc_app()) && getDest_app().equals(
        netFlowEntity.getDest_app())
        && getVpc_id().equals(netFlowEntity.getVpc_id());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSrc_app(), getDest_app(), getVpc_id());
  }

  public JSONObject toJSON() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("src_app", getSrc_app());
    jsonObject.put("dest_app", getDest_app());
    jsonObject.put("vpc_id", getVpc_id());
    jsonObject.put("bytes_tx", getBytes_tx());
    jsonObject.put("bytes_rx", getBytes_rx());
    jsonObject.put("hour", getHour());
    return jsonObject;
  }
}