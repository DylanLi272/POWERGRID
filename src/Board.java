import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Board
{
	private Deck deck;
	private Market resources;
	private ArrayList<Player> players;
	private TreeSet<City> cities;
	private City[] city;
	private HashMap<String, City> nameCity;
	private int step, phase;
	private boolean[] zones;
	private boolean gameover;

	private int[][] distances;
	private int[][] next;
//	private int[][] prev;
	
	private Queue<Player> payingQueue;
	private boolean[] selected;
	
	private ArrayList<Point> locations;
	private Queue<Player> buildingQueue;
	
	//bidding variables
	private Queue<Player> biddingQueue;
	private int highestBid, curBid;
	private Card bidCard;
	private Player highestPlayer;
	private boolean[] bidStatus; //true - already picked/passed, false - still can bid
	private boolean noBids;
	
	//resource buying variables
	private Queue<Player> resourceQueue;
	private int[][] tempResources;
	
	public Board(boolean[] z) throws IOException {
		deck = new Deck();
		resources = new Market();
		cities = new TreeSet<>();
		gameover = false;
		zones = z;
		step = 1;
		phase = 2;
		
		//0-R, 1-B, 2-G, 3-P
		players = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			players.add(new Player(i));
		Collections.shuffle(players);
//		Collections.shuffle(players, Runner.RNG);
		
		
		next = new int[42][];
		//load cities
		//0-B, 1-G, 2-Y, 3-R, 4-W, 5-P
		HashMap<Integer, TreeSet<City>> map = new HashMap<>();
		city = new City[42];
		nameCity = new HashMap<>();
		Scanner ci = new Scanner(getClass().getResourceAsStream("cities.txt"));
		while (ci.hasNext()) {
			TreeSet<City> set = new TreeSet<>();
			for (int i = 0; i < 7; i++) {
				String[] arr = ci.nextLine().split("\\|");
				City c = new City(arr[0], arr[1].charAt(0), Integer.parseInt(arr[2]), arr[3]);
				set.add(c);
				next[c.getId()] = c.getNeighbor();
				city[c.getId()] = c;
				nameCity.put(c.getName(), c);
			}
//			System.out.println(set);
			switch (set.first().getColor()) {
			case 'B': map.put(0, set); break;
			case 'G': map.put(1, set); break;
			case 'Y': map.put(2, set); break;
			case 'R': map.put(3, set); break;
			case 'W': map.put(4, set); break;
			case 'P': map.put(5, set); break;
			}
			cities.addAll(set);
		}
		
		Scanner dis = new Scanner(getClass().getResourceAsStream("distance.txt"));
//		Iterator<City> iter = cities.iterator();
		distances = new int[42][42];
		for (int r = 0; r < 42; r++) {
			StringTokenizer line = new StringTokenizer(dis.nextLine());
			for (int c = 0; c < next[r].length; c++)
				distances[r][next[r][c]] = Integer.parseInt(line.nextToken());
		}
//		prev = new int[42][42];
		for (int i = 0; i < 42; i++)
			if (zones[i/7])
				distances[i] = shortestPath(i);
		
//		System.out.println(Arrays.deepToString(distances).replaceAll("],", "\n"));
		
		for (int r = 0; r < 42; r++)
			for (int c = 0; c < 42; c++)
				if (!zones[r/7] || !zones[c/7])
					distances[r][c] = 9999;
		
//		System.out.println(Arrays.deepToString(distances).replaceAll("],", "\n"));
		
//		for (int i = 0; i < 10; i++) {
//			int a = (int) (Math.random()*42), b = (int) (Math.random()*42);
//			if (i == 0)
//				a = b;
//			System.out.print(idToCity.get(a).getName()+" - "+idToCity.get(b).getName()+": "+distances[a][b]);
//			System.out.print(" -> ");
//			String s = idToCity.get(b).getName()+", ";
//			while (prev[a][b] != a) {
//				if (prev[a][b] < 0)
//					break;
//				s += idToCity.get(prev[a][b]).getName() + ", ";
//				b = prev[a][b];
//			}
//			s += idToCity.get(a).getName();
//			System.out.println(s);
//		}
		
		Scanner loc = new Scanner(getClass().getResourceAsStream("locations.txt"));
		locations = new ArrayList<>();
		for (int i = 0; i < 42; i++) {
			String[] arr = loc.nextLine().split("\\|");
			StringTokenizer line = new StringTokenizer(arr[2]);
			locations.add(new Point(Integer.parseInt(line.nextToken()), Integer.parseInt(line.nextToken()), 
					Integer.parseInt(line.nextToken()), Integer.parseInt(line.nextToken()), Integer.parseInt(arr[1]), arr[0]));
		}

		biddingQueue = new LinkedList<>();
		bidStatus = new boolean[4];
		for (int i = 0; i < 4; i++)
			biddingQueue.add(players.get(i));
		highestPlayer = null;
		highestBid = 0;
		noBids = true;
		
		
		resourceQueue = new LinkedList<>();
		setResourceQueue();
		tempResources = new int[2][6];
		
		buildingQueue = new LinkedList<>();
		setBuildingQueue();
		
		payingQueue = new LinkedList<>();
		selected = new boolean[3];
		setPayingQueue();
		
	}

	private int[] shortestPath(int start) {
		PriorityQueue<Node> queue = new PriorityQueue<>();
		HashSet<Integer> vis = new HashSet<>();
		int[] dist = new int[42];
//		int[] pre = new int[42];
		Arrays.fill(dist, 9999);
		dist[start] = 0;
//		pre[start] = -1;
		queue.offer(new Node(start, 0));
		
		while (!queue.isEmpty()) {
			Node node = queue.poll();
			if (!zones[node.id/7])
				vis.add(node.id);
			if (vis.contains(node.id))
				continue;
			int[] list = next[node.id];
			for (int i = 0; i < list.length; i++) {
				int dis = dist[node.id] + distances[node.id][list[i]];
				if (dis < dist[list[i]]) {
					dist[list[i]] = dis;
//					pre[list[i]] = node.id;
					Node n = new Node(list[i], dis);
					if (!queue.contains(n))
						queue.offer(n);
				}
			}
			vis.add(node.id);
		}
//		prev[start] = pre;
		return dist;
	}
	private class Node implements Comparable<Node> {
		int id, dis;
		
		public Node(int id, int dis) {
			this.id = id;
			this.dis = dis;
		}
		
		public boolean equals(Object o) {
			Node n = (Node) o;
			return id == n.id;
		}
		public int compareTo(Node n) {
			if (dis == n.dis)
				return id - n.id;
			return dis - n.dis;
		}
		public String toString() {
			return id+"-"+dis;
		}
	}
	public void endGame() {
		gameover = true;
		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			int powered  = 0;
			Iterator<Card> hand = player.getHand().iterator();
			for (int j = 0; j < player.getHand().size(); j++) {
				Card c = hand.next();
				if (c.getCost().length == 1)
					powered += c.getPower();
				else if (c.getCost().length/2 <= c.getStorage().size())
					powered += c.getPower();
			}
			player.setPowered(Math.min(powered, player.getCities().size()));
			player.endGame();
		}
		Collections.sort(players);
	}
	public boolean getGameover() {
		return gameover;
	}
	public void setNextRound() {
//		if (step == 3) {
//			for (int i = 0; i < players.size(); i++)
//				if (players.get(i).getCities().size() >= 17) {
//					endGame();
//					return;
//				}
//		}
		resources.refill(step);
		Collections.sort(players);
		setBiddingQueue();
		Arrays.fill(bidStatus, false);
		highestPlayer = null;
		highestBid = 0;
		phase = 2;
		noBids = true;
		if (step == 3)
//			try {
				deck.clearLowest();
//			} catch (Exception e) {}
		else
			deck.moveHighest();
		int max = 0;
		for (int i = 0; i < players.size(); i++)
			max = Math.max(max, players.get(i).getCities().size());
		while (!deck.getMarket().isEmpty() && deck.getMarket().first().getMinCost() <= max)
			deck.clearLowest();
		if (deck.getMarket().contains(new Card(99))) {
			step = 3;
			deck.getMarket().pollFirst();
			deck.getMarket().pollLast();
			shuffle();
		}
		
	}
	public void shuffle() {
		deck.shuffle();
	}
	public void setStep(int s) {
		step = s;
	}
	public void setPhase(int p) {
		phase = p;
	}
	public int getStep() {
		return step;
	}
	public int getPhase() {
		return phase;
	}
	
	public ArrayList<Point> getLocations() {
		return locations;
	}
	public City getCity(int i) {
		return city[i];
	}
	public City findCity(int x, int y) {
		for (int i = 0; i < locations.size(); i++)
			if (locations.get(i).x1 < x && x < locations.get(i).x2 && 
					locations.get(i).y1 < y && y < locations.get(i).y2)
				return city[locations.get(i).id];
		return null;
	}
	public int getCost(City city, Player player) {
		if (city == null)
			return 9999;
		ArrayList<Player> players = city.getPlayers();
		if (players.size() >= step)
			return 9999;
		int cost = 9999;
		ArrayList<City> cities = player.getCities();
		if (cities.isEmpty() && zones[city.getId()/7])
			cost = 0;
		for (int i = 0; i < cities.size(); i++)
			cost = Math.min(cost, distances[city.getId()][cities.get(i).getId()]);
		cost += players.size()*5 + 10;
		return cost;
	}
	public void setBuildingQueue() {
		buildingQueue.clear();
		for (int i = players.size()-1; i >= 0; i--)
			buildingQueue.add(players.get(i));
	}
	public Queue<Player> getBuildingQueue() {
		return buildingQueue;
	}
	
	public void setPayingQueue() {
		for (int i = 0; i < players.size(); i++)
			if (players.get(i).getCities().size() >= 17) {
				endGame();
				return;
			}
		payingQueue.clear();
		for (int i = 0; i < players.size(); i++)
			players.get(i).setPowered(0);
		for (int i = 0; i < players.size(); i++)
			payingQueue.add(players.get(i));
		Arrays.fill(selected, false);
	}
	public Queue<Player> getPayingQueue(){
		return payingQueue;
	}
	public boolean[] getSelected() {
		return selected;
	}
	public int getPayment(int i) {
		switch (i) {
		case 0: return 10;
		case 1: return 22;
		case 2: return 33;
		case 3: return 44;
		case 4: return 54;
		case 5: return 64;
		case 6: return 73;
		case 7: return 82;
		case 8: return 90;
		case 9: return 98;
		case 10: return 105;
		case 11: return 112;
		case 12: return 118;
		case 13: return 124;
		case 14: return 129;
		case 15: return 134;
		case 16: return 138;
		case 17: return 142;
		case 18: return 145;
		case 19: return 148;
		case 20: return 150;
		default: return 150;
		}
	}
	public void addExcess(ArrayList<Character> list) {
		int c = 0, o = 0, t = 0, n = 0;
		for (int i = 0; i < list.size(); i++)
			switch (list.get(i)) {
			case 'C': c++; break;
			case 'O': o++; break;
			case 'T': t++; break;
			case 'N': n++; break;
			}
		resources.addExccess(c, o, t, n);
	}
	
	//methods to access deck
	public Deck getDeck() {
		return deck;
	}
	public TreeSet<Card> getMarket() {
		return deck.getMarket();
	}
	public void buy(int id) {
		deck.buy(id);
	}
	public ArrayList<Player> getPlayers(){
		return players;
	}
	public Player getPlayer(int color) {
		for (int i = 0; i < 4; i++)
			if (players.get(i).getColor() == color)
				return players.get(i);
		return null;
	}
	
	//methods to access resource market
	public int getResource(char r) {
		return resources.getResource(r);
	}
	public int cost(int c, int o, int t, int n, int p) {
		return resources.cost(c, o, t, n, p);
	}
	public void buyResource(int c, int o, int t, int n) {
		resources.buy('C', c);
		resources.buy('O', o);
		resources.buy('T', t);
		resources.buy('N', n);
	}
	
	public void setResourceQueue() {
		resourceQueue.clear();
		for (int i = players.size()-1; i >= 0; i--)
			resourceQueue.add(players.get(i));
	}
	public void buy(char r, int n) {
		resources.buy(r, n);
	}
	public Queue<Player> getResourceQueue(){
		return resourceQueue;
	}
	public void setTempResources() {
		Arrays.fill(tempResources[0], 0);
		Arrays.fill(tempResources[1], 0);
	}
	public int[][] getTempResources() {
		return tempResources;
	}
	public int getTempR(char ch) {
		int count = 0, r = 0, c = 0;
		switch (ch) {
		case 'C': r = 0; c = 0; break;
		case 'O': r = 1; c = 0; break;
		case 'T': r = 0; c = 1; break;
		case 'N': r = 1; c = 1; break;
		}
		for (int i = 0; i < 6; i += 2)
			count += tempResources[r][i+c];
		return count;
	}
	public void clearLowest() {
		deck.clearLowest();
	}
	
	//methods to access players
	public void reorder() {
		Collections.sort(players);
	}
	public void setBiddingQueue() {
		if (deck.getMarket().isEmpty()) {
//			if (deck.getMarket().contains(new Card(99))) {
//				step = 3;
//				deck.getMarket().pollFirst();
//				deck.getMarket().pollLast();
//				shuffle();
//			}
			phase = 3;
			Arrays.fill(bidStatus, false);
			setResourceQueue();
			return;
		}
		highestBid = 0;
		bidCard = null;
		highestPlayer = null;
		biddingQueue.clear();
		Card c = deck.getMarket().first();
		for (int i = 0; i < 4; i++)
			if (!bidStatus[players.get(i).getColor()] && players.get(i).getMoney() > c.getMinCost()-1)
				biddingQueue.add(players.get(i));
		if (biddingQueue.isEmpty()) {
			if (deck.getMarket().contains(new Card(99))) {
				step = 3;
				deck.getMarket().pollFirst();
				deck.getMarket().pollLast();
				shuffle();
			}
			phase = 3;
			Arrays.fill(bidStatus, false);
			setResourceQueue();
		}
	}
	public void setHighestPlayer(Player p) {
		highestPlayer = p;
	}
	public void setHighestBid(int b) {
		highestBid = b;
	}
	public void setBidCard(Card c) {
		bidCard = c;
	}
	public void setCurBid(int b) {
		curBid = b;
	}
	public Queue<Player> getBiddingQueue() {
		return biddingQueue;
	}
	public boolean[] getBidStatus() {
		return bidStatus;
	}
	public Player getHighestPlayer() {
		return highestPlayer;
	}
	public int getHighestBid() {
		return highestBid;
	}
	public Card getBidCard() {
		return bidCard;
	}
	public int getCurBid() {
		return curBid;
	}
	public void setNoBids(boolean b) {
		noBids = b;
	}
	public boolean getNoBids() {
		return noBids;
	}
}
class Point implements Comparable<Point> {
	String name;
	int x1, x2, y1, y2, id;
	public Point(int a, int b, int c, int d, int e, String f) {
		x1 = a; x2 = b;
		y1 = c; y2 = d;
		id = e;
		name = f;
	}
	
	public boolean equals(Object o) {
		Point p = (Point) o;
		return id == p.id;
	}
	public int compareTo(Point p) {
		if (x1 == p.x1)
			return y1 - p.y1;
		return x1 - p.x1;
	}
	public String toString() {
		return String.format("%s|%d|%d %d %d %d", name, id, x1, x2, y1, y2);
	}
}
