package com.survival2d.server.repo;

import com.survival2d.server.entity.GamePlayerId;
import com.survival2d.server.entity.PlayerCurrentGame;
import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository
public interface PlayerCurrentGameRepo
    extends EzyMongoRepository<GamePlayerId, PlayerCurrentGame> {}
