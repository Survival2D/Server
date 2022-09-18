package com.survival2d.server.service.impl;

import com.survival2d.server.service.MatchingService;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import java.util.concurrent.atomic.AtomicLong;

@EzySingleton
public class MatchingServiceImpl implements MatchingService {
  private static final AtomicLong currentMatchId = new AtomicLong();

  @Override
  public long newMatch() {
    return currentMatchId.getAndIncrement();
  }
}
