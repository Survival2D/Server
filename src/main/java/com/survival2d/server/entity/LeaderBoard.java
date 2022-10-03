package com.survival2d.server.entity;

import com.tvd12.ezyfox.database.annotation.EzyCollection;
import com.tvd12.ezyfox.database.annotation.EzyCollectionId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EzyCollection
@NoArgsConstructor
@AllArgsConstructor
public class LeaderBoard {

  @EzyCollectionId(composite = true)
  private Id id;

  private long score;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Id {

    private String game;
    private long gameId;
    private String player;
  }
}
