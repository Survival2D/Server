package com.survival2d.server.controller;

import com.tvd12.ezyfox.sercurity.EzySHA256;
import org.testng.annotations.Test;

public class UserLoginControllerTest {
  @Test
  public void testEncodeEmptyString() {
    System.out.println(EzySHA256.cryptUtfToLowercase(""));
  }
}
