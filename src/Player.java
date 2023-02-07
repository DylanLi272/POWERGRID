import java.util.ArrayList;
import java.util.TreeSet;

public class Player implements Comparable<Player>
{
	private TreeSet<Card> hand;
	private ArrayList<City> cities;
	private int powered, money, color;
	private boolean gameover;
	
	public Player(int c) {
		hand = new TreeSet<>();
		cities = new ArrayList<>();
		powered = 0;
		money = 50;
		gameover = false;
		color = c;
	}
	
	public void endGame() {
		gameover = true;
	}
	public void addCard(Card c) {
		hand.add(c);
	}
	public void removeCard(Card c) {
		hand.remove(c);
	}
	public void addCity(City c) {
		cities.add(c);
	}
	public void setPowered(int num) {
		powered = num;
	}
	public void addMoney(int num) {
		money += num;
	}
	public void setMoney(int num) {
		money = num;
	}
	public TreeSet<Card> getHand(){
		return hand;
	}
	public int getColor() {
		return color;
	}
	public int getPowered() {
		return powered;
	}
	public ArrayList<City> getCities() {
		return cities;
	}
	public int getMoney() {
		return money;
	}
	
	public boolean equals(Object o) {
		Player p = (Player) o;
		return color == p.color;
	}
	public int compareTo(Player p) {
		if (gameover)
			if (powered == p.powered)
				if (money == p.money)
					return p.cities.size() - cities.size();
				else return p.money - money;
			else return p.powered - powered;
		
		if (cities.size() == p.cities.size())
			return p.hand.last().compareTo(hand.last());
		return p.cities.size() - cities.size();
	}
	public String toString() {
		return ""+color;
	}
}
