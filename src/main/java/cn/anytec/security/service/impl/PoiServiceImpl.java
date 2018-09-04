package cn.anytec.security.service.impl;

import cn.anytec.security.dao.PoiMapper;
import cn.anytec.security.model.offlineMap.Poi;
import cn.anytec.security.service.PoiService;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Created by root on 2018/3/13 0013.
 */
@Service
public class PoiServiceImpl extends BaseServiceImpl<Poi, Long> implements PoiService {

    @Autowired
    private PoiMapper poiMapper;

    @Override
    public List<Poi> getAll(Poi poi) {
        if (poi.getPage() != null && poi.getRows() != null) {
            PageHelper.startPage(poi.getPage(), poi.getRows());
        }
        return poiMapper.selectAll();
    }

    @Override
    public List<Poi> getMapByName(String name) {
        if (name != null && name != "") {
            List<Poi> list = poiMapper.getMapByName(name);
            return list;
        } else {
            return null;
        }
    }
}
