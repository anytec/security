package cn.anytec.security.service;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.TbUser;
import cn.anytec.security.model.dto.UserDTO;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpSession;

public interface UserService {

    ServerResponse<UserDTO> login(String account, String upass, HttpSession session);

    ServerResponse register(TbUser user);

    ServerResponse<String> checkaccount(String account);

    ServerResponse<PageInfo> list(int pageNum, int pageSize, String keyword);

    ServerResponse delete(String userIds, HttpSession session);

    ServerResponse update(TbUser user);

    ServerResponse<UserDTO> getInformation(Integer userId);

    ServerResponse checkAdminRole(TbUser user);
}
