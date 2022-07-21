package com.survival2d.server.service.impl;

import com.survival2d.server.entity.Prize;
import com.survival2d.server.service.MaxIdService;
import com.survival2d.server.service.PrizeService;
import com.survival2d.server.repo.PrizeRepo;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import lombok.Setter;

@Setter
@EzySingleton("prizeService")
public class PrizeServiceImpl implements PrizeService {
	
	@EzyAutoBind
	private PrizeRepo prizeRepo;
	
	@EzyAutoBind
	private MaxIdService maxIdService;
	
	@Override
	public void savePrize(Prize record) {
		prizeRepo.save(record);
	}
	
	@Override
	public Prize createPrize(String username, int prizeRecord) {
		Prize prize = new Prize();
		prize.setId(maxIdService.incrementAndGet("prize"));
		prize.setUsername(username);
		prize.setPrize(prizeRecord);
		
		prizeRepo.save(prize);
		
		return prize;
	}
}
