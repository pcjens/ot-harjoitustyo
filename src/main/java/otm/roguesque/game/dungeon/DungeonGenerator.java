package otm.roguesque.game.dungeon;

import java.util.ArrayList;
import otm.roguesque.game.GlobalRandom;
import otm.roguesque.game.entities.Door;
import otm.roguesque.game.entities.Item;
import otm.roguesque.game.entities.Rat;

/**
 * Tämä luokka luo uusia Dungeoneita. Käytännössä tämä on vain yhden funktion
 * luokka, ja kaikki tila jota tämä luokka sisältää on tuon funktion asettamaa.
 * Tämän takia tästä luokasta ei voi esimerkiksi luoda instansseja, vaan on
 * kokonaan staattinen.
 *
 * @author Jens Pitkänen
 */
public class DungeonGenerator {

    private enum RoomType {
        StartRoom, EndRoom, MonsterRoom, ItemRoom
    }

    // These work as a sort of "internal state" for the dungeon generation
    // algortims, to lessen the amount of parameters.
    // Reset on every generateNewDungeon call.
    private static Dungeon dungeon;
    private static int level;
    private static int width;
    private static int height;
    private static TileType[] tiles;

    private DungeonGenerator() {
    }

    public static Dungeon generateNewDungeon(int level) {
        DungeonGenerator.dungeon = new Dungeon(level);
        DungeonGenerator.level = level;
        DungeonGenerator.width = dungeon.getWidth();
        DungeonGenerator.height = dungeon.getHeight();
        DungeonGenerator.tiles = dungeon.getTiles();

        generateDungeon();
        dungeon.updateSolidity();
        return dungeon;
    }

    // Sorry about the fragmentation of the functions; CheckStyle doesn't like long functions.
    private static void generateDungeon() {
        int roomCountX = width / Dungeon.MAX_ROOM_WIDTH;
        int roomCountY = height / Dungeon.MAX_ROOM_HEIGHT;
        int minimumRooms = (int) Math.ceil(Dungeon.MAX_ROOMS / 3.0);
        int roomCount = GlobalRandom.get().nextInt(Dungeon.MAX_ROOMS - minimumRooms + 1) + minimumRooms;
        RoomType[] rooms = generateRoomTypes(roomCount, roomCountX, roomCountY);
        generateRooms(rooms, roomCountX, roomCountY);
    }

    private static void generateRooms(RoomType[] rooms, int roomCountX, int roomCountY) {
        for (int roomY = 0; roomY < roomCountY; roomY++) {
            for (int roomX = 0; roomX < roomCountX; roomX++) {
                RoomType type = rooms[roomX + roomY * roomCountX];
                if (type != null) {
                    int roomWidth = GlobalRandom.get().nextInt(Dungeon.MAX_ROOM_WIDTH - (Dungeon.MIN_ROOM_WIDTH + Dungeon.MIN_ROOM_MARGIN)) + Dungeon.MIN_ROOM_WIDTH;
                    int roomHeight = GlobalRandom.get().nextInt(Dungeon.MAX_ROOM_HEIGHT - (Dungeon.MIN_ROOM_HEIGHT + Dungeon.MIN_ROOM_MARGIN)) + Dungeon.MIN_ROOM_HEIGHT;
                    int x = roomX * Dungeon.MAX_ROOM_WIDTH + GlobalRandom.get().nextInt(Dungeon.MAX_ROOM_WIDTH - roomWidth - 2) + 2;
                    int y = roomY * Dungeon.MAX_ROOM_HEIGHT + GlobalRandom.get().nextInt(Dungeon.MAX_ROOM_HEIGHT - roomHeight - 2) + 2;
                    generateRoom(type, x, y, roomWidth, roomHeight);
                    generateCorridors(rooms, roomX, roomY, roomCountX, roomCountY,
                            x, y, roomWidth, roomHeight, roomX * Dungeon.MAX_ROOM_WIDTH, roomY * Dungeon.MAX_ROOM_HEIGHT,
                            (roomX + 1) * Dungeon.MAX_ROOM_WIDTH, (roomY + 1) * Dungeon.MAX_ROOM_HEIGHT);
                    generateDoors(x, y, roomWidth, roomHeight);
                }
            }
        }
    }

