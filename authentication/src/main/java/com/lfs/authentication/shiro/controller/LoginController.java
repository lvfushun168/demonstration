package com.lfs.authentication.shiro.controller;

import com.lfs.authentication.shiro.dto.CreateAccountDto;
import com.lfs.authentication.shiro.dto.ServletDto;
import com.lfs.authentication.shiro.entity.Permission;
import com.lfs.authentication.shiro.entity.User;
import com.lfs.authentication.shiro.jwt.JwtFilter;
import com.lfs.authentication.shiro.service.PermissionService;
import com.lfs.authentication.shiro.service.UserService;
import com.lfs.authentication.shiro.util.UserUtil;
import com.lfs.authentication.shiro.vo.PermissionVO;
import com.lfs.system.annotation.Idempotent;
import com.lfs.config.enums.ErrorEnum;
import com.lfs.config.model.OperationException;
import com.lfs.config.model.Result;
import com.lfs.config.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@AllArgsConstructor
@Slf4j
public class LoginController extends JwtFilter {
    PermissionService permissionService;
    RedisTemplate<String,String> redisTemplate;
    UserService userService;
    LogoutController logoutController;



    @GetMapping("/test")
    public String test(){
        return "test";
    }

    /**
     * 登陆接口
     * 1.游客正常登陆;
     * 2.如果已携带token，不管是否正确均提示信息异常，并执行登出最后重新登陆；
     * 幂等性接口测试
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/login")
    @Idempotent(expireTime = 5L,message = "差不多得了，恁的手速太快力🤡")
    public Result login(@RequestParam(value = "username") String username, @RequestParam(value = "password")String password, ServletRequest request){
        this.loginStatus(request);
        this.checkUserAccount(username, password);
        String token = JwtUtil.tokenGenerate(username);
        redisTemplate.opsForValue().set(username,token,3600L, TimeUnit.SECONDS);
//        tokenMap.put(username,token);
        return  Result.success("Bearer "+token);
    }

    /**
     * 登陆状态检查
     * @param request
     */
    private void loginStatus(ServletRequest request){
        if (super.isLoginAttempt(request, null)) {
            logoutController.logout(request);
            throw new OperationException(ErrorEnum.LOGINERR,"  请重新登陆");
        }
    }

    /**
     * 账号检查
     * @param username
     * @param password
     * @return
     */
    private User checkUserAccount(String username, String password){
        if(StringUtils.isEmpty(username)){
            throw new OperationException(ErrorEnum.USERNAME);
        }
        if(StringUtils.isEmpty(password)){
            throw new OperationException(ErrorEnum.PASSWORD);
        }
        User user = userService.queryByUserName(username);
        if (!Optional.ofNullable(user).isPresent()){
            throw new OperationException(ErrorEnum.USERERR);
        }
        if (!password.equals(user.getPassword())){
            throw new OperationException(ErrorEnum.PASSWORDERROR);
        }
        return user;
    }

    /**
     * 处理权限错误问题专用重定向接口
     * @param message
     * @param code
     * @return
     */
    @GetMapping("/unauthorized")
    @Scope("prototype")
    public Result unauthorized(@RequestParam(value = "message") String message,@RequestParam(value = "code") Integer code){
        //redirect
        return Result.error(code,message);
    }

    /**
     * 查询指定权限id下的所有权限
     * 测试高级查询
     * @param id
     * @return
     */
    @GetMapping("/system/findAllNextPermission")
    public Result findAllNextPermissionByPermissionId(@RequestParam Integer id){
        List<Permission> list = permissionService.findAllNextPermissionByPermissionId(id);
        List<PermissionVO> collect = list.stream().map(entity -> {
            PermissionVO vo = new PermissionVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.list(collect);
    }

    /**
     * 创建用户接口
     * 如果登陆了就记录创建者(即便传入了创建者id)；没有登陆如果传入了创建者就用这个id，没有传入就写为visitor;
     * @param dto
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/system/createAccount")
    public Result createAccount(@RequestBody @Valid CreateAccountDto dto, ServletRequest request, ServletResponse response){
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("Authorization");
        if (StringUtils.isNotEmpty(token)){
            try {
                super.executeLogin(request,response);
            }catch (Exception ex){
                log.info("登陆错误信息：{}",ex.getMessage());
            }
        }
        User user = UserUtil.get();
//        dto.setCrtUserId(Optional.ofNullable(user).isPresent() ? user.getId().toString():"visitor");
        Optional.ofNullable(user).ifPresent(entity -> dto.setCrtUserId(entity.getId().toString()));
        userService.createAccount(dto);
        return Result.success();
    }


    @PostMapping("/isPermitted")
    public boolean isPermitted(@RequestBody ServletDto dto){
        super.accessToken = dto.getToken();
        return super.isAccessAllowed(dto.getRequest(),dto.getResponse(),new Object());
    }
}
