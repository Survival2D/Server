package com.survival2d.server.repo;

import com.survival2d.server.entity.GameObjectPosition;
import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyQuery;
import com.tvd12.ezyfox.database.annotation.EzyRepository;
import java.util.List;

@EzyRepository
public interface GameObjectPositionRepo
    extends EzyMongoRepository<GameObjectPosition.Id, GameObjectPosition> {

  @EzyQuery("{$and:[{'_id.game':?0},{'_id.gameId':?1}]}")
  List<GameObjectPosition> findByGameAndGameId(String game, long gameId);

  @EzyQuery("{$and:[{'_id.game':?0},{'_id.gameId':?1}]}")
  int deleteByGameAndGameId(String game, long gameId);
}
