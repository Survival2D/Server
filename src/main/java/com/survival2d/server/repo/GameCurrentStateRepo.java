package com.survival2d.server.repo;

import com.survival2d.server.entity.GameCurrentState;
import com.survival2d.server.entity.GameId;
import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository
public interface GameCurrentStateRepo extends EzyMongoRepository<GameId, GameCurrentState> {

}
