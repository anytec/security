package cn.anytec.security.findface;


import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.findface.model.*;
import cn.anytec.security.model.parammodel.FindFaceParam;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import javax.annotation.PostConstruct;
import java.io.IOException;


@Component
public class FindFaceImpl implements FindFaceService {
    private static Logger logger = LoggerFactory.getLogger(FindFaceService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GeneralConfig config;


    private HttpHeaders snapRequestHeaders = null;
    private HttpHeaders staticHeaders = null;
    private static Gson gson = new Gson();

    @PostConstruct
    void init() {
        snapRequestHeaders = new HttpHeaders();
        snapRequestHeaders.add("Authorization", "token " + config.getSnapSdkToken());
        staticHeaders = new HttpHeaders();
        staticHeaders.add("Authorization", "token " + config.getStaticSdkToken());
    }

    @Override
    public DetectPojo imageDetect(byte[] photo) {
        MultiValueMap<String, Object> requestParams = new LinkedMultiValueMap<>();
        ByteArrayResource fileResource = new ByteArrayResource(photo) {
            @Override
            public String getFilename() {
                return "photo";
            }
        };
        requestParams.add("photo", fileResource);
        String url = "http://" + config.getSnapSdkIp() + ":" + config.getSnapSdkPort() + "/" + config.getSnapSdkVersion() + "/detect";
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestParams, snapRequestHeaders);
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        }catch (Exception e){
            logger.error("Http异常："+e.getMessage());
            return null;
        }
        int responseCode = responseEntity.getStatusCodeValue();
        String result = responseEntity.getBody();
        logger.info("sdk detect :" + result);
        if (responseCode == 200) {
            //return JSONObject.parseObject(result, DetectPojo.class);
            return gson.fromJson(result,DetectPojo.class);
        }
        return null;
    }


    @Override
    public VerifyPojo imagesVerify(byte[] photo1, byte[] photo2) {
        MultiValueMap<String, Object> requestParams = new LinkedMultiValueMap<>();
        ByteArrayResource fileResource1 = new ByteArrayResource(photo1) {
            @Override
            public String getFilename() {
                return "photo1";
            }
        };
        ByteArrayResource fileResource2 = new ByteArrayResource(photo2) {
            @Override
            public String getFilename() {
                return "photo2";
            }
        };
        requestParams.add("photo1", fileResource1);
        requestParams.add("photo2", fileResource2);
        String url = "http://" + config.getSnapSdkIp() + ":" + config.getSnapSdkPort() + "/" + config.getSnapSdkVersion() + "/verify";
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestParams,snapRequestHeaders);
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        }catch (Exception e){
            logger.error("Http异常："+e.getMessage());
            return null;
        }
        int responseCode = responseEntity.getStatusCodeValue();
        String result = responseEntity.getBody();
        logger.info("sdk verify :" + result);
        if (responseCode == 200) {
            //return JSONObject.parseObject(result, VerifyPojo.class);
            return gson.fromJson(result,VerifyPojo.class);

        }
        return null;
    }

    public IdentifyPojo imageIdentify(byte[] photo, FindFaceParam param) {
        MultiValueMap<String, Object> requestParams = new LinkedMultiValueMap<>();
        if(photo != null){
            ByteArrayResource fileResource = new ByteArrayResource(photo) {
                @Override
                public String getFilename() {
                    return "photo";
                }
            };
            requestParams.add("photo", fileResource);
        }else {
            String photoUrl = param.getPhotoUrl();
            if(!StringUtils.isEmpty(photoUrl)){
                requestParams.add("photo", photoUrl);
            }
        }
        String identifyUri = "";
        if (param.getFaceInfo() != null) {
            requestParams.add("bbox", param.getFaceInfo().toBbox());
        }
        if (!StringUtils.isEmpty(param.getMf_selector())) {
            requestParams.add("mf_selector", param.getMf_selector());
        }
        if (param.getN() != null && param.getN() > 0) {
            requestParams.add("n", param.getN().toString());
        }
        if (!StringUtils.isEmpty(param.getThreshold())) {
            requestParams.add("threshold", param.getThreshold());
        }
        if (param.getGalleries() != null && !StringUtils.isEmpty(param.getGalleries()[0])) {
            identifyUri = "/faces/gallery/" + param.getGalleries()[0] + "/identify";
        } else {
            identifyUri = "/identify";
        }
        String url = "http://" + param.getSdkIp() + ":" + param.getSdkPort() + "/" + param.getSdkVersion() + identifyUri;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "token " + param.getSdkToken());
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestParams,headers);
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        }catch (Exception e){
            logger.error("Http异常："+e.getMessage());
            return null;
        }
        int responseCode = responseEntity.getStatusCodeValue();
        String result = responseEntity.getBody();
        logger.info("sdk identify :" + result);
        if (responseCode == 200) {
//            return JSONObject.parseObject(result, IdentifyPojo.class);
            return gson.fromJson(result,IdentifyPojo.class);
        }
        return null;
    }



    public FacePojo addFace(byte[] photo, FindFaceParam param) {
        HttpResponse response;
        org.apache.http.HttpEntity entity;
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        if(photo != null){
            multipartEntityBuilder.addBinaryBody("photo",photo, ContentType.DEFAULT_BINARY, "photo");
        }else if(!StringUtils.isEmpty(param.getPhotoUrl())){
            multipartEntityBuilder.addTextBody("photo",param.getPhotoUrl());
        }
        if(!StringUtils.isEmpty(param.getMeta())) {
            multipartEntityBuilder.addTextBody("meta",param.getMeta());
        }
        if(!StringUtils.isEmpty(param.getBbox())){
            multipartEntityBuilder.addTextBody("bbox",param.getBbox());
        }
        if(config.isAgeOpen()){
            multipartEntityBuilder. addTextBody("age","1");
        }
        if(config.isEmotionsOpen()){
            multipartEntityBuilder.addTextBody("emotions","1");
        }
        if(config.isGenderOpen()){
            multipartEntityBuilder.addTextBody("gender","1");
        }
        String[] galleries = param.getGalleries();
        if (galleries != null && galleries.length > 0) {
            StringBuilder galleriesString = new StringBuilder();
            for (int i = 0; i < galleries.length; i++) {
                galleriesString.append(galleries[i]);
                if (i != galleries.length - 1) {
                    galleriesString.append(",");
                }
            }
            multipartEntityBuilder.addTextBody("galleries",galleriesString.toString());
        }
        entity = multipartEntityBuilder.build();
        String url = "http://" + param.getSdkIp() + ":" + param.getSdkPort() + "/" + param.getSdkVersion() + "/face";
        try {
            response = Request.Post(url)
                    .connectTimeout(10000)
                    .socketTimeout(30000)
                    .addHeader("Authorization", "Token " + param.getSdkToken())
                    .body(entity)
                    .execute().returnResponse();
            String reply = EntityUtils.toString(response.getEntity());
            int responseCode = response.getStatusLine().getStatusCode();
            if(responseCode == 200){
                return gson.fromJson(reply,FacePojo.class);
            }else {
                logger.warn("请求未正确响应：" + responseCode);
                logger.warn(reply);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*@Override
      public FacePojo addFace(byte[] photo, FindFaceParam param) {
        MultiValueMap<String, Object> requestParams = new LinkedMultiValueMap<>();
        ByteArrayResource fileResource = new ByteArrayResource(photo) {
            @Override
            public String getFilename() {
                return "photo";
            }
        };
        requestParams.add("photo", fileResource);
        if (param != null) {
            if (param.getMeta() != null) {
                requestParams.add("meta", param.getMeta());
            }
            String[] galleries = param.getGalleries();
            if (galleries != null && galleries.length > 0) {
                StringBuilder galleriesString = new StringBuilder();
                for (int i = 0; i < galleries.length; i++) {
                    galleriesString.append(galleries[i]);
                    if (i != galleries.length - 1) {
                        galleriesString.append(",");
                    }
                }
                requestParams.add("galleries", galleriesString.toString());
            }
            if (param.isFriend()) {
                requestParams.add("friend", "true");
            }
            if (param.getMf_selector() != null) {
                requestParams.add("mf_selector", param.getMf_selector());
            }
            if (param.getFaceInfo() != null) {
                requestParams.add("bbox", param.getFaceInfo().toBbox());
            }
            if (param.getCamid() != null) {
                requestParams.add("cam_id", param.getCamid());
            }
            if (param.getThreshold() != null) {
                requestParams.add("threshold", param.getThreshold());
            }
            if(config.isAgeOpen()){
                requestParams.add("age",true);
            }
            if(config.isEmotionsOpen()){
                requestParams.add("emotions",true);
            }
            if(config.isGenderOpen()){
                requestParams.add("gender",true);
            }
        }
        String url = "http://" + config.getSdkIp() + ":" + config.getSdkPort() + "/" + config.getSdkVersion() + "/face";
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestParams,requestHeaders);
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        }catch (Exception e){
            logger.error("Http异常："+e.getMessage());
            return null;
        }
        int responseCode = responseEntity.getStatusCodeValue();
        String result = responseEntity.getBody();
        logger.info("sdk addFace :" + result);
        if (responseEntity.getStatusCodeValue() == 200) {
            //return JSONObject.parseObject(result, FacePojo.class);
            return gson.fromJson(result,FacePojo.class);
        }
        return null;
    }*/

    @Override
    public boolean deleteFace(String sdkId) {
        try {
            String url = "http://" + config.getStaticSdkIp() + ":" + config.getStaticSdkPort() + "/" + config.getStaticSdkVersion() + "/face/id/" + sdkId;
            HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(null,staticHeaders);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, String.class);
            int statusCode = responseEntity.getStatusCodeValue();
            if (statusCode == 204) {
                logger.info("删除sdk中的face成功");
                return true;
            } else if (statusCode == 404) {
                logger.info("sdk库中未找到对应face");
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("删除sdk中的face失败");
        return false;
    }

    /*@Override
    public boolean deleteFace(String sdkId){
        try {
            String url = "http://"+config.getSdkIp() +":"+config.getSdkPort()+"/"+config.getSdkVersion()+"/face/id/"+sdkId;
            CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
            HttpDeleteWithBody httpdelete = new HttpDeleteWithBody(url);
            httpdelete.setHeader("Authorization", "Token " + config.getSdkToken());
            HttpResponse response = closeableHttpClient.execute(httpdelete);
            if (response.getStatusLine().getStatusCode() == 204) {
                logger.info("删除sdk中的face成功");
                return true;
            }else if(response.getStatusLine().getStatusCode() == 404){
                logger.info("sdk库中未找到对应face");
                return true;
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        logger.info("删除sdk中的face失败");
        return false;
    }*/

    @Override
    public String getCamera() {
        org.springframework.http.HttpEntity<String> requestEntity = new org.springframework.http.HttpEntity<String>(null, staticHeaders);
        ResponseEntity<String> response = restTemplate.exchange("http://" + config.getStaticSdkIp() + ":" + config.getStaticSdkPort() + "/" + config.getStaticSdkVersion() + "/camera", HttpMethod.GET, requestEntity, String.class);
        String result = response.getBody();
        logger.info("sdk getCammera : " + result);
        return result;
    }
}
