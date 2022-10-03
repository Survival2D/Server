package com.survival2d.server.entity;

import com.tvd12.ezyfox.annotation.EzyId;
import com.tvd12.ezyfox.database.annotation.EzyCollection;
import lombok.Data;

@Data
@EzyCollection
public class User {

  @EzyId Long id;

  String username;
  String password;
}
