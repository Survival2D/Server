package survival2d.login.repo;

import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;
import survival2d.login.entity.User;

@EzyRepository("userRepo")
public interface UserRepo extends EzyMongoRepository<Long, User> {

}
