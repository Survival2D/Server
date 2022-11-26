package survival2d.repo;

import survival2d.entity.GamePlayerId;
import survival2d.entity.PlayerCurrentGame;
import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository
public interface PlayerCurrentGameRepo
    extends EzyMongoRepository<GamePlayerId, PlayerCurrentGame> {

}