    private static void generateCorridors(RoomType[] rooms, int roomIndexX, int roomIndexY,
            int roomCountX, int roomCountY, int roomX, int roomY, int roomWidth, int roomHeight,
            int startX, int startY, int endX, int endY) {
        if (roomIndexX > 0 && rooms[(roomIndexX - 1) + roomIndexY * roomCountX] != null) {
            int otherY = findCorridorY(startX, startY, endY);
            int y = roomY + 1 + GlobalRandom.get().nextInt(roomHeight - 2);
            generateHorizontalCorridor(y, startX, roomX);
            generateVerticalCorridor(startX, y, otherY); // the corridor connecting the corridors
        }
        if (roomIndexY > 0 && rooms[roomIndexX + (roomIndexY - 1) * roomCountX] != null) {
            int otherX = findCorridorX(startY, startX, endX);
            int x = roomX + 1 + GlobalRandom.get().nextInt(roomWidth - 2);
            generateVerticalCorridor(x, startY, roomY);
            generateHorizontalCorridor(startY, x, otherX); // the corridor connecting the corridors
        }
        if (roomIndexX < roomCountX - 1 && rooms[(roomIndexX + 1) + roomIndexY * roomCountX] != null) {
            generateHorizontalCorridor(roomY + 1 + GlobalRandom.get().nextInt(roomHeight - 2), roomX + roomWidth - 1, endX);
        }
        if (roomIndexY < roomCountY - 1 && rooms[roomIndexX + (roomIndexY + 1) * roomCountX] != null) {
            generateVerticalCorridor(roomX + 1 + GlobalRandom.get().nextInt(roomWidth - 2), roomY + roomHeight - 1, endY);
        }
    }

    private static int findCorridorY(int x, int startY, int endY) {
        for (int y = startY; y <= endY; y++) {
            if (tiles[x + y * width] == TileType.Corridor) {
                return y;
            }
        }
        return -1;
    }

    private static int findCorridorX(int y, int startX, int endX) {
        for (int x = startX; x <= endX; x++) {
            if (tiles[x + y * width] == TileType.Corridor) {
                return x;
            }
        }
        return -1;
    }

    private static void generateHorizontalCorridor(int y, int startX, int endX) {
        if (startX < 0 || endX < 0) {
            return;
        }
        if (startX > endX) {
            int temp = startX;
            startX = endX;
            endX = temp;
        }
        for (int x = startX; x <= endX; x++) {
            tiles[x + y * width] = TileType.Corridor;
        }
    }

    private static void generateVerticalCorridor(int x, int startY, int endY) {
        if (startY < 0 || endY < 0) {
            return;
        }
        if (startY > endY) {
            int temp = startY;
            startY = endY;
            endY = temp;
        }
        for (int y = startY; y <= endY; y++) {
            tiles[x + y * width] = TileType.Corridor;
        }
    }

    private static RoomType[] generateRoomTypes(int roomCount, int roomCountX, int roomCountY) {
        RoomType[] rooms = new RoomType[roomCountX * roomCountY];
        int itemRoomCount = 1 + GlobalRandom.get().nextInt(2);
        int roomX = GlobalRandom.get().nextInt(roomCountX);
        int roomY = GlobalRandom.get().nextInt(roomCountY);
        RoomType room = RoomType.StartRoom;
        ArrayList<Integer> visitedRooms = new ArrayList();
        ArrayList<Integer> possibleRooms = new ArrayList();
        while (roomCount > 0) {
            rooms[roomX + roomY * roomCountX] = room;
            addNeighborRooms(possibleRooms, visitedRooms, roomX, roomY, roomCountX, roomCountY);
            roomCount--;

            int roomIdx = possibleRooms.remove(GlobalRandom.get().nextInt(possibleRooms.size()));
            roomX = roomIdx % roomCountX;
            roomY = roomIdx / roomCountX;
            room = rollRoom(roomCount, itemRoomCount);
            itemRoomCount -= room == RoomType.ItemRoom ? 1 : 0;
        }
        return rooms;
    }

    private static RoomType rollRoom(int roomCount, int itemRoomCount) {
        if (roomCount == 1) {
            return RoomType.EndRoom;
        } else {
            int r = GlobalRandom.get().nextInt(roomCount + 1);
            if (r < itemRoomCount + 1) {
                return RoomType.ItemRoom;
            } else {
                return RoomType.MonsterRoom;
            }
        }
    }

