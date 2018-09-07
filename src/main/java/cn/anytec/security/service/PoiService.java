package cn.anytec.security.service;

import cn.anytec.security.model.offlineMap.Poi;
import java.util.List;

public interface PoiService extends BaseService<Poi, Long> {

    List<Poi> getAll(Poi poi);

    List<Poi> getMapByName(String name);
}
