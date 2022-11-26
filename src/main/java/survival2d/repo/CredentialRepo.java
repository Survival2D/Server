package survival2d.repo;

import survival2d.entity.Credential;
import survival2d.entity.GamePlayerId;
import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository
public interface CredentialRepo extends EzyMongoRepository<GamePlayerId, Credential> {

}
