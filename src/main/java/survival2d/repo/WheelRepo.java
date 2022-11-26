package survival2d.repo;

import survival2d.entity.Wheel;
import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository("wheelRepo")
public interface WheelRepo extends EzyMongoRepository<String, Wheel> {

}
