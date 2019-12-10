package com.leyou.auth.controller;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.service.AuthService;
import com.leyou.common.poji.UserInfo;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@EnableConfigurationProperties(JwtProperties.class)  //引用配置文件
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("login")
    public ResponseEntity<Void> login(
         @RequestParam("username") String username,
         @RequestParam("password") String password,
         HttpServletRequest request,
         HttpServletResponse response
    ){
        String token = this.authService.login(username, password);

        //身份未认证
        if (StringUtils.isBlank(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CookieUtils.setCookie(request,response,this.jwtProperties.getCookieName(),token,this.jwtProperties.getExpire()*60);

        //响应参数
        return ResponseEntity.ok(null);
    }

    /**
     * 校验用户信息
     * @param token
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUserByCookie(@CookieValue("LY_TOKEN") String token,
       HttpServletRequest request, HttpServletResponse response) {

        try {
            // 从token中解析token信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
            // 解析成功要重新刷新token
            token = JwtUtils.generateToken(userInfo, this.jwtProperties.getPrivateKey(), this.jwtProperties.getExpire());
            // 更新cookie中的token
            CookieUtils.setCookie(request, response, this.jwtProperties.getCookieName(), token, this.jwtProperties.getCookieMaxAge());

            // 解析成功返回用户信息
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 出现异常则，响应500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }
}
