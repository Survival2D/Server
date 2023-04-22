package survival2d.match.entity.config;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BackPackType {
  LEVEL_0(100, 1, 1),
  LEVEL_1(200, 2, 2);
  private final int maxBulletCapacity;
  private final int maxMedKitCapacity;
  private final int maxBandageCapacity;
}
