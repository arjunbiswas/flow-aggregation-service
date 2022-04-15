package com.example.flowAggregationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryStorageRepository implements FlowStorageRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryStorageRepository.class);

  private static Map<Integer, NetFlowEntity> intKeyToCompositeKey = new ConcurrentHashMap<>();

  /**
   * The array has 24 slots for 24 hours . Every index of the array corresponds to an hourly
   * Concurrent HashMap . For hourly indexes based on days another level of lookup using day
   * as key would be useful . Day --> Hour --> DataMap . ConcurrentHashMaps do not lock the
   * entire map and locks segments of the map and by default the size of  ConcurrentHashMaps are 16
   **/
  private static ConcurrentHashMap<Integer, AtomicIntegerArray>[] hourlyCache = new ConcurrentHashMap[24];
  private static  JSONObject jsonSchema;

  /**
   * This method takes in a list of netflow logs and persists it in an in-memory store
   * @return
   */
  @Override
  public List<NetFlowEntity> save(List<NetFlowEntity> netFlowEntities) {
    LOGGER.debug("Starting save() process for {} netflow json rows", netFlowEntities.size());
    List<NetFlowEntity> errorSet = new ArrayList<>();
    for (NetFlowEntity netFlowEntity : netFlowEntities) {
      ConcurrentHashMap<Integer, AtomicIntegerArray> hourlyMapStore = hourlyCache[
          netFlowEntity.getHour()];
      if (validateInputJsonRow(netFlowEntity)) {
        if (hourlyMapStore != null) {
          foundExistingHourlyBucket(netFlowEntity, hourlyMapStore);
        } else {
          createNewHourlyBucket(netFlowEntity);
        }
      } else {
        errorSet.add(netFlowEntity);
      }
    }
    return errorSet;
  }

  /**
   * Method to validate input json row against schema
   * @param netFlowEntity
   * @return
   */
  private boolean validateInputJsonRow(NetFlowEntity netFlowEntity) {
    if (jsonSchema == null ) {
      jsonSchema = new JSONObject(
          new JSONTokener(InMemoryStorageRepository.class.getResourceAsStream("/schema.json")));
    }
    Schema schema = SchemaLoader.load(jsonSchema);
    try {
      JSONObject o = netFlowEntity.toJSON();
      schema.validate(o);
    } catch (ValidationException ex) {
      return false;
    }
    return true;
  }

  /**
   * Add/update new data in existing hourly cache
   * @param netFlowEntity
   * @param hourlyMapStore
   */
  private void foundExistingHourlyBucket(NetFlowEntity netFlowEntity,
      ConcurrentHashMap<Integer, AtomicIntegerArray> hourlyMapStore) {
    LOGGER.debug("Found existing bucket for data @hour{}", netFlowEntity.getHour());
    AtomicIntegerArray tXrXArray = hourlyMapStore.get(netFlowEntity.hashCode());
    if (tXrXArray != null) {
      LOGGER.debug("Found existing composite key for data @key{} @hour{}", netFlowEntity.hashCode(),
          netFlowEntity.getHour());

      /** atomically updating the values */
      atomicUpdatesOfBytesTyBytesRx(netFlowEntity, tXrXArray);
    } else {
      LOGGER.debug("No existing composite key for data @key{} @hour{}", netFlowEntity.hashCode(),
          netFlowEntity.getHour());

      tXrXArray = new AtomicIntegerArray(2);
      atomicUpdatesOfBytesTyBytesRx(netFlowEntity, tXrXArray);

      int flowKey = netFlowEntity.hashCode();
      intKeyToCompositeKey.putIfAbsent(flowKey, netFlowEntity);
      LOGGER.debug("Setting composite key for data @key{} @hour{}", netFlowEntity.hashCode(), netFlowEntity.getHour());
      hourlyMapStore.put(netFlowEntity.hashCode(), tXrXArray);

      hourlyCache[netFlowEntity.getHour()] = hourlyMapStore;
    }
  }

  /**
   * Creates a new hourly cache and add/update new data
   * @param netFlowEntity
   */
  private void createNewHourlyBucket(NetFlowEntity netFlowEntity) {
    LOGGER.debug("No existing bucket for data @hour{} .. creating new bucket", netFlowEntity.getHour());
    Map<Integer, AtomicIntegerArray> newHourlyMap = new ConcurrentHashMap<>(16);
    AtomicIntegerArray tXrXArray = new AtomicIntegerArray(2);

    atomicUpdatesOfBytesTyBytesRx(netFlowEntity, tXrXArray);
    int flowKey = netFlowEntity.hashCode();
    newHourlyMap.put(netFlowEntity.hashCode(), tXrXArray);

    intKeyToCompositeKey.putIfAbsent(flowKey, netFlowEntity);

    LOGGER.debug("Setting new bucket for data @hour{} .. after creating new bucket",
        netFlowEntity.getHour());
    hourlyCache[netFlowEntity.getHour()] = (ConcurrentHashMap<Integer, AtomicIntegerArray>) newHourlyMap;
  }

  /** atomically updating the values */
  private void atomicUpdatesOfBytesTyBytesRx(NetFlowEntity netFlowEntity,
      AtomicIntegerArray tXrXArray) {
    tXrXArray.getAndAdd(0, netFlowEntity.getBytes_tx());
    tXrXArray.getAndAdd(1, netFlowEntity.getBytes_rx());
  }


  /**
   * This method retrieves a list of netflow logs for a specific hour (ex: hour=1) The method
   * returns an empty array if no data is found
   */
  @Override
  public List<NetFlowEntity> get(String hour) {
    ConcurrentHashMap<Integer, AtomicIntegerArray> hourlyData = hourlyCache[
        Integer.valueOf(hour)];
    List<NetFlowEntity> resultSet;
    if (hourlyData != null) {
      resultSet = new ArrayList<>(hourlyData.keySet().size());
      for (Map.Entry<Integer, AtomicIntegerArray> entry : hourlyData.entrySet()) {
        NetFlowEntity f = intKeyToCompositeKey.get(entry.getKey());
        resultSet.add(new NetFlowEntity(f.getSrc_app(), f.getDest_app(), f.getVpc_id(),
            entry.getValue().get(0), entry.getValue().get(1), Integer.valueOf(hour)));
      }
      return resultSet;
    }
    return Arrays.asList();
  }
}
