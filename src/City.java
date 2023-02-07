import java.util.ArrayList;
import java.util.StringTokenizer;

public class City implements Comparable<City>
{
	private String name;
	private char color;
	private int id;
	private ArrayList<Player> players;
	//purple = P, blue = B, red = R, yellow = Y, green = G, brown = W
	private int[] neighbor;
	
	public City(String n, char c, int d, String neigh) {
		players = new ArrayList<>();
		name = n;
		color = c;
		id = d;
		StringTokenizer s = new StringTokenizer(neigh);
		neighbor = new int[s.countTokens()];
		for (int i = 0; i < neighbor.length; i++)
			neighbor[i] = Integer.parseInt(s.nextToken());
	}
	
	public void addPlayer(Player p) {
		players.add(p);
	}
	public String getName() {
		return name;
	}
	public char getColor() {
		return color;
	}
	public int getId() {
		return id;
	}
	public int[] getNeighbor(){
		return neighbor;
	}
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public int compareTo(City c) {
		return id - c.id;
	}
	public String toString() {
		return name + "|" + color + "|" + id + "|" + neighbor.toString();
	}
}
