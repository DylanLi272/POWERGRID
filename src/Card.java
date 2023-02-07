import java.util.ArrayList;

public class Card implements Comparable<Card>
{
	private int minCost, power; //minCost is basically id;
//	private ArrayList<Character> cost;//C - coal, O - oil, B - hybrid, N - nuclear, T - trash, E - empty
	private char[] cost;
	private ArrayList<Character> storage;
	
	public Card(int min) {
		minCost = min;
	}
	public Card (int min, String list, int p) {
		minCost = min;
		storage = new ArrayList<>();
		power = p;
		list += list;
		char[] arr = list.toCharArray();
		if (arr[0] == 'E')
			cost = new char[1];
		else
			cost = arr;
	}
	
	public int getMinCost() {
		return minCost;
	}
	public int getPower() {
		return power;
	}
	public ArrayList<Character> power() {
		ArrayList<Character> use = new ArrayList<>();
		for (int i = 0; i < cost.length/2; i++) {
			use.add(storage.remove((int)(Math.random()*storage.size())));
		}
		return use;
	}
	public boolean hasResources() {
		if (cost.length == 1)
			return true;
		if (cost.length/2 <= storage.size())
			return true;
		return false;
	}
	public ArrayList<Character> getStorage(){
		return storage;
	}
	public void addResource(char r, int n) {
		for (int i = 0; i < n; i++)
			storage.add(r);
	}
	public int getResource(char r) {
		int count = 0;
		for (int i = 0; i < storage.size(); i++)
			if (storage.get(i) == r)
				count++;
		return count;
	}
	public int getRC(char r) {
		int count = 0;
		for (int i = 0; i < cost.length; i++)
			if (cost[i] == r)
				count++;
			else if (cost[i] == 'B' && (r =='C' || r == 'O'))
				count++;
		return count;
	}
	public char[] getCost() {
		return cost;
	}
	
	public boolean equals(Object o) {
		Card c = (Card) o;
		return minCost == c.minCost;
	}
	public int compareTo(Card c) {
		return minCost - c.minCost;
	}
	public String toString() {
		return ""+minCost;
//		return minCost + " " + Arrays.toString(cost);
	}
}
