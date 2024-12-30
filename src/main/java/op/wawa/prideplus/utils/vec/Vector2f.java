package op.wawa.prideplus.utils.vec;

public class Vector2f {
    public float x;
    public float y;

    public Vector2f() {
        this(0.0f, 0.0f);
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(Vector2f v) {
        this.x = v.x;
        this.y = v.y;
    }

    public Vector2f(Vector3f v) {
        this(v.x, v.y);
    }

    public Vector2f get() {
        return new Vector2f(this.x, this.y);
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector2f v) {
        this.x = v.x;
        this.y = v.y;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float dot(Vector2f v) {
        return this.x * v.x + this.y * v.y;
    }

    public float distance(Vector2f v) {
        float dx = this.x - v.x;
        float dy = this.y - v.y;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public boolean isZero() {
        return this.x == 0.0f && this.y == 0.0f;
    }

    public boolean equals(Vector2f v) {
        return this.x == v.x && this.y == v.y;
    }

    public Vector2f clone() {
        return new Vector2f(this.x, this.y);
    }

    public Vector2f add(Vector2f v) {
        return new Vector2f(this.x + v.x, this.y + v.y);
    }

    public Vector2f subtract(Vector2f v) {
        return new Vector2f(this.x - v.x, this.y - v.y);
    }

    public Vector2f scale(double scalar) {
        return new Vector2f((float)((double)this.x * scalar), (float)((double)this.y * scalar));
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public Vector2f normalize() {
        float len = (float)this.length();
        return new Vector2f(this.x / len, this.y / len);
    }

    public String toString() {
        return "Vector2f{x=" + this.x + ", y=" + this.y + '}';
    }
}

