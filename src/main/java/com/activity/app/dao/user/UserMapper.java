package com.activity.app.dao.user;

import com.activity.common.utils.mapper.MyBatisRepository;

@MyBatisRepository
public interface UserMapper {
    Integer selectCount();
}
