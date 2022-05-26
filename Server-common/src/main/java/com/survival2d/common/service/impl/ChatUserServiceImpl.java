package com.survival2d.common.service.impl;

import com.survival2d.common.entity.ChatUser;
import com.survival2d.common.repo.ChatUserRepo;
import com.survival2d.common.service.ChatMaxIdService;
import com.survival2d.common.service.ChatUserService;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;

@EzySingleton
public class ChatUserServiceImpl implements ChatUserService {
  @EzyAutoBind private ChatUserRepo chatUserRepo;

  @EzyAutoBind private ChatMaxIdService maxIdService;

  @Override
  public void saveUser(ChatUser user) {
    chatUserRepo.save(user);
  }

  @Override
  public ChatUser createUser(String username, String password) {
    ChatUser user = new ChatUser();
    user.setId(maxIdService.incrementAndGet("chat_user"));
    user.setUsername(username);
    user.setPassword(password);
    chatUserRepo.save(user);
    return user;
  }

  @Override
  public ChatUser getUser(String username) {
    return chatUserRepo.findByField("username", username);
  }
}
