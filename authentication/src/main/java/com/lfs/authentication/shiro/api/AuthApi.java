package com.lfs.authentication.shiro.api;


import com.lfs.authentication.shiro.dto.ServletDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="authentication")
public interface AuthApi {


        @RequestMapping(method = RequestMethod.GET, value = "/test")
        String test();

        @RequestMapping(method = RequestMethod.POST, value = "/isPermitted")
        boolean isPermitted(@RequestBody ServletDto dto);



}
