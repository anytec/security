package cn.anytec.security.service;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.TbUser;
import com.github.pagehelper.PageInfo;

public interface UserService {

    ServerResponse<TbUser> login(String username, String password);

    ServerResponse<String> register(TbUser user);

    ServerResponse<String> checkUsername(String username);

    ServerResponse<PageInfo> list(int pageNum, int pageSize);

    ServerResponse delete(String userIds);

    ServerResponse<TbUser> update(TbUser user);

    ServerResponse<TbUser> getInformation(Integer userId);

    ServerResponse checkAdminRole(TbUser user);
}
