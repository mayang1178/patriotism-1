package com.fehead.initialize.suport;

import com.fehead.initialize.login.TelValidateCodeFilter;
import com.fehead.initialize.login.config.FeheadLoginSecurityConfig;
import com.fehead.initialize.properties.SecurityProperties;
import com.fehead.initialize.service.TelValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 写代码 敲快乐
 * だからよ...止まるんじゃねぇぞ
 * ▏n
 * █▏　､⺍
 * █▏ ⺰ʷʷｨ
 * █◣▄██◣
 * ◥██████▋
 * 　◥████ █▎
 * 　　███▉ █▎
 * 　◢████◣⌠ₘ℩
 * 　　██◥█◣\≫
 * 　　██　◥█◣
 * 　　█▉　　█▊
 * 　　█▊　　█▊
 * 　　█▊　　█▋
 * 　　 █▏　　█▙
 * 　　 █
 *
 * @author Nightnessss 2019/7/14 17:16
 */
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private AuthenticationSuccessHandler feheadAuthenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler feheadAuthenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    FeheadLoginSecurityConfig feheadLoginSecurityConfig;


    @Override
    protected void configure(HttpSecurity http) throws Exception {




        TelValidateCodeFilter filter = new TelValidateCodeFilter();

        filter.setSecurityProperties(securityProperties);
        filter.setTelValidateCodeService(getApplicationContext().getBean(TelValidateCodeService.class));
        filter.setFeheadAuthenticationFailureHandler(feheadAuthenticationFailureHandler);
        filter.setFeheadAuthenticationSuccessHandler(feheadAuthenticationSuccessHandler);


        http
                .apply(feheadLoginSecurityConfig)
                .and()
//                .addFilterBefore(telValidateCodeAuthenticationFilter,UsernamePasswordAuthenticationFilter.class)
//                .authenticationProvider(telValidateCodeAuthenticationProvider)
                .addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class)
//                .apply(feheadWebSecurityConfig)
//                .and()
                .formLogin()
                .loginPage("/authentication/require")
                .loginProcessingUrl(securityProperties.getBrowser().getFormLoginUrl())
                .successHandler(feheadAuthenticationSuccessHandler)
                .failureHandler(feheadAuthenticationFailureHandler)
//                .authenticationDetailsSource(authenticationDetailsSource)
                .and()
                .authorizeRequests()
                .antMatchers("/authentication/require",
                        securityProperties.getBrowser().getLoginPage(),
                        "/register/sendSms", "/register/register", "/email/send").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .csrf().disable();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        super
////        auth.
//
//    }
}
