package cn.anytec.security.findface.model;


import java.util.List;
import java.util.Map;

public class IdentifyPojo {

    private Map<String,List<MatchFace>> results;

    public Map<String, List<MatchFace>> getResults() {
        return results;
    }


}
