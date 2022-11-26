package survival2d.repo;

import survival2d.entity.GameCurrentState;
import survival2d.entity.GameId;
import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository
public interface GameCurrentStateRepo extends EzyMongoRepository<GameId, GameCurrentState> {

}
