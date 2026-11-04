final class MazeMap {
    static final int ROW_COUNT = 21;
    static final int COLUMN_COUNT = 19;
    static final int TILE_SIZE = 32;
    static final int TUNNEL_ROW = 9;
    static final int BOARD_WIDTH = COLUMN_COUNT*TILE_SIZE;
    static final int BOARD_HEIGHT = ROW_COUNT*TILE_SIZE;

    // X = wall, O = skip, P = Pac-Man, Q = power pellet
    // Ghosts: b = blue, o = orange, p = pink, r = red
    private static final String[] TILES = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "XQ               QX",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O      XbpoX      O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "XQ               QX",
            "XXXXXXXXXXXXXXXXXXX"
    };

    char tileAt(int row, int column) {
        return TILES[row].charAt(column);
    }
}