    private static void addNeighborRooms(ArrayList<Integer> rooms, ArrayList<Integer> visitedRooms, int roomX, int roomY, int roomCountX, int roomCountY) {
        visitedRooms.add(roomX + roomY * roomCountX);
        if (roomX > 0 && !visitedRooms.contains((roomX - 1) + roomY * roomCountX)) {
            rooms.add((roomX - 1) + roomY * roomCountX);
        }
        if (roomY > 0 && !visitedRooms.contains(roomX + (roomY - 1) * roomCountX)) {
            rooms.add(roomX + (roomY - 1) * roomCountX);
        }
        if (roomX < roomCountX - 1 && !visitedRooms.contains((roomX + 1) + roomY * roomCountX)) {
            rooms.add((roomX + 1) + roomY * roomCountX);
        }
        if (roomY < roomCountY - 1 && !visitedRooms.contains(roomX + (roomY + 1) * roomCountX)) {
            rooms.add(roomX + (roomY + 1) * roomCountX);
        }
    }

    private static void generateRoom(RoomType type, int xOffset, int yOffset, int roomWidth, int roomHeight) {
        generateRoomFrame(xOffset, yOffset, roomWidth, roomHeight);
        switch (type) {
            case MonsterRoom:
                generateRoomEnemies(GlobalRandom.get().nextInt(3) + 2, xOffset + 1, yOffset + 1, roomWidth - 2, roomHeight - 2);
                break;
            case ItemRoom:
                generateRoomItems(GlobalRandom.get().nextInt(2) + 1, xOffset + 1, yOffset + 1, roomWidth - 2, roomHeight - 2);
                break;
            case StartRoom:
                dungeon.setPlayerSpawn(xOffset + 1 + GlobalRandom.get().nextInt(roomWidth - 2), yOffset + 1 + GlobalRandom.get().nextInt(roomHeight - 2));
                break;
            case EndRoom:
                tiles[(xOffset + GlobalRandom.get().nextInt(roomWidth - 2) + 1) + (yOffset + GlobalRandom.get().nextInt(roomHeight - 2) + 1) * width] = TileType.Ladder;
                break;
            default:
                break;
        }
    }

    private static void generateRoomFrame(int xOffset, int yOffset, int roomWidth, int roomHeight) {
        for (int y = 0; y < roomHeight; y++) {
            for (int x = 0; x < roomWidth; x++) {
                TileType tile;
                if (x == 0 || x == roomWidth - 1 || y == 0 || y == roomHeight - 1) {
                    tile = TileType.Wall;
                } else {
                    tile = TileType.Floor;
                }
                tiles[(x + xOffset) + (y + yOffset) * width] = tile;
            }
        }
    }

    private static void generateDoors(int xOffset, int yOffset, int roomWidth, int roomHeight) {
        for (int y = 0; y < roomHeight; y++) {
            for (int x = 0; x < roomWidth; x++) {
                int globalX = (x + xOffset);
                int globalY = (y + yOffset);
                if (tiles[globalX + globalY * width] == TileType.Corridor) {
                    dungeon.spawnEntity(new Door(), globalX, globalY);
                }
            }
        }
    }

    private static void generateRoomEnemies(int count, int xOffset, int yOffset, int roomWidth, int roomHeight) {
        for (int i = 0; i < count; i++) {
            int x = xOffset + GlobalRandom.get().nextInt(roomWidth);
            int y = yOffset + GlobalRandom.get().nextInt(roomHeight);
            int enemyType = GlobalRandom.get().nextInt(1);
            if (enemyType == 0) {
                dungeon.spawnEntity(new Rat(), x, y);
            }
            // TODO: More enemies.
        }
    }

    private static void generateRoomItems(int count, int xOffset, int yOffset, int roomWidth, int roomHeight) {
        for (int i = 0; i < count; i++) {
            int x = xOffset + GlobalRandom.get().nextInt(roomWidth);
            int y = yOffset + GlobalRandom.get().nextInt(roomHeight);
            dungeon.spawnEntity(new Item(level), x, y);
        }
    }
}