package cn.anytec.security.service;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.TbPerson;
import cn.anytec.security.model.vo.PersonVO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PersonService {
    TbPerson getPersonInfo(Integer personId);
    ServerResponse add(PersonVO personVO);
    ServerResponse<String> delete(String personSdkIds);
    ServerResponse update(PersonVO personVO);
    ServerResponse<PageInfo> list(int pageNum, int pageSize,TbPerson person);
    ServerResponse<TbPerson> getPersonBySdkId(String sdkId);
    List<String> uploadPhotos(MultipartFile[] files);
    ServerResponse addPhotos(List<String> photoPathList, String personGroupId, String personGroupName);
}
