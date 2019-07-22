package com.fehead.initialize.login;

import com.fehead.initialize.service.model.ValidateCode;
import com.fehead.initialize.util.ValidateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lmwis on 2019-07-20 19:00
 */
public class TelValidateCodeFilter extends OncePerRequestFilter {

    public static final Logger logger = LoggerFactory.getLogger(TelValidateCodeFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String url = request.getRequestURI();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        logger.info("username:"+username);
        logger.info("password:"+password);

        logger.info("请求的URL："+url);

        if(StringUtils.equals(url,"/loginByOtp")){

            ServletWebRequest servletWebRequest = new ServletWebRequest(request);

            String tel =servletWebRequest.getParameter("tel");

            logger.info("请求的手机号为："+tel);

            ValidateCode code = ValidateUtil.createCode(4);

            logger.info("生成的验证码为："+code.getCode());

            return;

        }


        filterChain.doFilter(request,response);
    }
}