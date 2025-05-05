package models;
import controllers.MenuController;
import models.Enums.Tile;

import java.util.*;

public class GameMap {
    private int size;
    private Tile[][] grid;
    public GameMap(int size, boolean random) {
        this.size = size;
        grid = new Tile[size][size];
        if(random) generateRandomMap();
        else fillPlain();
    }
    private void generateRandomMap() {
        Random rand = new Random();
        fillPlain();
        for (Tile t : new Tile[]{Tile.LAKE, Tile.GREENHOUSE, Tile.COTTAGE, Tile.QUARRY}){
            placeRandom(t, rand);
        }
    }
    private void placeRandom(Tile t, Random rand) {
        int x, y;
        do{
            x = rand.nextInt(size);
            y = rand.nextInt(size);
        } while (grid[y][x] != Tile.PLAIN);
        grid[y][x] = t;
    }
    public Tile getTile(int x, int y) {
        if(x < 0 || y < 0 || x >= size || y >= size){
            return null;
        }
        return grid[y][x];
    }

    public int getSize() {return size;}

    public void setTile(int x, int y, Tile tile) {
        if(x < 0 || y < 0 || x >= size || y >= size){
            return ;
        }
         grid[y][x] = tile;
    }

    private void fillPlain() {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                grid[y][x] = Tile.PLAIN;
            }
        }
    }
    public static List<int[]> findPath(GameMap map, int startX, int startY, int endX, int endY) {
        // پیاده‌سازی الگوریتم BFS
        Queue<int[]> queue = new LinkedList<>();
        Map<String, int[]> parent = new HashMap<>();
        boolean[][] visited = new boolean[map.getSize()][map.getSize()];

        queue.add(new int[]{startX, startY});
        visited[startY][startX] = true;

        while(!queue.isEmpty()) {
            int[] current = queue.poll();
            if(current[0] == endX && current[1] == endY) {
                return reconstructPath(parent, current);
            }

            for(int[] neighbor : map.getNeighbors(current[0], current[1])) {
                String key = neighbor[0] + "," + neighbor[1];
                if(!visited[neighbor[1]][neighbor[0]]) {
                    visited[neighbor[1]][neighbor[0]] = true;
                    parent.put(key, current);
                    queue.add(neighbor);
                }
            }
        }
        return Collections.emptyList(); // هیچ مسیری یافت نشد
    }

    private static List<int[]> reconstructPath(Map<String, int[]> parent, int[] end) {
        List<int[]> path = new ArrayList<>();
        int[] current = end;
        while(current != null) {
            path.add(current);
            current = parent.get(current[0] + "," + current[1]);
        }
        Collections.reverse(path);
        return path;
    }

    public static int calculateEnergy(List<int[]> path) {
        return (int) Math.ceil(path.size() / 20.0);
    }

    public static void printMapLegend() {
        System.out.println("\nMap Symbols Legend:");
        for(Tile tile : Tile.values()) {
            System.out.printf("%-2s : %-15s (%s)%n",
                    tile.getSymbol(),
                    tile.name(),
                    tile.getDescription());
        }
        System.out.println("\nExample Map Key:");
        System.out.println("T . L C Q");
        System.out.println("Tree, Plain, Lake, Cottage, Quarry\n");
    }

    public boolean isTilePassable(int x, int y) {
        Tile tile = getTile(x, y);
        return tile != null &&
                tile != Tile.LAKE &&
                tile != Tile.STONE;
    }

    // 2. یافتن همسایه‌های مجاز
    public List<int[]> getNeighbors(int x, int y) {
        List<int[]> neighbors = new ArrayList<>();
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1},
                {-1,-1}, {-1,1}, {1,-1}, {1,1}};

        for(int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if(isTilePassable(nx, ny)) {
                neighbors.add(new int[]{nx, ny});
            }
        }
        return neighbors;
    }

    // 3. چاپ بخشی از نقشه
    public void printMapArea(int centerX, int centerY, int size) {
        int half = size / 2;
        int startX = Math.max(0, centerX - half);
        int startY = Math.max(0, centerY - half);
        int endX = Math.min(size-1, centerX + half);
        int endY = Math.min(size-1, centerY + half);

        for(int y = startY; y <= endY; y++) {
            for(int x = startX; x <= endX; x++) {
                Tile tile = getTile(x, y);
                System.out.print(tile.getSymbol() + " ");
            }
            System.out.println();
        }
    }

    public static void handleWalkCommand(String command, MenuController controller) {
        try {
            String[] parts = command.split(" ");
            String[] coords = parts[2].split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            GameSession session = controller.getCurrentSession();
            GameMap map = session.getMap();


            // بررسی وجود خانه مقصد
            if(!map.isTilePassable(x, y)) {
                System.out.println("Destination is blocked!");
                return;
            }

            // یافتن مسیر
            List<int[]> path = findPath(
                    map,
                    session.getPlayerX(),
                    session.getPlayerY(),
                    x,
                    y
            );

            if(path.isEmpty()) {
                System.out.println("No path found!");
                return;
            }

            //  انرژی
            int energyCost = calculateEnergy(path);
            System.out.println("Energy needed: " + energyCost + ". Confirm? (Y/N)");

            //  تایید کاربر
            String input = scanner.nextLine();
            if(input.equalsIgnoreCase("Y")) {
                if(session.getEnergy() >= energyCost) {
                    session.setPlayerPosition(x, y);
                    session.reduceEnergy(energyCost);
                    System.out.println("Moved successfully!");
                } else {
                    System.out.println("Insufficient energy! You fainted.");
                   // player.faint();
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid command format! Usage: walk -l <x,y>");
        }
    }

    public static void handlePrintMap(String command, MenuController controller) {
        GameSession session = controller.getCurrentSession();
        try {
            String[] parts = command.split(" ");
            String[] coords = parts[2].split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            int size = Integer.parseInt(parts[4]);

            session.getMap().printMapArea(x, y, size);
        } catch (Exception e) {
            System.out.println("Invalid command format! Usage: print map -l <x,y> -s <size>");
        }
    }
}