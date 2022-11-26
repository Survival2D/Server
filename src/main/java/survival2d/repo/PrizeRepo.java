package survival2d.repo;

import survival2d.entity.Prize;
import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository("prizeRepo")
public interface PrizeRepo extends EzyMongoRepository<Long, Prize> {

}
