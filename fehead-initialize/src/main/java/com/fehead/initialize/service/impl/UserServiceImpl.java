package com.fehead.initialize.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.fehead.initialize.dao.UserDOMapper;
import com.fehead.initialize.dao.UserPasswordDOMapper;
import com.fehead.initialize.dataobject.UserDO;
import com.fehead.initialize.dataobject.UserPasswordDO;
import com.fehead.initialize.error.BusinessExpection;
import com.fehead.initialize.error.EmBusinessError;
import com.fehead.initialize.service.UserService;
import com.fehead.initialize.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
 * @author Nightnessss 2019/7/8 14:53
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Override
    public UserModel getUserById(Integer id) {
        // 调用userdomapper获取到对应用户dataobject
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if (userDO == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(id);

        UserModel userModel = convertFromDO(userDO, userPasswordDO);

        return userModel;
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessExpection {
        // 将userModel转为dataobject存入数据库
        if (userModel == null) {
            throw new BusinessExpection(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if (StringUtils.isEmpty(userModel.getName()) ||
                userModel.getGender() == null ||
                userModel.getAge() == null ||
                StringUtils.isEmpty(userModel.getTelphone())) {
            throw new BusinessExpection(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if (StringUtils.isEmpty(userModel.getEncrptPassword())) {
            throw new BusinessExpection(EmBusinessError.PARAMETER_VALIDATION_ERROR, "密码不能为空");
        }
        // userModel -> userDO
        UserDO userDO = convertFromModel(userModel);
        try {
            userDOMapper.insertSelective(userDO);
        } catch (DuplicateKeyException ex) {
            throw new BusinessExpection(EmBusinessError.PARAMETER_VALIDATION_ERROR, "手机号已被注册");
        }

        userModel.setId(userDO.getId());

        // userModel -> userPasswordDO
        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);

        return;
    }

    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BusinessExpection {
        // 判空
        if (StringUtils.isEmpty(telphone) || StringUtils.isEmpty(encrptPassword)) {
            throw new BusinessExpection(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        // 用户校验
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if (userDO == null) {
            throw new BusinessExpection(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        if (!StringUtils.equals(encrptPassword, userPasswordDO.getEncrptPassword())) {
            throw new BusinessExpection(EmBusinessError.USER_LOGIN_FAIL);
        }

        UserModel userModel =  convertFromDO(userDO,userPasswordDO);
        return userModel;
        // 标记用户已登录

    }

    private UserPasswordDO convertPasswordFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }

        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());

        return userPasswordDO;
    }

    private UserDO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }

        UserDO userDO = new UserDO();

        BeanUtils.copyProperties(userModel, userDO, "id");
//        userDO.setName(userModel.getName());
//        userDO.setGender(userModel.getGender());
//        userDO.setAge(userModel.getAge());
//        userDO.setTelphone(userModel.getTelphone());
//        userDO.setThirdPartyId(userModel.getThirdPartyId());
//        userDO.setRegisterMode(userModel.getRegisterMode());
        return userDO;
    }

    private UserModel convertFromDO(UserDO userDO, UserPasswordDO userPasswordDO) {
        if (userDO == null) {
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO, userModel);
        if (userPasswordDO != null) {
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }

        return userModel;
    }
}