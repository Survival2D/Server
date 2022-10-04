package com.survival2d.server.repo;

import com.survival2d.server.entity.LeaderBoard;
import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository
public interface LeaderBoardRepo extends EzyMongoRepository<LeaderBoard.Id, LeaderBoard> {

}
