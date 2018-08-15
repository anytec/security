package cn.anytec.security.service;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.TbPerson;
import cn.anytec.security.model.vo.PersonVo;
import com.github.pagehelper.PageInfo;

public interface PersonService {
    ServerResponse add(PersonVo personVo);
    ServerResponse<String> delete(String personSdkIds);
    ServerResponse update(PersonVo personVo);
    ServerResponse<PageInfo> list(int pageNum, int pageSize,TbPerson person);
    ServerResponse<TbPerson> getPersonBySdkId(String sdkId);

}
