package com.survival2d.server.repo;

import com.survival2d.server.entity.Wheel;
import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository("wheelRepo")
public interface WheelRepo extends EzyMongoRepository<String, Wheel> {

}
