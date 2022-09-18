package com.survival2d.server.service.impl;

import com.survival2d.server.service.MatchingService;
import java.util.concurrent.atomic.AtomicLong;

public class MatchingServiceImpl implements MatchingService {
  private static final AtomicLong currentMatchId = new AtomicLong();

  @Override
  public long newMatch() {
    return currentMatchId.getAndIncrement();
  }
}
