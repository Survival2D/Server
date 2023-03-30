package survival2d.network.json;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum JsonPacketId {
  PING,
  LOGIN,
  PLAYER_MOVE,
  PLAYER_ATTACK,
  MATCH_INFO,
  ;

  public static JsonPacketId valueOf(int id) {
    if (id < 0 || id >= values().length) {
      log.error("No element matches {}", id);
      throw new IllegalArgumentException("No element matches " + id);
    }
    return values()[id];
  }
}
