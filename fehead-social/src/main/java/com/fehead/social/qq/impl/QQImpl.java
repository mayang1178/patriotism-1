package com.fehead.social.qq.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fehead.social.qq.QQ;
import com.fehead.social.qq.QQUserInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import java.io.IOException;

/**
 * @author lmwis on 2019-07-18 11:23
 */
public class QQImpl extends AbstractOAuth2ApiBinding implements QQ {

    Logger logger = LoggerFactory.getLogger(QQImpl.class);

    private static final String URL_GET_OPENID = "https://graph.qq.com/oauth2.0/me?access_token=%s";

    private static final String URL_GET_USER_INFO = "https://graph.qq.com/user/get_user_info?oauth_consumer_key=%s&openid=%s";

    private String appId;

    private String openId;

    private ObjectMapper objectMapper = new ObjectMapper();

    public QQImpl(String accessToken, String appId) {
        super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);

        this.appId = appId;

        String url = String.format(URL_GET_OPENID,accessToken);

        String result = getRestTemplate().getForObject(url,String.class);

        logger.info(result);

        this.openId = StringUtils.substringBetween(result,"\"openid\"","}");

    }

    @Override
    public QQUserInfo getUserInfo()  {

        String url = String.format(URL_GET_USER_INFO,appId,openId);

        String result = getRestTemplate().getForObject(url,String.class);

        QQUserInfo qqUserInfo = null;
        try {
            qqUserInfo = objectMapper.readValue(result,QQUserInfo.class);
        } catch (IOException e) {
            throw new RuntimeException("获取用户信息失败");
        }

        return qqUserInfo;
    }


}
