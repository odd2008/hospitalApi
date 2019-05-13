package com.hospital.tools;

import com.alibaba.fastjson.JSONObject;
import com.hospital.constant.Constants;
import com.hospital.dao.UserDao;
import com.hospital.entity.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LoginAspectHandler {

    private final static Logger logger = LoggerFactory.getLogger(LoginAspectHandler.class);

    @Autowired
    private UserDao userDao;

    @Pointcut("execution(public * com.hospital.controller.*.*(..))")
    public void all(){

    }

    @Pointcut("execution(public * com.hospital.controller.NoTokenController.*(..))")
    public void noToken() {

    }

    @Pointcut("all() && !noToken()")
    public void loginHandle() {

    }

    @Around("loginHandle()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable, ServletException, IOException {
        Map<String, Object> resultMap = new HashMap<>();
        logger.info("进入拦截");
        //也可以用来记录一些信息，比如获取请求的url和ip
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        String authorizationToken = request.getHeader(Constants.AUTHORIZATION);
        try{
            JwtTokenHelper.isExpiration(authorizationToken);
            Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);
            logger.info(userMap.get("telephone"));
            User user = userDao.getUserByTelephone(userMap.get("telephone"));
            if (user != null){
                if (userMap.get("password").equals(user.getPasswordHash())) {
                    resultMap.put("status", "success");
                    resultMap.put("errMsg", "");
                    resultMap.put("data", "");
                } else {
                    resultMap.put("status", "error");
                    resultMap.put("errMsg", "登录失败，密码不正确！");
                    resultMap.put("data", "");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, JSONObject.toJSONString(resultMap));
                    return null;
                }
            } else {
                resultMap.put("status", "error");
                resultMap.put("errMsg", "登录失败，账号不存在！");
                resultMap.put("data", "");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, JSONObject.toJSONString(resultMap));
                return null;
            }
        } catch (Exception e){
            //token过期，抛出异常，重新登录
            resultMap.put("status", "error");
            resultMap.put("errMsg", "token验证失败！");
            resultMap.put("data", "");
            logger.info(JSONObject.toJSONString(resultMap));
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, JSONObject.toJSONString(resultMap));
            logger.info(authorizationToken);
            return null;
        }
        return  pjp.proceed();
    }
}
