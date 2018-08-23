package cn.anytec.security.service;

import cn.anytec.security.common.ServerResponse;
import cn.anytec.security.model.TbUser;
import cn.anytec.security.model.vo.UserVO;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpSession;

public interface UserService {

    ServerResponse<UserVO> login(String uname, String upass, HttpSession session);

    ServerResponse register(TbUser user);

    ServerResponse<String> checkUsername(String username);

    ServerResponse<PageInfo> list(int pageNum, int pageSize, String keyword);

    ServerResponse delete(String userIds, HttpSession session);

    ServerResponse update(TbUser user);

    ServerResponse<UserVO> getInformation(Integer userId);

    ServerResponse checkAdminRole(TbUser user);
}
