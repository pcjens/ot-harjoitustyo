package otm.roguesque.game.dungeon;

import java.util.ArrayList;
import java.util.Random;
import otm.roguesque.game.entities.AI;
import otm.roguesque.game.entities.Door;
import otm.roguesque.game.entities.Entity;
import otm.roguesque.game.entities.Item;
import otm.roguesque.game.entities.Player;
import otm.roguesque.game.entities.Rat;

public class Dungeon {

    private static final int MAX_ROOM_WIDTH = 14;
    private static final int MAX_ROOM_HEIGHT = 12;
    private static final int MIN_ROOM_WIDTH = 6;
    private static final int MIN_ROOM_HEIGHT = 5;
    private static final int MIN_ROOM_MARGIN = 2;
    private static final int MAX_ROOMS = 10;

    private final boolean[] solid;
    private final TileType[] tiles;
    private final int seed;
    private final int width;
    private final int height;
    private final int level;

    private ArrayList<Entity> entities;
    private Player player;
    private int playerSpawnX;
    private int playerSpawnY;

    public Dungeon(int level, int seed) {
        this.width = (int) (MAX_ROOM_WIDTH * (Math.sqrt(MAX_ROOMS) + 1));
        this.height = (int) (MAX_ROOM_HEIGHT * (Math.sqrt(MAX_ROOMS) + 1));
        this.level = level;
        this.entities = new ArrayList();
        this.tiles = new TileType[width * height];
        this.solid = new boolean[width * height];
        this.seed = seed;
        generateDungeon();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (tiles[x + y * width] == TileType.Wall) {
                    this.solid[x + y * width] = true;
                }
            }
        }
        for (Entity e : entities) {
            if (e instanceof Door) {
                this.solid[e.getX() + e.getY() * width] = true;
            }
        }
    }

    public final void spawnEntity(Entity e, int x, int y) {
        this.entities.add(e);
        e.setDungeon(this);
        e.setPosition(x, y);
        if (e instanceof Player) {
            this.player = (Player) e;
        }
    }

    public int getPlayerSpawnX() {
        return playerSpawnX;
    }

    public int getPlayerSpawnY() {
        return playerSpawnY;
    }

    public Player getPlayer() {
        return player;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSeed() {
        return seed;
    }

    public TileType[] getTiles() {
        return tiles;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public int getLevel() {
        return level;
    }

    public boolean canFinish() {
        return getTileAt(player.getX(), player.getY()) == TileType.Stairs;
    }

    public boolean solid(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height || tiles[x + y * width] == null) {
            return true;
        }
        return solid[x + y * width];
    }

    public Entity getEntityAt(int x, int y) {
        for (Entity e : entities) {
            if (e.getX() == x && e.getY() == y && !e.isDead()) {
                return e;
            }
        }
        return null;
    }

    public TileType getTileAt(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return null;
        }
        return tiles[x + y * width];
    }

    public void processRound() {
        for (Entity e : entities) {
            if (e instanceof AI) {
                ((AI) e).processRound(this);
            }
        }
    }

    public void cleanupDeadEntities() {
        entities.forEach((entity) -> {
            Entity lastEntityInteractedWith = entity.getLastEntityInteractedWith();
            if (lastEntityInteractedWith != null && lastEntityInteractedWith.isDead()) {
                entity.resetLastEntityInteractedWith();
            }
        });
        entities.removeIf((entity) -> {
            boolean dead = entity.isDead();
            if (dead && entity instanceof Door) {
                solid[entity.getX() + entity.getY() * width] = false;
            }
            return dead;
        });
    }

    // This is just a testing utility
    public void movePlayerNTimes(int x, int y) {
        for (int i = 0; i < Math.abs(x); i++) {
            player.move((int) Math.signum(x), 0);
            processRound();
            cleanupDeadEntities();
            player.recalculateLineOfSight(false);
        }
        for (int i = 0; i < Math.abs(y); i++) {
            player.move(0, (int) Math.signum(y));
            processRound();
            cleanupDeadEntities();
            player.recalculateLineOfSight(false);
        }
    }

    // Dungeon generation functionality
    // Sorry about the fragmentation of the functions; CheckStyle doesn't like long functions.
    private void generateDungeon() {
        Random rand = new Random(seed);

        int roomCountX = width / MAX_ROOM_WIDTH;
        int roomCountY = height / MAX_ROOM_HEIGHT;
        int minimumRooms = (int) Math.ceil(MAX_ROOMS / 3.0);
        int roomCount = rand.nextInt(MAX_ROOMS - minimumRooms + 1) + minimumRooms;
        RoomType[] rooms = generateRoomTypes(rand, roomCount, roomCountX, roomCountY);
        generateRooms(rand, rooms, roomCountX, roomCountY);
    }

    private void generateRooms(Random rand, RoomType[] rooms, int roomCountX, int roomCountY) {
        for (int roomY = 0; roomY < roomCountY; roomY++) {
            for (int roomX = 0; roomX < roomCountX; roomX++) {
                RoomType type = rooms[roomX + roomY * roomCountX];
                if (type != null) {
                    int roomWidth = rand.nextInt(MAX_ROOM_WIDTH - (MIN_ROOM_WIDTH + MIN_ROOM_MARGIN)) + MIN_ROOM_WIDTH;
                    int roomHeight = rand.nextInt(MAX_ROOM_HEIGHT - (MIN_ROOM_HEIGHT + MIN_ROOM_MARGIN)) + MIN_ROOM_HEIGHT;
                    int x = roomX * MAX_ROOM_WIDTH + rand.nextInt(MAX_ROOM_WIDTH - roomWidth - 2) + 2;
                    int y = roomY * MAX_ROOM_HEIGHT + rand.nextInt(MAX_ROOM_HEIGHT - roomHeight - 2) + 2;
                    generateRoom(rand, type, x, y, roomWidth, roomHeight);
                    generateCorridors(rand, rooms, roomX, roomY, roomCountX, roomCountY,
                            x, y, roomWidth, roomHeight, roomX * MAX_ROOM_WIDTH, roomY * MAX_ROOM_HEIGHT,
                            (roomX + 1) * MAX_ROOM_WIDTH, (roomY + 1) * MAX_ROOM_HEIGHT);
                    generateDoors(x, y, roomWidth, roomHeight);
                }
            }
        }
    }

    private void generateCorridors(Random rand, RoomType[] rooms, int roomIndexX, int roomIndexY,
            int roomCountX, int roomCountY, int roomX, int roomY, int roomWidth, int roomHeight,
            int startX, int startY, int endX, int endY) {
        if (roomIndexX > 0 && rooms[(roomIndexX - 1) + roomIndexY * roomCountX] != null) {
            int otherY = findCorridorY(startX, startY, endY);
            int y = roomY + 1 + rand.nextInt(roomHeight - 2);
            generateHorizontalCorridor(y, startX, roomX);
            generateVerticalCorridor(startX, y, otherY); // the corridor connecting the corridors
        }
        if (roomIndexY > 0 && rooms[roomIndexX + (roomIndexY - 1) * roomCountX] != null) {
            int otherX = findCorridorX(startY, startX, endX);
            int x = roomX + 1 + rand.nextInt(roomWidth - 2);
            generateVerticalCorridor(x, startY, roomY);
            generateHorizontalCorridor(startY, x, otherX); // the corridor connecting the corridors
        }
        if (roomIndexX < roomCountX - 1 && rooms[(roomIndexX + 1) + roomIndexY * roomCountX] != null) {
            generateHorizontalCorridor(roomY + 1 + rand.nextInt(roomHeight - 2), roomX + roomWidth - 1, endX);
        }
        if (roomIndexY < roomCountY - 1 && rooms[roomIndexX + (roomIndexY + 1) * roomCountX] != null) {
            generateVerticalCorridor(roomX + 1 + rand.nextInt(roomWidth - 2), roomY + roomHeight - 1, endY);
        }
    }

    private int findCorridorY(int x, int startY, int endY) {
        for (int y = startY; y <= endY; y++) {
            if (tiles[x + y * width] == TileType.Corridor) {
                return y;
            }
        }
        return -1;
    }

    private int findCorridorX(int y, int startX, int endX) {
        for (int x = startX; x <= endX; x++) {
            if (tiles[x + y * width] == TileType.Corridor) {
                return x;
            }
        }
        return -1;
    }

    private void generateHorizontalCorridor(int y, int startX, int endX) {
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

    private void generateVerticalCorridor(int x, int startY, int endY) {
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

    private RoomType[] generateRoomTypes(Random rand, int roomCount, int roomCountX, int roomCountY) {
        RoomType[] rooms = new RoomType[roomCountX * roomCountY];
        int itemRoomCount = 1 + rand.nextInt(2);
        int roomX = rand.nextInt(roomCountX);
        int roomY = rand.nextInt(roomCountY);
        RoomType room = RoomType.StartRoom;
        ArrayList<Integer> visitedRooms = new ArrayList();
        ArrayList<Integer> possibleRooms = new ArrayList();
        while (roomCount > 0) {
            rooms[roomX + roomY * roomCountX] = room;
            addNeighborRooms(possibleRooms, visitedRooms, roomX, roomY, roomCountX, roomCountY);
            roomCount--;

            int roomIdx = possibleRooms.remove(rand.nextInt(possibleRooms.size()));
            roomX = roomIdx % roomCountX;
            roomY = roomIdx / roomCountX;
            room = rollRoom(rand, roomCount, itemRoomCount);
            itemRoomCount -= room == RoomType.ItemRoom ? 1 : 0;
        }
        return rooms;
    }

    private RoomType rollRoom(Random rand, int roomCount, int itemRoomCount) {
        if (roomCount == 1) {
            return RoomType.EndRoom;
        } else {
            int r = rand.nextInt(roomCount + 1);
            if (r < itemRoomCount + 1) {
                return RoomType.ItemRoom;
            } else {
                return RoomType.MonsterRoom;
            }
        }
    }

    private void addNeighborRooms(ArrayList<Integer> rooms, ArrayList<Integer> visitedRooms, int roomX, int roomY, int roomCountX, int roomCountY) {
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

    private void generateRoom(Random rand, RoomType type, int xOffset, int yOffset, int roomWidth, int roomHeight) {
        generateRoomFrame(xOffset, yOffset, roomWidth, roomHeight);
        switch (type) {
            case MonsterRoom:
                generateRoomEnemies(rand, rand.nextInt(3) + 2, xOffset + 1, yOffset + 1, roomWidth - 2, roomHeight - 2);
                break;
            case ItemRoom:
                generateRoomItems(rand, rand.nextInt(2) + 1, xOffset + 1, yOffset + 1, roomWidth - 2, roomHeight - 2);
                break;
            case StartRoom:
                playerSpawnX = xOffset + 1 + rand.nextInt(roomWidth - 2);
                playerSpawnY = yOffset + 1 + rand.nextInt(roomHeight - 2);
                break;
            case EndRoom:
                tiles[(xOffset + rand.nextInt(roomWidth - 2) + 1) + (yOffset + rand.nextInt(roomHeight - 2) + 1) * width] = TileType.Stairs;
                break;
            default:
                break;
        }
    }

    private void generateRoomFrame(int xOffset, int yOffset, int roomWidth, int roomHeight) {
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

    private void generateDoors(int xOffset, int yOffset, int roomWidth, int roomHeight) {
        for (int y = 0; y < roomHeight; y++) {
            for (int x = 0; x < roomWidth; x++) {
                int globalX = (x + xOffset);
                int globalY = (y + yOffset);
                if (tiles[globalX + globalY * width] == TileType.Corridor) {
                    spawnEntity(new Door(), globalX, globalY);
                }
            }
        }
    }

    private void generateRoomEnemies(Random rand, int count, int xOffset, int yOffset, int roomWidth, int roomHeight) {
        for (int i = 0; i < count; i++) {
            int x = xOffset + rand.nextInt(roomWidth);
            int y = yOffset + rand.nextInt(roomHeight);
            int enemyType = rand.nextInt(1);
            if (enemyType == 0) {
                spawnEntity(new Rat(rand.nextInt()), x, y);
            }
            // TODO: More enemies.
        }
    }

    private void generateRoomItems(Random rand, int count, int xOffset, int yOffset, int roomWidth, int roomHeight) {
        for (int i = 0; i < count; i++) {
            int x = xOffset + rand.nextInt(roomWidth);
            int y = yOffset + rand.nextInt(roomHeight);
            spawnEntity(new Item(level, rand.nextInt()), x, y);
        }
    }
}
