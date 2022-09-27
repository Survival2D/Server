package com.survival2d.server.service;

import java.util.List;

public interface MatchingService {

  long createMatch(List<Long> teamIds);
}
