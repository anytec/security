package cn.anytec.security.dao;

import cn.anytec.security.model.TbPerson;
import cn.anytec.security.model.TbPersonExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TbPersonMapper {
    int countByExample(TbPersonExample example);

    int deleteByExample(TbPersonExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TbPerson record);

    int insertSelective(TbPerson record);

    List<TbPerson> selectByExample(TbPersonExample example);

    TbPerson selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TbPerson record, @Param("example") TbPersonExample example);

    int updateByExample(@Param("record") TbPerson record, @Param("example") TbPersonExample example);

    int updateByPrimaryKeySelective(TbPerson record);

    int updateByPrimaryKey(TbPerson record);
}