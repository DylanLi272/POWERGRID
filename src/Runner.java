import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Runner extends JFrame implements MouseListener, ActionListener
{
	private static final long serialVersionUID = 3213634360766245752L;
	
	public static final Random RNG = new Random(112334556778990L);
	
	private GUI graphics;
	private JPanel loading;
	private HashMap<String, BufferedImage> imageMap;
	private boolean[] select;
	private boolean begin;
	private Timer t;
	
	public static void main(String[] args) throws IOException {
		@SuppressWarnings("unused")
		Runner runner = new Runner();
	}
	
	public Runner() throws IOException {
		super("Power Grid");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setSize(1800, 1000);
		setSize(3600, 2000);
		setLocationRelativeTo(null);
		setVisible(true);
		
		begin = false;
		select = new boolean[6]; //0-B, 1-G, 2-Y, 3-R, 4-W, 5-P
		imageMap = new HashMap<>();
		imageMap.put("board", ImageIO.read(getClass().getResourceAsStream("board.png")));
		imageMap.put("background", ImageIO.read(getClass().getResourceAsStream("background.png")));
		imageMap.put("check", ImageIO.read(getClass().getResourceAsStream("check.png")));

		t = new Timer(500, this);
		loading = new JPanel() {
			private static final long serialVersionUID = -1625257375612294645L;

			public void paintComponent(Graphics gr) {
				super.paintComponent(gr);
				BufferedImage scr = new BufferedImage(3600, 2000, BufferedImage.TYPE_INT_ARGB);
				Graphics g = scr.createGraphics();
				
				if (begin) {
					//put loading screen
					g.drawImage(imageMap.get("background"), 0, 0, 3600, 2000, null);
					g.setColor(Color.black);
					g.setFont(new Font("OCR A Extended", Font.BOLD, 300));
					g.drawString("Loading...", 1000, 1100);
					
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
					
					t.start();
					return;
				}
				
				g.drawImage(imageMap.get("background"), 2700, 0, 910, 2000, null);
				g.drawImage(imageMap.get("board"), 0, 0, 2766, 2000, null);
				
				g.setColor(Color.black);
				g.setFont(new Font("OCR A Extended", Font.BOLD, 100));
				g.drawString("Choose areas", 2780, 150);
				
				for (int i = 0; i < 6; i++) {
					if (select[i])
						g.drawImage(imageMap.get("check"), 2820, 250+i*250, 140, 110, null);
					switch (i) {
					case 0: g.setColor(new Color(145, 165, 155, 235)); break;
					case 1: g.setColor(new Color(130, 130, 50, 235)); break;
					case 2: g.setColor(new Color(230, 200, 100, 235)); break;
					case 3: g.setColor(new Color(180, 100, 90, 235)); break;
					case 4: g.setColor(new Color(150, 100, 60, 235)); break;
					case 5: g.setColor(new Color(170, 140, 150, 235)); break;
					}
					g.fillRoundRect(3000, 200+i*250, 530, 200, 50, 50);
				}
				
				g.setColor(new Color(200, 200, 200, 235));
				g.fillRoundRect(3230, 1750, 300, 150, 50, 50);
				int count = 0;
				for (int i = 0; i < 6; i++)
					if (select[i])
						count++;
				if (count == 4) {
					g.setColor(new Color(0, 200, 0, 235));
					g.fillRoundRect(2850, 1750, 300, 150, 50, 50);
				}
				
				g.setColor(Color.black);
				g.setFont(new Font("OCR A Extended", Font.BOLD, 70));
				g.drawString("Clear", 3260, 1850);
				if (count == 4)
					g.drawString("Start", 2880, 1850);
				
				
				
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
			}
		};
//		loading.setPreferredSize(new Dimension(1800, 1000));
		loading.setPreferredSize(new Dimension(3600, 2000));
		setContentPane(loading);
		loading.addMouseListener(this);
		
		
		pack();
	}
	
	public void start() throws IOException {
		graphics = new GUI(select);
		add(graphics);
		setContentPane(graphics);
		pack();
	}
	
	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		int width = loading.getWidth();
		int height = loading.getHeight();
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
		
		int count = 0;
		for (int i = 0; i < 6; i++)
			if (select[i])
				count++;
		
		if (3230 < x && x < 3530 && 1750 < y && y < 1900)
			Arrays.fill(select, false);
		else if (count == 4 && 2850 < x && x < 3150 && 1750 < y && y < 1900)
			begin = true;
		else if (x < 3000 || x > 3530 || y < 200 || y > 1700 || (y-200) % 250 > 200 || count == 4)
			;
		else {	//0-B, 1-G, 2-Y, 3-R, 4-W, 5-P
			switch ((y-200)/250) {
			case 0: select[0] = select[0] || count == 0 || select[5] || select[3]; break;
			case 1: select[1] = select[1] || count == 0 || select[3] || select[2] || select[4]; break;
			case 2: select[2] = select[2] || count == 0 || select[5] || select[3] || select[1] || select[4]; break;
			case 3: select[3] = select[3] || count == 0 || select[5] || select[0] || select[2] || select[1]; break;
			case 4: select[4] = select[4] || count == 0 || select[2] || select[1]; break;
			case 5: select[5] = select[5] || count == 0 || select[0] || select[3] || select[2]; break;
			}
		}
		
		repaint();
	}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	
	public void actionPerformed(ActionEvent a) {
		try {
			t.stop();
			start();
		} catch (Exception ex) {
			System.out.println("Error: "+ex.toString()); 
			ex.printStackTrace();
		}
	}
}
