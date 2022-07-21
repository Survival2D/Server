package com.survival2d.server.repo;

import com.survival2d.server.entity.Prize;
import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository("prizeRepo")
public interface PrizeRepo extends EzyMongoRepository<Long, Prize> {
}
