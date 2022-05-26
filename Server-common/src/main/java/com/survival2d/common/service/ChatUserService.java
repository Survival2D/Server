package com.survival2d.common.service;

import com.survival2d.common.entity.ChatUser;

public interface ChatUserService {
  void saveUser(ChatUser user);
  ChatUser createUser(String username, String password);
  ChatUser getUser(String username);
}
