package cn.anytec.security.dao;

import cn.anytec.security.model.offlineMap.Poi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PoiMapper extends MyMapper<Poi> {
    List<Poi> getMapByName(@Param("name") String name);
}