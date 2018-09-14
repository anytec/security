package cn.anytec.security.dao;

import cn.anytec.security.model.TbCamera;
import cn.anytec.security.model.TbCameraExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface TbCameraMapper {
    int countByExample(TbCameraExample example);

    int deleteByExample(TbCameraExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TbCamera record);

    int insertSelective(TbCamera record);

    List<TbCamera> selectByExample(TbCameraExample example);

    TbCamera selectByPrimaryKey(Integer id);

    List<TbCamera> selectInCameraSdkIds(List<String> cameraSdkIds);

    int updateByExampleSelective(@Param("record") TbCamera record, @Param("example") TbCameraExample example);

    int updateByExample(@Param("record") TbCamera record, @Param("example") TbCameraExample example);

    int updateByPrimaryKeySelective(TbCamera record);

    int updateByPrimaryKey(TbCamera record);

    List<Map<String,Object>> selectServerLabel();

}