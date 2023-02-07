import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GUI extends JPanel implements MouseListener
{
	private static final long serialVersionUID = -1601653437309252684L;
	
	private HashMap<Integer, BufferedImage> cards;
	private HashMap<String, BufferedImage> images;
	private Board board;
	private int selectCard;
	private Player selectPlayer;
	private boolean cardSelected, bidding, playerSelected;
	private boolean[] playZones;
	
	private boolean firstRound, clickAvailable, replacing;
	
	private boolean citySelected;
	private City selectCity;
	
	private int a;

	public GUI(boolean[] zones) throws IOException {
		cards = new HashMap<>();
		images = new HashMap<>();
		board = new Board(zones);
		playZones = zones;
		cardSelected = false;
		selectCard = 0;
		firstRound = true;
		replacing = false;
		
		//reading the card images
		ArrayList<Integer> temp = board.getDeck().getIDs();
		for (int item : temp)
			cards.put(item, ImageIO.read(getClass().getResourceAsStream(item+".png")));
		
		Scanner in = new Scanner(getClass().getResourceAsStream("images.txt"));
		while (in.hasNext()) {
			StringTokenizer line = new StringTokenizer(in.nextLine());
			images.put(line.nextToken(), ImageIO.read(getClass().getResourceAsStream(line.nextToken()+".png")));
		}

//		setPreferredSize(new Dimension(1800, 1000));
		setPreferredSize(new Dimension(3600, 2000));
		addMouseListener(this);
	}
	public void paintComponent(Graphics gr) {
//		try {
		
		super.paintComponent(gr);
		BufferedImage scr = new BufferedImage(3600, 2000, BufferedImage.TYPE_INT_ARGB);
		Graphics g = scr.createGraphics();
		
		//side panel
		g.drawImage(images.get("background"), 2700, 0, 910, 2000, null);
		
		//white boxes near top
		g.setColor(new Color(255, 255, 255, 200));
		//top
		g.fillRoundRect(2800, 400, 750, 300, 50, 50);
		g.setColor(new Color(240, 240, 240));
		//for power plant market button
		g.fillRoundRect(2800, 1430, 750, 150, 50, 50);
		
		
		if (board.getGameover()) {
			g.setColor(Color.black);
			g.setFont(new Font("OCR A Extended", Font.BOLD, 120));
			g.drawString("GAME OVER", 2810, 250);
			g.setFont(new Font("OCR A Extended", Font.BOLD, 100));
			g.drawString("Back to", 2850, 530);
			g.drawString("Score", 2850, 620);
		}
		else {
			g.setColor(Color.black);
			g.setFont(new Font("OCR A Extended", Font.BOLD, 100));
			g.drawString("Step: "+board.getStep(), 2800, 120);
			g.drawString("Phase:", 2800, 250);
			switch (board.getPhase()) {
			case 2: g.drawString("Auction", 2800, 340);
					g.drawString("Back to", 2850, 530);
					if (replacing) g.drawString("Replacing", 2850, 620);
					else g.drawString("Auction", 2850, 620);
					break;
			case 3: g.drawString("Resources", 2800, 340);
					g.drawString("Back to", 2850, 530);
					g.drawString("Resources", 2850, 620); break;
			case 4: g.drawString("Building", 2800, 340); break;
			case 5: g.drawString("Bureaucracy", 2800, 340);
					g.drawString("Back to", 2850, 530);
					g.drawString("Picking", 2850, 620); break;
			}
		}
		
		
		g.drawImage(images.get("refill"), 2811, 1610, 350, 350, null);
		g.drawImage(images.get("bureaucracy"), 3206, 1610, 350, 350, null);
		g.setFont(new Font("OCR A Extended", Font.BOLD, 50));
		//0-B, 1-G, 2-Y, 3-R, 4-W, 5-P
		boolean tempB = false;
		String tempSt = "Play Zones:  ";
		String tempS = "";
		for (int i = 0; i < playZones.length; i++) {
			if (playZones[i])
				if (tempB) {
					switch (i) {
					case 0: tempS += "Blue, "; break;
					case 1: tempS += "Green, "; break;
					case 2: tempS += "Yellow, "; break;
					case 3: tempS += "Red, "; break;
					case 4: tempS += "Brown, "; break;
					case 5: tempS += "Purple, "; break;
					}
				}
				else {
					switch (i) {
					case 0: tempSt += "Blue, "; break;
					case 1: tempSt += "Green, "; break;
					case 2: tempSt += "Yellow, "; break;
					case 3: tempSt += "Red, "; break;
					case 4: tempSt += "Brown, "; break;
					case 5: tempSt += "Purple, "; break;
					}
					tempB = true;
				}
		}
		g.drawString(tempSt, 2820, 1495);
		g.drawString(tempS.substring(0, tempS.length()-2), 2820, 1555);
		
		g.setColor(new Color(200, 0, 0, 200));
		g.fillRoundRect(2840, 750, 300, 300, 50, 50);
		g.setColor(new Color(0, 0, 140, 200));
		g.fillRoundRect(2840, 1100, 300, 300, 50, 50);
		g.setColor(new Color(70, 120, 0, 200));
		g.fillRoundRect(3210, 750, 300, 300, 50, 50);
		g.setColor(new Color(200, 0, 200, 200));
		g.fillRoundRect(3210, 1100, 300, 300, 50, 50);
		
		
		//board image and game pieces
		g.drawImage(images.get("board"), 0, 0, 2766, 2000, null);
		
		ArrayList<Player> players = board.getPlayers();
		for (int i = 0; i < players.size(); i++)
			g.drawImage(images.get(""+players.get(i).getColor()), 148+i*73, 90, 50, 50, null);
		
		
		ArrayList<Point> locations = board.getLocations();
		for (int i = 0; i < locations.size(); i++) {
			Point point = locations.get(i);
			ArrayList<Player> list = board.getCity(point.id).getPlayers();
			for (int j = 0; j < list.size(); j++) {
				switch (j) {
				case 0: g.drawImage(images.get(""+list.get(j).getColor()), point.x1+40, point.y1, 40, 40, null); break;
				case 1: g.drawImage(images.get(""+list.get(j).getColor()), point.x1, point.y1+50, 40, 40, null); break;
				case 2: g.drawImage(images.get(""+list.get(j).getColor()), point.x1+70, point.y1+50, 40, 40, null); break;
				}
			}
		}
		
		
		//market resources
		a = board.getResource('C');
		g.setColor(new Color(80, 30, 0));
		for (int i = 0; i < a; i++)
			g.fillRect(1703-i*65-i/3*20, 1785, 35, 35);
		a = board.getResource('O');
		g.setColor(Color.black);
		for (int i = 0; i < a; i++)
			g.fillRect(1667-i*48-i/3*71, 1842, 25, 35);
		a = board.getResource('T');
		g.setColor(Color.yellow);
		for (int i = 0; i < a; i++)
			g.fillRect(1703-i*65-i/3*20, 1900, 35, 35);
		a = board.getResource('N');
		g.setColor(Color.red);
		for (int i = 0; i < a && i < 4; i++)
			g.fillRect(1878-i%2*90, 1892-i/2*95, 40, 40);
		for (int i = 0; i < a-4 && i < 8; i++)
			g.fillRect(1715-i*215, 1843, 25, 35);
		
		
		//gameover
		if (board.getGameover() && !clickAvailable && !playerSelected) {
			g.setColor(new Color(255, 255, 255, 235));
			g.fillRoundRect(300, 300, 3000, 1400, 50, 50);
			ArrayList<Player> list = board.getPlayers();
			g.setColor(Color.black);
			g.setFont(new Font("OCR A Extended", Font.BOLD, 150));
			for (int i = 0; i < list.size(); i++) {
				String word = "";
				switch (i) {
				case 0: word += "1st   "; break;
				case 1: word += "2nd   "; break;
				case 2: word += "3rd   "; break;
				case 3: word += "4th   "; break;
				}
				//0-R, 1-B, 2-G, 3-P
				switch (list.get(i).getColor()) {
				case 0: word += "Red"; break;
				case 1: word += "Blue"; break;
				case 2: word += "Green"; break;
				case 3: word += "Purple"; break;
				}
				g.drawString(word, 1200, 650+i*250);
			}
			
			g.dispose();
			int width = getWidth();
			int height = getHeight();
			if (width >= height * 9 / 5.0) {
				gr.setColor(Color.BLACK);
				gr.fillRect(0, 0, width, height);
				gr.drawImage(scr, (int)(width / 2.0 - height * 9 / 5.0 / 2.0), 0, (int)(height * 9 / 5.0), height, null);
			}
			else {
				gr.setColor(Color.BLACK);
				gr.fillRect(0, 0, width, height);
				gr.drawImage(scr, 0, (int)(height / 2.0 - width * 5 / 9.0 / 2.0), width, (int)(width * 5 / 9.0), null);
			}
			return;
		}
		
		//paying
		if (board.getPhase() == 5 && !clickAvailable && !playerSelected) {
			Player player = board.getPayingQueue().peek();
			g.setColor(new Color(255, 255, 255, 200));
			g.fillRoundRect(300, 300, 3000, 1400, 50, 50);
			//0-R, 1-B, 2-G, 3-P
			switch (player.getColor()) {
			case 0: g.setColor(new Color(200, 0, 0, 180)); break;
			case 1: g.setColor(new Color(0, 0, 140, 180)); break;
			case 2: g.setColor(new Color(70, 120, 0, 180)); break;
			case 3: g.setColor(new Color(200, 0, 200, 180)); break;
			}
			g.fillRoundRect(300, 300, 3000, 1400, 50, 50);
			
			Iterator<Card> handIter = player.getHand().iterator();
			boolean[] arr = board.getSelected();
			for (int i = 0; handIter.hasNext(); i++) {
				if (arr[i])
					g.drawImage(images.get("check"), 800+i*500, 500, 140, 110, null);
				Card c = handIter.next();
				g.drawImage(cards.get(c.getMinCost()), 650+i*500, 650, 450, 450, null); //650
				g.setColor(new Color(255, 255, 255, 235));
				g.fillRoundRect(650+i*500, 1150, 450, 200, 50, 50);
				g.setColor(new Color(110, 60, 0));
				g.fillRect(680+i*500, 1180, 55, 55);
				g.setColor(Color.black);
				g.fillRect(680+i*500, 1265, 55, 55);
				g.setColor(new Color(240, 230, 50));
				g.fillRect(870+i*500, 1180, 55, 55);
				g.setColor(new Color(230, 0, 0));
				g.fillRect(870+i*500, 1265, 55, 55);
				g.setColor(Color.black);
				g.setFont(new Font("OCR A Extended", Font.BOLD, 50));
				g.drawString("x"+c.getResource('C'), 750+i*500, 1230);
				g.drawString("x"+c.getResource('O'), 750+i*500, 1315);
				g.drawString("x"+c.getResource('T'), 940+i*500, 1230);
				g.drawString("x"+c.getResource('N'), 940+i*500, 1315);
			}
			
			g.setColor(new Color(50, 200, 0));
			g.fillRoundRect(2300, 1150, 550, 200, 50, 50);
			g.setColor(Color.black);
			g.setFont(new Font("OCR A Extended", Font.BOLD, 100));
			g.drawString("Powered: "+player.getPowered(), 2230, 800); //2130
			g.drawString("Receive: $"+board.getPayment(player.getPowered()), 2230, 1000);
			g.setFont(new Font("OCR A Extended", Font.BOLD, 80));
			g.drawString("Get Money", 2340, 1280);
		}
		
		
		//building
		if (board.getPhase() == 4) {
			switch (board.getBuildingQueue().peek().getColor()) {
			case 0: g.setColor(new Color(200, 0, 0, 180)); break;
			case 1: g.setColor(new Color(0, 0, 140, 180)); break;
			case 2: g.setColor(new Color(70, 120, 0, 180)); break;
			case 3: g.setColor(new Color(200, 0, 200, 180)); break;
			}
			g.fillRoundRect(2800, 400, 750, 300, 50, 50);
			g.setColor(new Color(200, 200, 200));
			g.fillRoundRect(3320, 570, 170, 80, 30, 30);
			g.setColor(Color.black);
			g.setFont(new Font("OCR A Extended", Font.BOLD, 50));
			g.drawString("Done", 3340, 625);
		}
		if (board.getPhase() == 4 && citySelected) {
			g.setColor(Color.black);
			g.setFont(new Font("OCR A Extended", Font.BOLD, 70));
			g.drawString(selectCity.getName(), 2840, 520);
			g.drawString(""+board.getCost(selectCity, board.getBuildingQueue().peek()), 2840, 630);
			g.setColor(new Color(50, 200, 0));
			g.fillRoundRect(3110, 570, 170, 80, 30, 30);
			g.setColor(Color.black);
			g.setFont(new Font("OCR A Extended", Font.BOLD, 50));
			g.drawString("Buy", 3145, 625);
		}
		
		
		//buying resources
		if (board.getPhase() == 3 && !clickAvailable && !playerSelected) {
			firstRound = false;
			if (board.getNoBids()) {
				board.clearLowest();
				int max = 0;
				ArrayList<Player> list = board.getPlayers();
				for (int i = 0; i < list.size(); i++)
					max = Math.max(max, list.get(i).getCities().size());
				while (!board.getMarket().isEmpty() && board.getMarket().first().getMinCost() <= max)
					board.clearLowest();
				board.setNoBids(false);
			}
			
			Player player = board.getResourceQueue().peek();
			g.setColor(new Color(255, 255, 255, 200));
			g.fillRoundRect(300, 300, 3000, 1400, 50, 50);
			//0-R, 1-B, 2-G, 3-P
			switch (player.getColor()) {
			case 0: g.setColor(new Color(200, 0, 0, 180)); break;
			case 1: g.setColor(new Color(0, 0, 140, 180)); break;
			case 2: g.setColor(new Color(70, 120, 0, 180)); break;
			case 3: g.setColor(new Color(200, 0, 200, 180)); break;
			}
			g.fillRoundRect(300, 300, 3000, 1400, 50, 50);
			
			TreeSet<Card> hand = player.getHand();
			Iterator<Card> handIter = hand.iterator();
			
			int[][] tr = board.getTempResources();
			for (int i = 0; i < hand.size(); i++) {
				Card c = handIter.next();
				g.drawImage(cards.get(c.getMinCost()), 650+i*600, 550, 450, 450, null);
				g.setColor(new Color(255, 255, 255, 235));
				g.fillRoundRect(600+i*600, 1050, 550, 400, 50, 50);
				g.setColor(new Color(110, 60, 0));
				g.fillRect(650+i*600, 1125, 60, 60);
				g.setColor(Color.black);
				g.fillRect(650+i*600, 1305, 60, 60);
				g.setColor(new Color(240, 230, 50));
				g.fillRect(900+i*600, 1125, 60, 60);
				g.setColor(new Color(230, 0, 0));
				g.fillRect(900+i*600, 1305, 60, 60);
				g.setColor(Color.black);
				g.setFont(new Font("OCR A Extended", Font.BOLD, 50));
				g.drawString("x"+c.getResource('C'), 725+i*600, 1180);
				g.drawString("x"+c.getResource('O'), 725+i*600, 1360); //original -> +85, 1230 and 1315
				g.drawString("x"+c.getResource('T'), 975+i*600, 1180);
				g.drawString("x"+c.getResource('N'), 975+i*600, 1360);
				g.setColor(new Color(50, 200, 0));
				for (int q = 0; q < 2; q++)
					for (int w = 0; w < 2; w++)
						if (tr[q][i*2+w] != 0)
							g.drawString("+"+tr[q][i*2+w], 800+i*600+w*250, 1180+q*180);
				for (int q = 0; q < 2; q++)
					for (int w = 0; w < 2; w++) {
						g.drawImage(images.get("up"), 740+i*600+w*250, 1080+q*180, 70, 70, null);
						g.drawImage(images.get("down"), 740+i*600+w*250, 1180+q*180, 70, 70, null);
					}
			}
			
			g.setColor(new Color(255, 255, 255, 235));
			g.fillRoundRect(2450, 510, 800, 730, 50, 50);
			g.setColor(new Color(110, 60, 0));
			g.fillRect(2485, 550, 150, 150);
			g.setColor(Color.black);
			g.fillRect(2485, 850, 150, 150);
			g.setColor(new Color(240, 230, 50));
			g.fillRect(2865, 550, 150, 150);
			g.setColor(new Color(230, 0, 0));
			g.fillRect(2865, 850, 150, 150);
			g.setColor(Color.black);
			g.setFont(new Font("OCR A Extended", Font.BOLD, 100));
			g.drawString("+"+board.getTempR('C'), 2645, 670);
			g.drawString("+"+board.getTempR('O'), 2645, 970);
			g.drawString("+"+board.getTempR('T'), 3025, 670);
			g.drawString("+"+board.getTempR('N'), 3025, 970);
			g.drawString("$"+board.cost(board.getTempR('C'), board.getTempR('O'), 
					board.getTempR('T'), board.getTempR('N'), player.getMoney()), 2740, 1160);
			
			g.setColor(new Color(50, 200, 0));
			g.fillRoundRect(2500, 1280, 290, 170, 50, 50);
			g.setColor(new Color(200, 200, 200));
			g.fillRoundRect(2850, 1280, 290, 170, 50, 50);
			g.setColor(Color.black);
			g.setFont(new Font("OCR A Extended", Font.BOLD, 80));
			g.drawString("Buy", 2560, 1390);
			g.drawString("Reset", 2865, 1390);
			
			
		}
		
		if (replacing && !clickAvailable && !playerSelected) {
			Player player = board.getBiddingQueue().peek();
			g.setColor(new Color(255, 255, 255, 200));
			g.fillRoundRect(300, 300, 3000, 1400, 50, 50);
			//0-R, 1-B, 2-G, 3-P
			switch (player.getColor()) {
			case 0: g.setColor(new Color(200, 0, 0, 180)); break;
			case 1: g.setColor(new Color(0, 0, 140, 180)); break;
			case 2: g.setColor(new Color(70, 120, 0, 180)); break;
			case 3: g.setColor(new Color(200, 0, 200, 180)); break;
			}
			g.fillRoundRect(300, 300, 3000, 1400, 50, 50);
			
			Iterator<Card> handIter = player.getHand().iterator();
			for (int i = 0; handIter.hasNext(); i++) {
				if (cardSelected && selectCard == i)
					g.drawImage(images.get("check"), 800+i*500, 500, 140, 110, null);
				Card c = handIter.next();
				g.drawImage(cards.get(c.getMinCost()), 650+i*500, 650, 450, 450, null); //650
				g.setColor(new Color(255, 255, 255, 235));
				g.fillRoundRect(650+i*500, 1150, 450, 200, 50, 50);
				g.setColor(new Color(110, 60, 0));
				g.fillRect(680+i*500, 1180, 55, 55);
				g.setColor(Color.black);
				g.fillRect(680+i*500, 1265, 55, 55);
				g.setColor(new Color(240, 230, 50));
				g.fillRect(870+i*500, 1180, 55, 55);
				g.setColor(new Color(230, 0, 0));
				g.fillRect(870+i*500, 1265, 55, 55);
				g.setColor(Color.black);
				g.setFont(new Font("OCR A Extended", Font.BOLD, 50));
				g.drawString("x"+c.getResource('C'), 750+i*500, 1230);
				g.drawString("x"+c.getResource('O'), 750+i*500, 1315);
				g.drawString("x"+c.getResource('T'), 940+i*500, 1230);
				g.drawString("x"+c.getResource('N'), 940+i*500, 1315);
			}
			
			g.drawImage(cards.get(board.getBidCard().getMinCost()), 2500, 650, 450, 450, null);
			g.setColor(new Color(50, 200, 0));
			g.fillRoundRect(2500, 1150, 450, 200, 50, 50);
			g.setColor(Color.black);
			g.setFont(new Font("OCR A Extended", Font.BOLD, 80));
			g.drawString("Select Power Plant to Replace", 1100, 1500);
			g.drawString("Replace", 2540, 1280);
		}
		
		//power plant market
		if (board.getPhase() == 2 && !clickAvailable && !playerSelected && !replacing) {
			g.setColor(new Color(255, 255, 255, 235));
			g.fillRoundRect(300, 300, 3000, 1400, 50, 50);
			g.setColor(Color.black);
			g.setFont(new Font("OCR A Extended", Font.BOLD, 80));
			g.drawString("Bidding Queue:", 450, 450);
			g.drawString("Highest Bidder:", 1550, 450);
			g.drawString("Current:", 2670, 450);
			Queue<Player> biddingQueue = board.getBiddingQueue();
			Iterator<Player> bidIter = biddingQueue.iterator();
			for (int i = 0; i < biddingQueue.size(); i++)
				g.drawImage(images.get(""+bidIter.next().getColor()), 1200+i*75, 400, 60, 60, null);
			if (board.getHighestPlayer() != null) {
				g.drawImage(images.get(""+board.getHighestPlayer().getColor()), 2350, 400, 60, 60, null);
				g.drawString("$"+board.getHighestBid(), 2430, 450);
			}
			g.drawImage(images.get(""+biddingQueue.peek().getColor()), 3100, 400, 60, 60, null);
			
			if (board.getStep() == 3) {
				Iterator<Card> iter = board.getMarket().iterator();
				int q = board.getMarket().size();
				for (int i = 0; i < 2; i++)
					for (int j = 0; j < 3 && q > 0; j++, q--)
						g.drawImage(cards.get(iter.next().getMinCost()), 450+j*550, 530+i*550, 500, 500, null);
				q = board.getMarket().size();
				if (cardSelected) {
					for (int i = 0; i < 2; i++)
						for (int j = 0; j < 3 && q > 0; j++, q--)
							if (i*10 + j == selectCard)
								g.drawImage(images.get("check"), 580+j*550, 650+i*550, 240, 185, null);
					g.setColor(new Color(0, 200, 0));
					g.fillRoundRect(2650, 1380, 280, 180, 50, 50);
					g.setColor(Color.black);
					g.setFont(new Font("OCR A Extended", Font.BOLD, 100));
					g.drawString("Bid", 2690, 1510);
					g.setFont(new Font("OCR A Extended", Font.BOLD, 120));
					g.drawString("$"+board.getCurBid(), 2810, 960);
				}
			}
			else {
				Iterator<Card> iter = board.getMarket().iterator();
				for (int i = 0; i < 2; i++)
					for (int j = 0; j < 4; j++)
						g.drawImage(cards.get(iter.next().getMinCost()), 450+j*550, 530+i*550, 500, 500, null);
				if (cardSelected) {
					for (int i = 0; i < 2; i++)
						for (int j = 0; j < 4; j++)
							if (i*10 + j == selectCard)
								g.drawImage(images.get("check"), 580+j*550, 650+i*550, 240, 185, null);
					g.setColor(new Color(0, 200, 0));
					g.fillRoundRect(2650, 1380, 280, 180, 50, 50);
					g.setColor(Color.black);
					g.setFont(new Font("OCR A Extended", Font.BOLD, 100));
					g.drawString("Bid", 2690, 1510);
					g.setFont(new Font("OCR A Extended", Font.BOLD, 120));
					g.drawString("$"+board.getCurBid(), 2810, 960);
				}
			}
			g.drawImage(images.get("up"), 2740, 520, 400, 400, null);
			g.drawImage(images.get("down"), 2740, 950, 400, 400, null);
			if (!firstRound || bidding) {
				g.setColor(new Color(200, 200, 200));
				g.fillRoundRect(2950, 1380, 280, 180, 50, 50);
				g.setColor(Color.black);
				g.setFont(new Font("OCR A Extended", Font.BOLD, 100));
				g.drawString("Pass", 2955, 1510);
			}
		}
		

		//player view
		if (playerSelected) {
			g.setColor(new Color(255, 255, 255, 200));
			g.fillRoundRect(500, 500, 2600, 1000, 50, 50);
			//0-R, 1-B, 2-G, 3-P
			switch (selectPlayer.getColor()) {
			case 0: g.setColor(new Color(200, 0, 0, 180)); break;
			case 1: g.setColor(new Color(0, 0, 140, 180)); break;
			case 2: g.setColor(new Color(70, 120, 0, 180)); break;
			case 3: g.setColor(new Color(200, 0, 200, 180)); break;
			}
			g.fillRoundRect(500, 500, 2600, 1000, 50, 50);
			
			Iterator<Card> handIter = selectPlayer.getHand().iterator();
			for (int i = 0; handIter.hasNext(); i++) {
				Card c = handIter.next();
				g.drawImage(cards.get(c.getMinCost()), 650+i*500, 650, 450, 450, null);
				g.setColor(new Color(255, 255, 255, 235));
				g.fillRoundRect(650+i*500, 1150, 450, 200, 50, 50);
				g.setColor(new Color(110, 60, 0));
				g.fillRect(680+i*500, 1180, 55, 55);
				g.setColor(Color.black);
				g.fillRect(680+i*500, 1265, 55, 55);
				g.setColor(new Color(240, 230, 50));
				g.fillRect(870+i*500, 1180, 55, 55);
				g.setColor(new Color(230, 0, 0));
				g.fillRect(870+i*500, 1265, 55, 55);
				g.setColor(Color.black);
				g.setFont(new Font("OCR A Extended", Font.BOLD, 50));
				g.drawString("x"+c.getResource('C'), 750+i*500, 1230);
				g.drawString("x"+c.getResource('O'), 750+i*500, 1315);
				g.drawString("x"+c.getResource('T'), 940+i*500, 1230);
				g.drawString("x"+c.getResource('N'), 940+i*500, 1315);
			}
			g.drawImage(images.get("money"), 2150, 650, 551, 270, null);
			
			g.setColor(Color.black);
			g.setFont(new Font("OCR A Extended", Font.BOLD, 100));
			g.drawString(":$"+selectPlayer.getMoney(), 2720, 840);
			g.drawString("Cities: "+selectPlayer.getCities().size(), 2180, 1100);
			if (board.getGameover())
				g.drawString("Powered: "+selectPlayer.getPowered(), 2180, 1300);
			
		}

		g.dispose();
		int width = getWidth();
		int height = getHeight();
		if (width >= height * 9 / 5.0) {
			gr.setColor(Color.BLACK);
			gr.fillRect(0, 0, width, height);
			gr.drawImage(scr, (int)(width / 2.0 - height * 9 / 5.0 / 2.0), 0, (int)(height * 9 / 5.0), height, null);
		}
		else {
			gr.setColor(Color.BLACK);
			gr.fillRect(0, 0, width, height);
			gr.drawImage(scr, 0, (int)(height / 2.0 - width * 5 / 9.0 / 2.0), width, (int)(width * 5 / 9.0), null);
		}
		
//		} catch (Exception e) {
//			JOptionPane.showMessageDialog(this, Arrays.toString(e.getStackTrace()).replaceAll(", ", "\n"), "ERROR: "+e.getMessage(), JOptionPane.ERROR_MESSAGE);
//		}
	}

	public void mouseReleased(MouseEvent e) {
//		try {
		
		int x = e.getX();
		int y = e.getY();
		int width = getWidth();
		int height = getHeight();
		if (width >= height * 9 / 5.0) {
			double xFactor = height*9/5.0/3600.0;
			double yFactor = height/2000.0;
			int start = (int)(width / 2.0 - height * 9 / 5.0 / 2.0);
			x -= start;
			x /= xFactor;
			y /= yFactor;
		}
		else {
			double xFactor = width/3600.0;
			double yFactor = width*5/9.0/2000.0;
			int start = (int)(height / 2.0 - width * 5 / 9.0 / 2.0);
			x /= xFactor;
			y -= start;
			y /= yFactor;
		}
//		System.out.println(x + " " + y);
		
//		if (x <10 && y < 10)
//			JOptionPane.showMessageDialog(this, "This is an error", "error", JOptionPane.ERROR_MESSAGE);
		
		//0-R, 1-B, 2-G, 3-P
		if (clickAvailable) {
			if (2840 < x && x < 3140 && 750 < y && y < 1050) {
				playerSelected = true;
				selectPlayer = board.getPlayer(0);
				clickAvailable = false;
			}
			else if (2840 < x && x < 3140 && 1100 < y && y < 1400) {
				playerSelected = true;
				selectPlayer = board.getPlayer(1);
				clickAvailable = false;
			}
			else if (3210 < x && x < 3510 && 750 < y && y < 1050) {
				playerSelected = true;
				selectPlayer = board.getPlayer(2);
				clickAvailable = false;
			}
			else if (3210 < x && x < 3510 && 1100 < y && y < 1400) {
				playerSelected = true;
				selectPlayer = board.getPlayer(3);
				clickAvailable = false;
			}
			else if (2800 < x && x < 3550 && 400 < y && y < 700) {
				playerSelected = false;
				clickAvailable = false;
			}
			else {
				citySelected = false;
			}
			if (board.getPhase() != 4 || playerSelected) {
				repaint();
				return;
			}
		}
		else {
			if (playerSelected) {
				if (x < 500 || x > 3100 || y < 500 || y > 1500){
					playerSelected = false;
					clickAvailable = true;
				}
			}
		}
		
		if (board.getGameover() && !clickAvailable && !playerSelected) {
			if (x < 300 || x > 3300 || y < 300 || y > 1700)
				clickAvailable = true;
			
			repaint();
			return;
		}
		

		if (board.getPhase() == 5 && !clickAvailable && !playerSelected) {
			if (x < 300 || x > 3300 || y < 300 || y > 1700) {
				clickAvailable = true;
				repaint();
				return;
			}
			Player player = board.getPayingQueue().peek();
			boolean[] selected = board.getSelected();
			if (2300 < x && x < 2850 && 1150 < y && y < 1350) {
				TreeSet<Card> hand = player.getHand();
				Iterator<Card> iter = hand.iterator();
				for (int i = 0; i < hand.size(); i++) {
					if (selected[i]) {
						Card c = iter.next();
						if (c.getCost().length == 1)
							continue;
						if (c.getCost().length/2 <= c.getStorage().size()) {
							board.addExcess(c.power());
						}
					}
					else
						iter.next();
				}
				player.addMoney(board.getPayment(player.getPowered()));
				board.getPayingQueue().poll();
				Arrays.fill(selected, false);
			}
			else {
				int count = 0;
				TreeSet<Card> hand = player.getHand();
				Iterator<Card> iter = hand.iterator();
				if (650 < x && x < 2100 && 650 < y && y < 1100 && (x-650)%500 < 450) {
					for (int i = 0; i < hand.size(); i++)
						if ((x-650)/500 == i) {
							if (iter.next().hasResources())
								selected[i] ^= true;
						}
						else
							iter.next();
				}
				iter = hand.iterator();
				for (int i = 0; i < hand.size(); i++) {
					if (selected[i]) {
						count += iter.next().getPower();
					}
					else
						iter.next();
				}
				player.setPowered(Math.min(player.getCities().size(), count));
			}
			if (board.getPayingQueue().isEmpty())
				board.setNextRound();
		}
		
		if (board.getPhase() == 4 && !playerSelected) {
			clickAvailable = true;
			Player player = board.getBuildingQueue().peek();
			int cost = board.getCost(selectCity, player);
			if (citySelected && 3110 < x && x < 3280 && 570 < y && y < 650 && cost <= player.getMoney()) {
				selectCity.addPlayer(player);
				player.addCity(selectCity);
				player.addMoney(-cost);
				citySelected = false;
			}
			else if (3320 < x && x < 3490 && 570 < y && y < 650) {
				board.getBuildingQueue().poll();
				citySelected = false;
			}
			else if (x < 2745) {
				selectCity = board.findCity(x, y);
				cost = board.getCost(selectCity, player);
				if (cost > player.getMoney() || selectCity.getPlayers().contains(player))
					citySelected = false;
				else
					citySelected = true;
			}
//			if (board.getStep() == 1) {
//				if (player.getCities().size() >= 7)
//					board.setStep(2);
////				ArrayList<Player> p = board.getPlayers();
////				for (int i = 0; i < p.size(); i++)
////					if (p.get(i).getCities().size() >= 7)
////						board.setStep(2);
//			}
			if (board.getBuildingQueue().isEmpty()) {
				if (board.getStep() == 1) {
					ArrayList<Player> p = board.getPlayers();
					for (int i = 0; i < p.size(); i++)
						if (p.get(i).getCities().size() >= 7)
							board.setStep(2);
				}
				//remove too small power plants
				if (!board.getMarket().isEmpty()) {
					int max = 0;
					ArrayList<Player> list = board.getPlayers();
					for (int i = 0; i < list.size(); i++)
						max = Math.max(max, list.get(i).getCities().size());
					while (board.getMarket().first().getMinCost() <= max)
						board.clearLowest();
				}
				board.setPhase(5);
				if (board.getMarket().contains(new Card(99))) {
					board.setStep(3);
					board.getMarket().pollFirst();
					board.getMarket().pollLast();
					board.shuffle();
				}
				board.setPayingQueue();
			}
		}
		
		
		if (board.getPhase() == 3 && !clickAvailable && !playerSelected) {
			if (x < 300 || x > 3300 || y < 300 || y > 1700) {
				clickAvailable = true;
				repaint();
				return;
			}
			int[][] tr = board.getTempResources();
			Player player = board.getResourceQueue().peek();
			Iterator<Card> iter = player.getHand().iterator();
			if ((x-750)/600 < player.getHand().size() && x > 750 && (y-1100)/180 < 2 && y > 1100) {
				if (((x-750)%600 < 50 || (250 < (x-750)%600 && (x-750)%600 < 300)) && 
						((y-1100)%180 < 30 || (100 < (y-1100)%180 && (y-1100)%180 < 130))) {
					int r = (y-1100)/180, c = (x-750)/600*2+(x-750)%600/250;
					Card card = null;
					for (int i = -1; i < (x-750)/600; i++)
						card = iter.next();
					char ch = 0;
					switch (r*10 + c) {
					case 0: ch = 'C'; break;
					case 1: ch = 'T'; break;
					case 2: ch = 'C'; break;
					case 3: ch = 'T'; break;
					case 4: ch = 'C'; break;
					case 5: ch = 'T'; break;
					case 10: ch = 'O'; break;
					case 11: ch = 'N'; break;
					case 12: ch = 'O'; break;
					case 13: ch = 'N'; break;
					case 14: ch = 'O'; break;
					case 15: ch = 'N'; break;
					}
					tr[r][c] += (y-1100)%180/100 == 0 ? 1 : -1;
					
					if (tr[r][c] > board.getResource(ch))
						tr[r][c]--;
					else if (tr[r][c] < 0)
						tr[r][c]++;
					else if (board.cost(board.getTempR('C'), board.getTempR('O'), 
							board.getTempR('T'), board.getTempR('N'), player.getMoney()) < 0)
						tr[r][c] += (y-1100)%180/100 == 0 ? -1 : 1;
					else if (card.getCost()[0] == 'B') {
						if (ch == 'C') {
							if (tr[r][c] + card.getResource('C') + tr[r+1][c] + card.getResource('O') > card.getRC(ch))
								tr[r][c] += (y-1100)%180/100 == 0 ? -1 : 1;
						}
						else if (ch == 'O') {
							if (tr[r-1][c] + card.getResource('C') + tr[r][c] + card.getResource('O') > card.getRC(ch))
								tr[r][c] += (y-1100)%180/100 == 0 ? -1 : 1;
						}
						else
							tr[r][c] += (y-1100)%180/100 == 0 ? -1 : 1;
					}
					else if (tr[r][c] + card.getResource(ch) > card.getRC(ch))
						tr[r][c] += (y-1100)%180/100 == 0 ? -1 : 1;
					
				}
			}
			else if (2850 < x && x < 3120 && 1280 < y && y < 1450)
				board.setTempResources();
			else if (2500 < x && x < 2770 && 1280 < y && y < 1450) {
				player.addMoney(-board.cost(board.getTempR('C'), board.getTempR('O'), 
						board.getTempR('T'), board.getTempR('N'), player.getMoney()));
				TreeSet<Card> hand = player.getHand();
				Iterator<Card> handIter = hand.iterator();
				for (int i = 0; i < hand.size(); i++) {
					Card card = handIter.next();
					card.addResource('C', tr[0][i*2]);
					card.addResource('O', tr[1][i*2]);
					card.addResource('T', tr[0][i*2+1]);
					card.addResource('N', tr[1][i*2+1]);
					
				}
				board.buy('C', board.getTempR('C'));
				board.buy('O', board.getTempR('O'));
				board.buy('T', board.getTempR('T'));
				board.buy('N', board.getTempR('N'));
				
				board.getResourceQueue().poll();
				board.setTempResources();
			}
			if (board.getResourceQueue().isEmpty()) {
				board.setPhase(4);
				board.setBuildingQueue();
			}
		}
		
		if (replacing && !clickAvailable && !playerSelected) {
			if (x < 300 || x > 3300 || y < 300 || y > 1700) {
				clickAvailable = true;
				repaint();
				return;
			}
			Player player = board.getBiddingQueue().peek();
			if (650 < x && x < 2100 && 650 < y && y < 1100 && (x-650)%500 < 450) {
				if (cardSelected && selectCard == (x-650)/500)
					cardSelected = false;
				else
					cardSelected = true;
				selectCard = (x-650)/500;
			}
			else if (cardSelected && 2500 < x && x < 2950 && 1150 < y && y < 1350) {
				replacing = false;
				Iterator<Card> iter = player.getHand().iterator();
				for (int i = 0; i < selectCard; i++)
					iter.next();
				Card c = iter.next();
				board.addExcess(c.getStorage());
				player.removeCard(c);
				player.addCard(board.getBidCard());
				board.getBidStatus()[player.getColor()] = true;
				board.buy(board.getBidCard().getMinCost());
				int max = 0;
				ArrayList<Player> list = board.getPlayers();
				for (int i = 0; i < list.size(); i++)
					max = Math.max(max, list.get(i).getCities().size());
				while (!board.getMarket().isEmpty() && board.getMarket().first().getMinCost() <= max)
					board.clearLowest();
				bidding = false;
				cardSelected = false;
				board.setHighestBid(0);
				board.setHighestPlayer(null);
				board.setBiddingQueue();
			}
		}
		
		if (board.getPhase() == 2 && !clickAvailable && !playerSelected && !replacing) {
			if (x < 300 || x > 3300 || y < 300 || y > 1700) {
				clickAvailable = true;
				repaint();
				return;
			}
			//bid and pass buttons
			Queue<Player> biddingQueue = board.getBiddingQueue();
			if (cardSelected && 2650 < x && x < 2930 && 1380 < y && y < 1560) {
				board.setNoBids(false);
				bidding = true;
				board.setHighestBid(board.getCurBid());
				board.setCurBid(board.getHighestBid()+1);
				board.setHighestPlayer(biddingQueue.peek());
				biddingQueue.add(biddingQueue.poll());
				while (biddingQueue.peek().getMoney() < board.getCurBid() && !biddingQueue.peek().equals(board.getHighestPlayer()))
					biddingQueue.poll();
				if (biddingQueue.size() == 1) {
					Player p = biddingQueue.peek();
					p.addMoney(-board.getHighestBid());
					if (p.getHand().size() == 3) {
						replacing = true;
						cardSelected = false;
						repaint();
						return;
					}
					if (!replacing) {
						p.addCard(board.getBidCard());
						board.getBidStatus()[p.getColor()] = true;
						board.buy(board.getBidCard().getMinCost());
						int max = 0;
						ArrayList<Player> list = board.getPlayers();
						for (int i = 0; i < list.size(); i++)
							max = Math.max(max, list.get(i).getCities().size());
						while (!board.getMarket().isEmpty() && board.getMarket().first().getMinCost() <= max)
							board.clearLowest();
						bidding = false;
						cardSelected = false;
						board.setHighestBid(0);
						board.setHighestPlayer(null);
						board.setBiddingQueue();
					}
				}
			}
			else if (2950 < x && x < 3230 && 1380 < y && y < 1560) {
//				if (bidding)
//					while (!biddingQueue.isEmpty() && biddingQueue.peek().getMoney() < board.getCurBid() && !biddingQueue.peek().equals(board.getHighestPlayer()))
//						biddingQueue.poll();
				boolean tempB = false;
				if (firstRound && !bidding) {
					tempB = true;
				}
				else if (!bidding) {
					board.getBidStatus()[biddingQueue.peek().getColor()] = true;
					board.setBiddingQueue();
				}
				else {
					biddingQueue.poll();
					if (bidding)
						while (!biddingQueue.isEmpty() && biddingQueue.peek().getMoney() < board.getCurBid() && !biddingQueue.peek().equals(board.getHighestPlayer()))
							biddingQueue.poll();
					if (biddingQueue.size() == 1 && !tempB) {
						Player p = biddingQueue.peek();
						p.addMoney(-board.getHighestBid());
						if (p.getHand().size() == 3) {
							replacing = true;
							cardSelected = false;
							repaint();
							return;
						}
						if (!replacing) {
							p.addCard(board.getBidCard());
							board.getBidStatus()[p.getColor()] = true;
							board.buy(board.getBidCard().getMinCost());
							int max = 0;
							ArrayList<Player> list = board.getPlayers();
							for (int i = 0; i < list.size(); i++)
								max = Math.max(max, list.get(i).getCities().size());
							while (!board.getMarket().isEmpty() && board.getMarket().first().getMinCost() <= max)
								board.clearLowest();
							bidding = false;
							cardSelected = false;
							board.setHighestBid(0);
							board.setHighestPlayer(null);
							board.setBiddingQueue();
						}
					}
				}
			}
			
			//up and down arrows
			if (x < 450 || x > 2650 || y < 530 || y > 1630 || (x-450) % 550 > 500 || (y-530) % 550 > 500) {
				if (2790 < x && x < 3090 && 605 < y && y < 795) {
					if (biddingQueue.peek().getMoney() > board.getCurBid())
						board.setCurBid(board.getCurBid()+1);
				}
				else if (2790 < x && x < 3090 && 1075 < y && y < 1260) {
					if (board.getCurBid()-1 > board.getHighestBid())
						board.setCurBid(board.getCurBid()-1);
				}
				else if (!bidding && x < 2650)
					cardSelected = false;
			}
			else if (!bidding) { //selecting card
				if (board.getStep() == 3) {
					if (cardSelected && selectCard == (x-450)/550 + (y-530)/550*10)
						cardSelected = false;
					else
						cardSelected = true;
					selectCard = (x-450)/550 + (y-530)/550*10;
					
					boolean here = false;
					Iterator<Card> cardIter = board.getMarket().iterator();
					int q = board.getMarket().size();
					for (int i = 0; i < 2; i++)
						for (int j = 0; j < 3 && q > 0; j++, q--)
							if (i*10 + j == selectCard) {
								board.setBidCard(cardIter.next());
								board.setCurBid(board.getBidCard().getMinCost());
								board.setHighestBid(board.getBidCard().getMinCost()-1);
								here = true;
							}
							else
								cardIter.next();
					if (!here)
						cardSelected = false;
				}
				else if (y < 1080) {
					if (cardSelected && selectCard == (x-450)/550 + (y-530)/550*10)
						cardSelected = false;
					else
						cardSelected = true;
					selectCard = (x-450)/550 + (y-530)/550*10;
					
					Iterator<Card> cardIter = board.getMarket().iterator();
					for (int i = 0; i < 2; i++)
						for (int j = 0; j < 4; j++)
							if (i*10 + j == selectCard) {
								board.setBidCard(cardIter.next());
								board.setCurBid(board.getBidCard().getMinCost());
								board.setHighestBid(board.getBidCard().getMinCost()-1);
							}
							else
								cardIter.next();
				}
				if (cardSelected && biddingQueue.peek().getMoney() < board.getBidCard().getMinCost())
					cardSelected = false;
			}
			if (board.getPhase() == 2) {
				if (biddingQueue.isEmpty())
					board.setBiddingQueue();
			}
		}
		
		repaint();

//		} catch (Exception ex) {
//			JOptionPane.showMessageDialog(this, Arrays.toString(ex.getStackTrace()).replaceAll(", ", "\n"), "ERROR: "+ex.getMessage(), JOptionPane.ERROR_MESSAGE);
//		}
	}
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
