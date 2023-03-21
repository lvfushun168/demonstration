package com.lfs.authentication.shiro;

import com.lfs.authentication.shiro.entity.User;
import com.lfs.authentication.shiro.jwt.JwtToken;
import com.lfs.authentication.shiro.service.PermissionService;
import com.lfs.authentication.shiro.service.RoleService;
import com.lfs.authentication.shiro.service.UserService;
import com.lfs.authentication.shiro.util.UserUtil;
import com.lfs.config.enums.ErrorEnum;
import com.lfs.config.model.OperationException;
import com.lfs.config.util.CacheMapUtil;
import com.lfs.config.util.JwtUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * Created with IntelliJ IDEA
 *
 * @Author yuanhaoyue swithaoy@gmail.com
 * @Description 自定义 Realm
 * @Date 2018-04-09
 * @Time 16:58
 */
@Component
public class CustomRealm extends AuthorizingRealm implements CacheMapUtil {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    /**
     * 必须重写此方法，不然会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) {
        String token = (String) authenticationToken.getCredentials();
        // 解密获得username，用于和数据库进行对比
        String username = JwtUtil.getUsername(token);
        if (username == null || !JwtUtil.verify(token, username)) {
            throw new AuthenticationException("token认证失败！");
        }
        String redisToken = redisTemplate.opsForValue().get(username);
//        String redisToken = tokenMap.get(username);
        if (!redisToken.equals(token)){
            throw new OperationException(ErrorEnum.ERRORTOKEN);
        }
        User user = userService.queryByUserName(username);
        if (!Optional.ofNullable(user).isPresent()) {
            throw new AuthenticationException("该用户不存在！");
        }
        UserUtil.setUser(user);
        return new SimpleAuthenticationInfo(token, token, "MyRealm");
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = JwtUtil.getUsername(principals.toString());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        User user = userService.queryByUserName(username);
        //获得该用户角色权限
        Set<String> roles = roleService.queryUserRoleByUserId(user.getId());
        Set<String> permissions = permissionService.queryUserPermissByUserId(user.getId());
        info.setRoles(roles);
        info.setStringPermissions(permissions);
        return info;
    }
}
