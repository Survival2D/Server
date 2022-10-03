package com.survival2d.server.repo;

import com.survival2d.server.entity.Credential;
import com.survival2d.server.entity.GamePlayerId;
import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository
public interface CredentialRepo extends EzyMongoRepository<GamePlayerId, Credential> {}
