import java.util.HashMap;

public class Market
{
	private HashMap<Character, int[]> costs;
	//C - coal, O - oil, B - hybrid, N - nuclear, T - trash, E - empty
	private HashMap<Character, Integer> resources, excess;
	
	public Market() {
		costs = new HashMap<>();
		resources = new HashMap<>();
		excess = new HashMap<>();

		resources.put('C', 24);
		resources.put('O', 18);
		resources.put('T', 6);
		resources.put('N', 2);
		
		excess.put('C', 0);
		excess.put('O', 6);
		excess.put('T', 18);
		excess.put('N', 10);
		
		int[] cost = new int[25];
		for (int i = 0; i < 24; i++)
			cost[i+1] = 8-(i/3);
		costs.put('C', cost);
		costs.put('O', cost);
		costs.put('T', cost);
		
		cost = new int[13];
		for (int i = 5; i < 13; i++)
			cost[i] = 13-i;
		for (int i = 0; i < 4; i++)
			cost[i+1] = 16-2*i;
		costs.put('N', cost);
	}
	
	public void addExccess(int c, int o, int t, int n) {
		excess.put('C', excess.get('C') + c);
		excess.put('O', excess.get('O') + o);
		excess.put('T', excess.get('T') + t);
		excess.put('N', excess.get('N') + n);
	}
	public void refill(int step) {
		int c = 0, o = 0, t = 0, n = 0;
		switch (step) {
		case 1:
			c = Math.min(5, excess.get('C'));
			o = Math.min(3, excess.get('O'));
			t = Math.min(2, excess.get('T'));
			n = Math.min(1, excess.get('N')); break;
		case 2:
			c = Math.min(6, excess.get('C'));
			o = Math.min(4, excess.get('O'));
			t = Math.min(3, excess.get('T'));
			n = Math.min(2, excess.get('N')); break;
		case 3:
			c = Math.min(4, excess.get('C'));
			o = Math.min(5, excess.get('O'));
			t = Math.min(4, excess.get('T'));
			n = Math.min(2, excess.get('N')); break;
		}
		excess.put('C', excess.get('C') - c);
		excess.put('O', excess.get('O') - o);
		excess.put('T', excess.get('T') - t);
		excess.put('N', excess.get('N') - n);
		resources.put('C', resources.get('C') + c);
		resources.put('O', resources.get('O') + o);
		resources.put('T', resources.get('T') + t);
		resources.put('N', resources.get('N') + n);
	}
	public void buy(char resource, int amount) {
		resources.put(resource, resources.get(resource) - amount);
	}
	public int getResource(char r) {
		return resources.get(r);
	}
	public int cost(int c, int o, int t, int n, int p) {
		if (resources.get('C') < c || resources.get('O') < o || resources.get('T') < t || resources.get('N') < n)
			return -1;
		int total = 0;
		for (int i = 0; i < c; i++)
			total += costs.get('C')[resources.get('C')-i];
		for (int i = 0; i < o; i++)
			total += costs.get('O')[resources.get('O')-i];
		for (int i = 0; i < t; i++)
			total += costs.get('T')[resources.get('T')-i];
		for (int i = 0; i < n; i++)
			total += costs.get('N')[resources.get('N')-i];
		if (total > p)
			return -1;
		return total;
	}
}
