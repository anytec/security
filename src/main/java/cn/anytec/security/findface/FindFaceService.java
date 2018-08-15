package cn.anytec.security.findface;

import cn.anytec.security.findface.model.*;
import cn.anytec.security.model.parammodel.FindFaceParam;
import org.springframework.stereotype.Service;


@Service
public interface FindFaceService {

    DetectPojo imageDetect(byte[] photo);

    VerifyPojo imagesVerify(byte[] photo1, byte[] photo2);

    IdentifyPojo imageIdentify(byte[] photo, FindFaceParam param);

    FacePojo addFace(byte[] photo, FindFaceParam param);

    boolean deleteFace(String sdkId);

    String getCamera();
}
