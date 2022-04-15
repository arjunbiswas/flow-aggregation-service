package com.example.flowAggregationService;

import java.util.List;

public interface FlowStorageRepository {

  List<NetFlowEntity> save(List<NetFlowEntity> netFlowEntities);

  List<NetFlowEntity> get(String hour);
}
