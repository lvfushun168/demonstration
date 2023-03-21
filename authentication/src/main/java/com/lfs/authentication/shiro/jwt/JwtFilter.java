package com.lfs.authentication.shiro.jwt;

import com.lfs.authentication.shiro.util.UserUtil;
import com.lfs.config.enums.ErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@Slf4j
@Order(1919)
@Component
public class JwtFilter extends BasicHttpAuthenticationFilter {
    protected static String accessToken ;

    /**
     * 如果带有 token，则对 token 进行检查，否则直接通过（这是否有点）
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        //判断请求的请求头是否带上 "Token"
        if (StringUtils.isNotEmpty(accessToken)) {
            if (executeLogin(request, response)){
                return true;
            }else {
//                responseError(response, "token无效",403);
                return false;
            }
        }
//        responseError(response, "无token",401);
        return false;
    }

    /**
     * 判断用户是否想要登入。
     * 检测 header 里面是否包含 Token 字段
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("Authorization");
        return StringUtils.isNotEmpty(token);
    }

    /**
     * 执行登陆操作
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response){
        try {
//            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//            String token = httpServletRequest.getHeader("Authorization");
            if (!accessToken.startsWith("Bearer ")){
                throw new AuthenticationException("token认证失败！");
            }
            String bearer = accessToken.replace("Bearer ", "");
            JwtToken jwtToken = new JwtToken(bearer);
            // 提交给realm进行登入，如果错误他会抛出异常并被捕获
            getSubject(request, response).login(jwtToken);
            // 如果没有抛出异常则代表登入成功，返回true
        }catch (Exception ex){
            UserUtil.setErrInfo(ErrorEnum.ERRORTOKEN);
            return false;
        }
        return true;
    }



    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }


    /**
     * 将非法请求跳转到 /unauthorized/**
     */
    protected void responseError(ServletResponse response, String message,Integer code) {
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            //设置编码，否则中文字符在重定向时会变为空字符串
            message = URLEncoder.encode(message, "UTF-8");
            httpServletResponse.sendRedirect("/unauthorized"+"?message="+message+"&code="+code);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
    }



}
