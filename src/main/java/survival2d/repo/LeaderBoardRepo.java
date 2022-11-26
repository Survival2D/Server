package survival2d.repo;

import survival2d.entity.LeaderBoard;
import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository
public interface LeaderBoardRepo extends EzyMongoRepository<LeaderBoard.Id, LeaderBoard> {

}
