package com.survival2d.server.service;

import com.survival2d.server.entity.Prize;

public interface PrizeService {

  void savePrize(Prize record);

  Prize createPrize(String username, int prizeRecord);
}
