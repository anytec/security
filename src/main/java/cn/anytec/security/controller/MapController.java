package cn.anytec.security.controller;
import cn.anytec.security.model.offlineMap.Poi;
import cn.anytec.security.service.PoiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/map")
public class MapController {

    @Autowired
    private PoiService poiService;

    @RequestMapping("/getAllMap")
    public List<Poi> getAllMap(Poi poi) {
        List<Poi> pois = poiService.getAll(poi);
        return pois;
    }

    @RequestMapping("/getMapByName")
    public List<Poi> getMapByName(String name) {
        List<Poi> pois = poiService.getMapByName(name.trim());
        return pois;
    }
}
