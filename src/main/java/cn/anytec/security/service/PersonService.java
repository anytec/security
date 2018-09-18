package cn.anytec.security.service;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.TbPerson;
import cn.anytec.security.model.dto.PersonDTO;
import cn.anytec.security.model.vo.PersonVO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PersonService {
    TbPerson getPersonInfo(Integer personId);
    boolean checkIdNumber(String idNumber);
    ServerResponse add(PersonDTO personDTO);
    ServerResponse<String> delete(String personSdkIds);
    ServerResponse update(PersonDTO personDTO);
    ServerResponse<PageInfo> list(int pageNum, int pageSize,TbPerson person);
    PersonVO personConvertPersonVO(TbPerson person);
    ServerResponse<TbPerson> getPersonBySdkId(String sdkId);
    List<String> uploadPhotos(MultipartFile[] files);
    ServerResponse addPhotos(List<String> photoPathList, String personGroupId, String personGroupName);
}
