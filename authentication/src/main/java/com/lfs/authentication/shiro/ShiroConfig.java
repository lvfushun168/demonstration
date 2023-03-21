package com.lfs.authentication.shiro;

import com.lfs.authentication.shiro.jwt.JwtFilter;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.shiro.mgt.SecurityManager;


import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    /**
     * first perform filter ，then if the filter detected token，turn to login with token，and verified in Realm
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager){
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();

        // 添加自己的过滤器并且取名为jwt
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        //设置自定义的JWT过滤器
        filterMap.put("jwt", new JwtFilter());
        factoryBean.setFilters(filterMap);
        factoryBean.setSecurityManager(securityManager);
        // 设置无权限时跳转的 url;
        factoryBean.setUnauthorizedUrl("/unauthorized");
        Map<String, String> filterRuleMap = new HashMap<>();
        // -----所有请求通过我们自己的JWT Filter，但是-----
    /*  filterRuleMap.put("/**", "jwt");
        // 访问 /unauthorized/** 不通过JWTFilter
        filterRuleMap.put("/login", "anon");
        filterRuleMap.put("/ws/**", "anon");
        filterRuleMap.put("/chat.html", "anon");
        filterRuleMap.put("/unauthorized", "anon");
        filterRuleMap.put("/system/createAccount", "anon");
        filterRuleMap.put("/script/**", "anon");
        ////以下是关于健康码网站的过滤
        filterRuleMap.put("/wyj/**", "anon");
        filterRuleMap.put("/prominent-citizens/**", "anon");
        filterRuleMap.put("/authentication/**", "anon");
        filterRuleMap.put("/wll/**", "anon");
    */
        factoryBean.setFilterChainDefinitionMap(filterRuleMap);
        return factoryBean;
    }


    /**
     * auth management
     */
    @Bean
    public SecurityManager securityManager(CustomRealm customRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(customRealm);
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        return securityManager;
    }


    /**
     * authorize the config
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }


    /**
     * Make the annotation effective
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }



    /**
     * Shiro lifecycle handler
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

}
