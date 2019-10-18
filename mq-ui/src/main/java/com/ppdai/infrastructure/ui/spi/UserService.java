package com.ppdai.infrastructure.ui.spi;

import com.ppdai.infrastructure.mq.biz.dto.UserInfo;

import java.util.List;


public interface UserService {

    List<UserInfo> searchUsers(String keyword, int offset, int limit);

    List<String> getDpts();

    String getCurrentDpt();    

    UserInfo findByUserId(String userId);

    List<UserInfo> findByUserIds(List<String> userIds);

    String getNamesByUserIds(String userIdS);

    List<String> getBizTypes();  
    
    boolean login(String username, String password);
}
