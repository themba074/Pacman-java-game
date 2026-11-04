import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MazeMapTest {
    @Test
    void exposesExpectedDimensionsAndKeyTiles() {
        MazeMap map = new MazeMap();

        assertEquals(21, MazeMap.ROW_COUNT);
        assertEquals(19, MazeMap.COLUMN_COUNT);
        assertEquals(32, MazeMap.TILE_SIZE);
        assertEquals(608, MazeMap.BOARD_WIDTH);
        assertEquals(672, MazeMap.BOARD_HEIGHT);
        assertEquals('P', map.tileAt(15, 9));
        assertEquals('X', map.tileAt(16, 9));
        assertEquals('Q', map.tileAt(3, 1));
    }
}
