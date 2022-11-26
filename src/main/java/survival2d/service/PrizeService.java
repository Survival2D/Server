package survival2d.service;

import survival2d.entity.Prize;

public interface PrizeService {

  void savePrize(Prize record);

  Prize createPrize(String username, int prizeRecord);
}
