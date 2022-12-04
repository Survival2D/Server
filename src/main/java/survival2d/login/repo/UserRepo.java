package survival2d.login.repo;

import survival2d.login.entity.User;
import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository("userRepo")
public interface UserRepo extends EzyMongoRepository<Long, User> {

}
