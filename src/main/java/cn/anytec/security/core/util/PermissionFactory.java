package cn.anytec.security.core.util;

import cn.anytec.security.config.GeneralConfig;
import cn.anytec.security.dao.TbUserMapper;
import cn.anytec.security.model.TbUser;
import cn.anytec.security.model.TbUserExample;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by imyzt on 2018/8/21 16:50
 * 权限检查的工厂工具
 */
public class PermissionFactory {

    public boolean isAdmin () {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        TbUser currentUser = (TbUser) req.getSession().getAttribute("currentUser");
        if (null == currentUser) {
            return false;
        }
        String uname = currentUser.getUname();

        TbUserExample userExample = new TbUserExample();
        TbUserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUnameEqualTo(uname);

        TbUserMapper userMapper = ApplicationContextHolder.getBean(TbUserMapper.class);
        GeneralConfig config = ApplicationContextHolder.getBean(GeneralConfig.class);

        List<TbUser> users = userMapper.selectByExample(userExample);

        return users.size() != 0 && users.get(0).getRole() == config.getAdminRole();
    }

}
