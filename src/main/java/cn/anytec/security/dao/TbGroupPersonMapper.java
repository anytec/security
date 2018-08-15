package cn.anytec.security.dao;

import cn.anytec.security.model.TbGroupPerson;
import cn.anytec.security.model.TbGroupPersonExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TbGroupPersonMapper {
    int countByExample(TbGroupPersonExample example);

    int deleteByExample(TbGroupPersonExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TbGroupPerson record);

    int insertSelective(TbGroupPerson record);

    List<TbGroupPerson> selectByExample(TbGroupPersonExample example);

    TbGroupPerson selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TbGroupPerson record, @Param("example") TbGroupPersonExample example);

    int updateByExample(@Param("record") TbGroupPerson record, @Param("example") TbGroupPersonExample example);

    int updateByPrimaryKeySelective(TbGroupPerson record);

    int updateByPrimaryKey(TbGroupPerson record);
}