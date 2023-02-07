import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Deck
{
	private ArrayList<Card> deck;
	private TreeSet<Card> market;
	private ArrayList<Integer> ids;
	//mincost|resouces|# power

	public Deck() throws IOException {
		deck = new ArrayList<>();
		market = new TreeSet<>();
		ids = new ArrayList<>();
		Scanner in = new Scanner(getClass().getResourceAsStream("powerPlants.txt"));
		StringTokenizer line = null;
		while (in.hasNext()) {
			line = new StringTokenizer(in.nextLine());
			int m = Integer.parseInt(line.nextToken());
			String r = line.nextToken();
			int p = Integer.parseInt(line.nextToken());
			ids.add(m);
			Card c = new Card(m, r, p);
			if (m < 11 || m == 13 || m == 99)
				market.add(c);
			else
				deck.add(c);
		}
		Collections.shuffle(deck);
		for (int i = 0; i < 4; i++)
			deck.remove((int)(Math.random()*deck.size()));
		Collections.shuffle(deck);
//		Collections.shuffle(deck, Runner.RNG);
//		for (int i = 0; i < 4; i++)
//			deck.remove(Runner.RNG.nextInt(deck.size()));
//		Collections.shuffle(deck, Runner.RNG);
		deck.add(market.pollLast());
		deck.add(0, market.pollLast());
//		System.out.println(deck.toString().replaceAll(",", "\n"));
	}
	
	public void shuffle() {
		Collections.shuffle(deck);
//		Collections.shuffle(deck, Runner.RNG);
//		System.out.println(deck.toString().replaceAll(",", "\n"));
	}
	public void moveHighest() {
		deck.add(market.pollLast());
		market.add(deck.remove(0));
	}
	public void clearLowest() {
		try {
			market.pollFirst();
			market.add(deck.remove(0));
		} catch (Exception e) {}
	}
	public void buy(int id) {
		try {
			market.remove(new Card(id));
			market.add(deck.remove(0));
		} catch (Exception e) {}
	}
	public TreeSet<Card> getMarket(){
		return market;
	}
	public Card getRndCard() {
		return deck.get((int)(Math.random()*deck.size()));
	}
	public Card getRndCard(int i) {
		return deck.get(i);
	}
	public ArrayList<Integer> getIDs() {
		return ids;
	}
}
