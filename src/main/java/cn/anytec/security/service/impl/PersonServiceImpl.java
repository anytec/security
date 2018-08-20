package cn.anytec.security.service.impl;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.dao.TbPersonMapper;
import cn.anytec.security.findface.FindFaceService;
import cn.anytec.security.findface.model.FacePojo;
import cn.anytec.security.findface.model.IdentifyFace;
import cn.anytec.security.model.TbPerson;
import cn.anytec.security.model.TbPersonExample;
import cn.anytec.security.model.parammodel.FindFaceParam;
import cn.anytec.security.service.PersonService;
import cn.anytec.security.model.vo.PersonVo;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service("PersonService")
public class PersonServiceImpl implements PersonService {
    private final Logger logger = LoggerFactory.getLogger(PersonServiceImpl.class);
    @Autowired
    private TbPersonMapper personMapper;
    @Autowired
    private FindFaceService findFaceService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private GeneralConfig config;

    public ServerResponse<TbPerson> add(PersonVo personVo) {
        MultipartFile photo = personVo.getPhoto();
        String photoUrl = personVo.getPhotoUrl();
        try {
            FacePojo facePojo = addSdkFace(photo,photoUrl);
            if(facePojo != null){
                TbPerson person = parsePersonVo(personVo, facePojo);
                if (addMySqlFace(person)) {
                    return ServerResponse.createBySuccess("新增person成功", person);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServerResponse.createByErrorMessage("新增person失败");
    }

    public FacePojo addSdkFace(MultipartFile photo,String photoUrl) throws IOException {
        FindFaceParam param = new FindFaceParam();
        param.setGalleries(new String[]{config.getStaticGallery()});
        param.setPhotoUrl(photoUrl);
        if(photo != null){
            return findFaceService.addFace(photo.getBytes(), param);
        }else {
            return findFaceService.addFace(null, param);
        }
    }

    public boolean addMySqlFace(TbPerson person) {
        int updateCount = personMapper.insert(person);
        if (updateCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    public ServerResponse delete(String personSdkIds) {
        boolean result = true;
        List<String> sdkIdList = Splitter.on(",").splitToList(personSdkIds);
        for (String sdkId : sdkIdList) {
            if (!StringUtils.isEmpty(sdkId)) {
                if (deleteSdkFace(sdkId) && deleteMysqlFace(sdkId)) {
                }else {
                    result =false;
                }
            }
        }
        if(result){
            return ServerResponse.createBySuccess();
        }else {
            return ServerResponse.createByError();
        }
    }

    public boolean deleteMysqlFace(String sdkId) {
        TbPersonExample example = new TbPersonExample();
        TbPersonExample.Criteria c = example.createCriteria();
        c.andSdkIdEqualTo(sdkId);
        int result = personMapper.deleteByExample(example);
        if (result > 0) {
            logger.info("删除mysql中的face成功");
            return true;
        }
        return false;
    }

    public boolean deleteSdkFace(String sdkId) {
        return findFaceService.deleteFace(sdkId);
    }

    public ServerResponse<TbPerson> update(PersonVo personVo) {
        MultipartFile photo = personVo.getPhoto();
        FacePojo facePojo = null;
        if (photo != null) {
            String sdkId = personVo.getSdkId();
            if (deleteSdkFace(sdkId)) {
                try{
                    facePojo = addSdkFace(photo,null);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        TbPerson person = parsePersonVo(personVo, facePojo);
        if(updateMysqlFace(person)){
            return ServerResponse.createBySuccessMessage("更新person信息成功");
        }
        return ServerResponse.createByErrorMessage("更新person信息失败");
    }

    public boolean updateMysqlFace(TbPerson person) {
        int updateCount = personMapper.updateByPrimaryKeySelective(person);
        if (updateCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    public ServerResponse<TbPerson> getPersonBySdkId(String sdkId) {
        String redisKey = config.getPeronBySdkId();
        if (redisTemplate.opsForHash().hasKey(redisKey, sdkId)) {
            String personStr = redisTemplate.opsForHash().get(redisKey, sdkId).toString();
            TbPerson person = JSONObject.parseObject(personStr,TbPerson.class);
            return ServerResponse.createBySuccess("getPersonBySdkId返回成功", person);
        }
        TbPersonExample example = new TbPersonExample();
        TbPersonExample.Criteria c = example.createCriteria();
        c.andSdkIdEqualTo(sdkId);
        List<TbPerson> personList = personMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(personList)) {
            TbPerson person = personList.get(0);
            redisTemplate.opsForHash().put(redisKey,sdkId,JSONObject.toJSONString(person));
            return ServerResponse.createBySuccess("getPersonBySdkId返回成功", person);
        }
        return ServerResponse.createByErrorMessage("getPersonBySdkId未查到符合条件的信息");
    }

    public ServerResponse<PageInfo> list(int pageNum, int pageSize, TbPerson person) {
        PageHelper.startPage(pageNum, pageSize);
        TbPersonExample example = new TbPersonExample();
        TbPersonExample.Criteria c = example.createCriteria();
        if (!StringUtils.isEmpty(person.getGender())) {
            c.andGenderEqualTo(person.getGender().trim());
        }
        if (person.getGroupId() != null) {
            c.andGroupIdEqualTo(person.getGroupId());
        }
        if (!StringUtils.isEmpty(person.getIdNumber())) {
            c.andIdNumberLike("%" + person.getIdNumber().trim() + "%");
        }
        if (!StringUtils.isEmpty(person.getName())) {
            c.andNameLike("%" + person.getName().trim() + "%");
        }
        example.setOrderByClause("enroll_time desc");
        List<TbPerson> personList = personMapper.selectByExample(example);
        System.out.println(personList.size());
        PageInfo pageResult = new PageInfo(personList);
        return ServerResponse.createBySuccess(pageResult);
    }


    public TbPerson parsePersonVo(PersonVo personVo, FacePojo facePojo) {
        TbPerson person = new TbPerson();
        if(personVo.getId() != null){
            person.setId(personVo.getId());
        }
        person.setName(personVo.getName());
        person.setGender(personVo.getGender());
        person.setIdNumber(personVo.getIdNumber());
        person.setGroupName(personVo.getGroupName());
        person.setGroupId(personVo.getGroupId());
        person.setRemarks(personVo.getRemarks());
        Timestamp timestamp = new Timestamp(new Date().getTime());
        person.setEnrollTime(timestamp);
        if (facePojo != null) {
            IdentifyFace face = facePojo.getResults().get(0);
            person.setNormalized(face.getNormalized());
            person.setThumbnail(face.getThumbnail());
            person.setPhoto(face.getPhoto());
            person.setSdkId(face.getId());
        }
        return person;
    }
}
