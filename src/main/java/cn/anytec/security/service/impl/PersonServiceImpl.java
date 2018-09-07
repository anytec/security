package cn.anytec.security.service.impl;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.core.log.LogObjectHolder;
import cn.anytec.security.dao.TbPersonMapper;
import cn.anytec.security.findface.FindFaceService;
import cn.anytec.security.findface.model.FacePojo;
import cn.anytec.security.findface.model.IdentifyFace;
import cn.anytec.security.model.TbPerson;
import cn.anytec.security.model.TbPersonExample;
import cn.anytec.security.model.parammodel.FindFaceParam;
import cn.anytec.security.service.PersonService;
import cn.anytec.security.model.vo.PersonVO;
import cn.anytec.security.util.MD5Util;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Value("${file.personPhotos.path}")
    private String personPhotosPath;
    @Value("${server.ip}")
    private String ip;
    @Value("${server.port}")
    private String port;

    public TbPerson getPersonInfo(Integer personId){
        TbPerson person = personMapper.selectByPrimaryKey(personId);
        LogObjectHolder.me().set(person);
        return person;
    }

    public ServerResponse<TbPerson> add(PersonVO personVO) {
        MultipartFile photo = personVO.getPhoto();
        String photoUrl = personVO.getPhotoUrl();
        try {
            FindFaceParam param = getStaticFindFaceParam();
            param.setPhotoUrl(photoUrl);
            FacePojo facePojo = addStaticSdkFace(photo,param);
            if(facePojo != null){
                TbPerson person = parsePersonVo(personVO, facePojo);
                if (addMySqlFace(person)) {
                    return ServerResponse.createBySuccess("添加person成功", person);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServerResponse.createByErrorMessage("添加person失败");
    }

    private FacePojo addStaticSdkFace(MultipartFile photo, FindFaceParam param) throws IOException {
        if(photo != null){
            return findFaceService.addFace(photo.getBytes(), param);
        }else {
            return findFaceService.addFace(null, param);
        }
    }

    private FindFaceParam getStaticFindFaceParam(){
        FindFaceParam param = new FindFaceParam();
        param.setGalleries(new String[]{config.getStaticGallery()});
        param.setSdkIp(config.getStaticSdkIp());
        param.setSdkPort(config.getStaticSdkPort());
        param.setSdkVersion(config.getStaticSdkVersion());
        param.setSdkToken(config.getStaticSdkToken());
        return param;
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

    public ServerResponse<TbPerson> update(PersonVO personVO) {
        MultipartFile photo = personVO.getPhoto();
        FacePojo facePojo = null;
        if (photo != null) {
            String sdkId = personVO.getSdkId();
            if (deleteSdkFace(sdkId)) {
                try{
                    facePojo = addStaticSdkFace(photo,null);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        TbPerson person = parsePersonVo(personVO, facePojo);
        if(updateMysqlFace(person)){
            TbPerson tbPerson = personMapper.selectByPrimaryKey(person.getId());
            removeRedisPerson(tbPerson);
            return ServerResponse.createBySuccessMessage("更新person信息成功");
        }
        return ServerResponse.createByErrorMessage("更新person信息失败");
    }

    private void removeRedisPerson(TbPerson tbPerson){
        String redisKey = config.getPeronBySdkId();
        String personSdkId = tbPerson.getSdkId();
        if (redisTemplate.opsForHash().hasKey(redisKey, personSdkId)) {
            redisTemplate.opsForHash().delete(redisKey,personSdkId);
        }
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
            redisTemplate.expire(redisKey, 1, TimeUnit.DAYS);
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
        if (!StringUtils.isEmpty(person.getSdkId())) {
            c.andSdkIdEqualTo(person.getSdkId());
        }
        example.setOrderByClause("enroll_time desc");
        List<TbPerson> personList = personMapper.selectByExample(example);
        System.out.println(personList.size());
        PageInfo pageResult = new PageInfo(personList);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 把前端选择的图片文件夹里的图片上传到服务器
     * @param files
     * @return
     */
    public List<String> uploadPhotos(MultipartFile[] files){
        List<String> pathList = new ArrayList<>();
        if (files == null || files.length == 0) {
            logger.info("批量上传的照片数量为0！");
            return pathList;
        }
        if (personPhotosPath.endsWith("/")) {
            personPhotosPath = personPhotosPath.substring(0, personPhotosPath.length() - 1);
        }
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if(fileName.indexOf("/")>0){
                fileName = fileName.substring(fileName.indexOf("/")+1);
            }
            String personName = fileName.split("\\.")[0];
            String newFileName = newFileName(fileName);
            String filePath = personPhotosPath + "/" + newFileName;
            File photo = new File(filePath);
            try {
                file.transferTo(photo);
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
                return null;
            }
            String photoPath = "http://"+ip+":"+port+"/static/" + newFileName + "#pn#" + personName;
            pathList.add(photoPath);
        }
        return pathList;
    }

    /**
     * MD5加密一个文件名
     * @param fileName
     * @return
     */
    public static String newFileName(String fileName){
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        return MD5Util.MD5EncodeUtf8(fileName + System.currentTimeMillis()) + prefix;
    }

    /**
     * 照片入sdk和mysql
     * @param photoPathList 照片路径和照片名称用“#pn#”间隔,组成的List<String>
     * @param personGroupId 人员组id
     * @return
     */
    public ServerResponse addPhotos(List<String> photoPathList, String personGroupId, String personGroupName){
        String msg = "";
        if(!CollectionUtils.isEmpty(photoPathList)){
            for(int i=0; i<photoPathList.size(); i++){
                String path = photoPathList.get(i);
                String photoPath = path.split("#pn#")[0];
                String personName = path.split("#pn#")[1];
                FindFaceParam findFaceParam = getStaticFindFaceParam();
                findFaceParam.setPhotoUrl(photoPath);
                findFaceParam.setMeta(personName);
                FacePojo facePojo = findFaceService.addFace(null,findFaceParam);
                if(facePojo != null){
                    PersonVO personVO = new PersonVO();
                    personVO.setGroupId(Integer.parseInt(personGroupId));
                    personVO.setGroupName(personGroupName);
                    personVO.setName(personName);
                    personVO.setRemarks("批量录入");
                    TbPerson person = parsePersonVo(personVO, facePojo);
                    if (!addMySqlFace(person)) {
                        return ServerResponse.createBySuccess("批量上传照片发生错误", person);
                    }
                }else {
                    if(i == photoPathList.size()-1){
                        msg += personName;
                    }else {
                        msg += personName+",";
                    }
                }
            }
        }
        if(!StringUtils.isEmpty(msg)){
            return ServerResponse.createBySuccessMessage("上传不成功的照片： "+msg);
        }
        return ServerResponse.createBySuccessMessage("批量上传照片成功,照片数量："+photoPathList.size());
    }

    /**
     * 把personVO和facePojo里的字段填充到TbPerson
     * @param personVO
     * @param facePojo
     * @return
     */
    public TbPerson parsePersonVo(PersonVO personVO, FacePojo facePojo) {
        TbPerson person = new TbPerson();
        if(personVO.getId() != null){
            person.setId(personVO.getId());
        }
        person.setName(personVO.getName());
        person.setGender(personVO.getGender());
        person.setIdNumber(personVO.getIdNumber());
        person.setGroupName(personVO.getGroupName());
        person.setGroupId(personVO.getGroupId());
        person.setRemarks(personVO.getRemarks());
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
