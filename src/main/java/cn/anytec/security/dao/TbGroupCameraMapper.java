package cn.anytec.security.dao;

import cn.anytec.security.model.TbGroupCamera;
import cn.anytec.security.model.TbGroupCameraExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TbGroupCameraMapper {
    int countByExample(TbGroupCameraExample example);

    int deleteByExample(TbGroupCameraExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TbGroupCamera record);

    int insertSelective(TbGroupCamera record);

    List<TbGroupCamera> selectByExample(TbGroupCameraExample example);

    TbGroupCamera selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TbGroupCamera record, @Param("example") TbGroupCameraExample example);

    int updateByExample(@Param("record") TbGroupCamera record, @Param("example") TbGroupCameraExample example);

    int updateByPrimaryKeySelective(TbGroupCamera record);

    int updateByPrimaryKey(TbGroupCamera record);
}