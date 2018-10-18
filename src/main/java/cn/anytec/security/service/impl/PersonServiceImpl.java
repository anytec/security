package cn.anytec.security.service.impl;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.constant.RedisConst;
import cn.anytec.security.core.exception.BussinessException;
import cn.anytec.security.core.log.LogObjectHolder;
import cn.anytec.security.dao.TbPersonMapper;
import cn.anytec.security.findface.FindFaceService;
import cn.anytec.security.findface.model.FacePojo;
import cn.anytec.security.findface.model.IdentifyFace;
import cn.anytec.security.model.TbGroupPerson;
import cn.anytec.security.model.TbPerson;
import cn.anytec.security.model.TbPersonExample;
import cn.anytec.security.model.form.PersonForm;
import cn.anytec.security.model.parammodel.FindFaceParam;
import cn.anytec.security.model.dto.PersonDTO;
import cn.anytec.security.service.GroupPersonService;
import cn.anytec.security.service.PersonService;
import cn.anytec.security.util.KeyUtil;
import cn.anytec.security.util.MD5Util;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Autowired
    private GroupPersonService groupPersonService;

    @Value("${file.personPhotos.path}")
    private String personPhotosPath;
    @Value("${server.port}")
    private String port;

    public TbPerson getPersonInfo(Integer personId){
        TbPerson person = personMapper.selectByPrimaryKey(personId);
        LogObjectHolder.me().set(person);
        return person;
    }

    @Override
    public boolean checkIdNumber(String idNumber) {
        TbPersonExample example = new TbPersonExample();
        TbPersonExample.Criteria c = example.createCriteria();
        c.andIdNumberEqualTo(idNumber);
        List<TbPerson> personList = personMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(personList)){
            return true;
        }
        return false;
    }

    public ServerResponse<TbPerson> add(PersonForm personForm) {
        MultipartFile photo = personForm.getPhoto();
        String photoUrl = personForm.getPhotoUrl();
        FindFaceParam param = getStaticFindFaceParam();
        param.setPhotoUrl(photoUrl);
        param.setMeta(personForm.getName());
        FacePojo facePojo = null;
        try {
            facePojo = addStaticSdkFace(photo,param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(facePojo != null){
            TbPerson person = parsePersonDTO(personForm, facePojo);
            if (addMySqlFace(person)) {
                return ServerResponse.createBySuccess("添加person成功", person);
            }
        }else {
            return ServerResponse.createByErrorMessage("图片未检测到人脸");
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
                    deleteRedisFace(sdkId);
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
        logger.info("删除mysql中的face失败");
        return false;
    }

    public boolean deleteSdkFace(String sdkId) {
        return findFaceService.deleteFace(sdkId);
    }
    public void deleteRedisFace(String sdkId) {
        if(redisTemplate.opsForHash().hasKey(RedisConst.PERSON_BY_SDKID,sdkId)){
            redisTemplate.opsForHash().delete(RedisConst.PERSON_BY_SDKID,sdkId);
            logger.info("【deleteRedisPerson】{}",sdkId);
        }
    }

    public ServerResponse<TbPerson> update(PersonForm personForm) {
        MultipartFile photo = personForm.getPhoto();
        FacePojo facePojo = null;
        if (photo != null) {
            String sdkId = personForm.getSdkId();
            if (deleteSdkFace(sdkId)) {
                try{
                    FindFaceParam param = getStaticFindFaceParam();
                    param.setMeta(personForm.getName());
                    facePojo = addStaticSdkFace(photo,param);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        TbPerson person = parsePersonDTO(personForm, facePojo);
        if(updateMysqlFace(person)){
            TbPerson tbPerson = personMapper.selectByPrimaryKey(person.getId());
            deleteRedisFace(tbPerson.getSdkId());
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
        String redisKey = RedisConst.PERSON_BY_SDKID;
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
        PageInfo pageResult = new PageInfo(personList);
        List<PersonDTO> personDTOList = personList.stream()
                .map(e->personConvertPersonDTO(e))
                .collect(Collectors.toList());
        pageResult.setList(personDTOList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public PersonDTO personConvertPersonDTO(TbPerson person) {
        PersonDTO personDTO = new PersonDTO();
        BeanUtils.copyProperties(person, personDTO);
        TbGroupPerson personGroup = groupPersonService.getGroupPersonById(person.getGroupId().toString()).getData();
        personDTO.setGroupName(personGroup.getName());
        return personDTO;
    }

    @Override
    public List<PersonDTO> personListConvertPersonDTOList(List<TbPerson> personList) {
        List<TbGroupPerson> personGroupList = groupPersonService.allList();
        List<PersonDTO> personDTOList = new ArrayList<>();
        if(personGroupList.size()>0 && personList.size()>0){
            for(TbPerson person : personList){
                for(TbGroupPerson personGroup : personGroupList){
                    if(person.getGroupId().equals(personGroup.getId())){
                        PersonDTO personDTO = new PersonDTO();
                        BeanUtils.copyProperties(person, personDTO);
                        personDTO.setGroupName(personGroup.getName());
                        personDTOList.add(personDTO);
                    }
                }
            }
        }
        return personDTOList;
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
            if(fileName.contains(".")){
                String personName = fileName.substring(0,fileName.lastIndexOf("."));
                String newFileName = newFileName(fileName);
                String filePath = personPhotosPath + "/" + newFileName;
                File photo = new File(filePath);
                try {
                    file.transferTo(photo);
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                    return null;
                }
                String ip = null;
                try {
                    ip = getIpAddress();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                if(StringUtils.isEmpty(ip)){
                    throw new BussinessException(1,"获取服务器ip地址失败");
                }
                String photoPath = "http://"+ip+":"+port+"/static/" + newFileName + "#pn#" + personName;
                pathList.add(photoPath);
            }
        }
        return pathList;
    }

    /**
     * 根据网卡获得IP地址
     * @return
     * @throws SocketException
     * @throws UnknownHostException
     */
    public  String getIpAddress() throws SocketException, UnknownHostException{
        String ip="";
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface intf = en.nextElement();
            String name = intf.getName();
            if (!name.contains("docker") && !name.contains("lo")) {
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    //获得IP
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ipaddress = inetAddress.getHostAddress().toString();
                        if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {

                            System.out.println(ipaddress);
                            if(!"127.0.0.1".equals(ip)){
                                ip = ipaddress;
                            }
                        }
                    }
                }
            }
        }
        return ip;
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
                try{
                    FacePojo facePojo = findFaceService.addFace(null,findFaceParam);
                    if(facePojo != null){
                        PersonForm personForm = new PersonForm();
                        personForm.setGroupId(Integer.parseInt(personGroupId));
                        personForm.setGroupName(personGroupName);
                        personForm.setName(personName);
                        personForm.setRemarks("批量录入");
                        personForm.setIdNumber(KeyUtil.generate());
                        TbPerson person = parsePersonDTO(personForm, facePojo);
                        if (!addMySqlFace(person)) {
                            return ServerResponse.createBySuccess("批量上传照片发生错误", person);
                        }
                    }else {
                        //待优化
                        if(i == photoPathList.size()-1){
                            msg += personName;
                        }else {
                            msg += personName+",";
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
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
     * 把personDTO和facePojo里的字段填充到TbPerson
     * @param personForm
     * @param facePojo
     * @return
     */
    public TbPerson parsePersonDTO(PersonForm personForm, FacePojo facePojo) {
        TbPerson person = new TbPerson();
        if(personForm.getId() != null){
            person.setId(personForm.getId());
        }
        person.setName(personForm.getName());
        person.setGender(personForm.getGender());
        person.setIdNumber(personForm.getIdNumber());
        person.setGroupName(personForm.getGroupName());
        person.setGroupId(personForm.getGroupId());
        person.setRemarks(personForm.getRemarks());
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
