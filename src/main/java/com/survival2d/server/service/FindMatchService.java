package com.survival2d.server.service;

import java.util.Optional;

public interface FindMatchService {

  Optional<Long> findMatch(long teamId);

  boolean cancelFindMatch(long teamId);
}
