package models.Enums;

public enum Direction {

    EAST(1, 0),
    WEST(-1, 0),
    NORTH(0, 1),
    SOUTH(0, -1),
    SOUTHWEST(-1, -1),
    SOUTHEAST(1, -1),
    NORTHWEST(-1, 1),
    NORTHEAST(1, 1);

    private int dx;
    private int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getX() {
        return dx;
    }

    public int getY() {
        return dy;
    }

    public static Direction findDirection(String direction) {
        for (Direction dir : Direction.values()) {
            if (dir.toString().equalsIgnoreCase(direction.trim())) {
                return dir;
            }
        }
        return null;
    }
}
