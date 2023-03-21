package com.lfs.openai.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @Author: LFS
 * @Date 2023/2/18
 * @Description: 公众号入口
 **/
@RequestMapping("lfs")
@Controller
@Slf4j
public class WeXinController {
    /**
     * 切记：这里是自定义的token，需和你微信配置界面提交的token完全一致
     */
    private final String TOKEN = "sgwishandsome";


    @GetMapping("response")
    public void checkSignature(HttpServletRequest request, HttpServletResponse response) {
        log.info("开始校验签名");
        /**
         * 接收微信服务器发送请求时传递过来的参数
         */
        //签名
        String signature = request.getParameter("signature");
        //时间戳
        String timestamp = request.getParameter("timestamp");
        //随机数
        String nonce = request.getParameter("nonce");
        //随机字符串
        String echostr = request.getParameter("echostr");
        String method = request.getMethod();

        if(method.equals("GET")){
            //get请求，说明是在配置微信后台的url过来的请求
            /**
             * 将token、timestamp、nonce三个参数进行字典序排序
             * 并拼接为一个字符串
             */
            String sortStr = this.sort(TOKEN, timestamp, nonce);
            /**
             * 对排序后的sortStr进行shal加密
             */
            String mySignature = shal(sortStr);


            /**
             * 校验"微信服务器传递过来的签名"和"加密后的字符串"是否一致, 如果一致则签名通过，否则不通过
             * 每次刚启动项目后，把下边的注释打开，与微信基本配置里的URL进行交互
             * 配置完毕后把下边代码注释掉即可
             */
            if (!"".equals(signature) && !"".equals(mySignature) && signature.equals(mySignature)) {

                log.info("签名校验通过");
                try {
                    //必须响应给微信，不然会提示"token校验失败"
                    if(echostr!=null&&echostr!=""){
                        response.getWriter().write(echostr);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.info("校验签名失败");
            }
        }else{
            //post请求，说明是微信公众号里来的请求
            try {
                request.setCharacterEncoding("UTF-8");
                response.setCharacterEncoding("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
//            Map<String, String> map = MessageHandlerUtils.getMsgFromClient(request);
//            System.out.println("开始构造消息");
//            String result = "";
//            result = MessageHandlerUtils.buildXml(map);
//            if (result.equals("")) {
//                result = "未正确响应";
//            }
//            try {
//                response.getWriter().write(result);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        //开始解析解析公众号里发来的消息 将解析结果存储在HashMap中


    }

    /**
     * 参数排序
     *
     * @param token
     * @param timestamp
     * @param nonce
     * @return
     */
    public String sort(String token, String timestamp, String nonce) {
        String[] strArray = {token, timestamp, nonce};
        Arrays.sort(strArray);
        StringBuilder sb = new StringBuilder();
        for (String str : strArray) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 字符串进行shal加密
     *
     * @param str
     * @return
     */
    public String shal(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
