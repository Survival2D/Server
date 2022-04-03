class Vector {
  static readonly Zero = new Vector();
  static readonly XAxis = new Vector(1, 0);
  static readonly YAxis = new Vector(0, 1);
  x: number = 0;
  y: number = 0;

  constructor(x: number = 0, y: number = 0) {
    this.x = x;
    this.y = y;
  }

  static add(v1: Vector, v2: Vector) {
    return new Vector(v1.x + v2.x, v1.y + v2.y);
  }

  static sub(v1: Vector, v2: Vector) {
    return new Vector(v1.x - v2.x, v1.y - v2.y);
  }

  static mul(n: number, v: Vector) {
    return new Vector(n * v.x, n * v.y);
  }

  static dot(v1: Vector, v2: Vector) {
    return v1.x * v2.x + v1.y * v2.y;
  }

  static lenSquared(v: Vector) {
    return this.dot(v, v);
  }

  static len(v: Vector) {
    return Math.sqrt(this.lenSquared(v));
  }
}
