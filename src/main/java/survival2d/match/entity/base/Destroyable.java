package survival2d.match.entity.base;

public interface Destroyable {
  void setDestroyed(boolean destroyed);

  boolean isDestroyed();
  default void markDestroyed() {
    setDestroyed(true);
  }
}
