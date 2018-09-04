package cn.anytec.security.service;

import java.io.Serializable;

public interface BaseService<T, ID extends Serializable> {

    /**
     * 通过id删除
     */
    int deleteByPrimaryKey(ID id);

    /**
     *添加
     */
    int insert(T record);

    /**
     *不为空字段添加
     */
    int insertSelective(T record);

    /**
     *通过id查询
     */
    T selectByPrimaryKey(ID id);

    /**
     *通过id不为空字段修改
     */
    int updateByPrimaryKeySelective(T record);


    /**
     *通过id修改
     */
    int updateByPrimaryKey(T record);
}
