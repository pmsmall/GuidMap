package com.mine.UI;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.common.UI.MyButton;
import com.common.UI.MyFrame;
import com.common.UI.MyPanel;
import com.common.UI.theme.ButtonTheme;
import com.common.UI.theme.FrameTheme;
import com.main.UI.theme.DefaultPanelTheme;
import com.mine.IO.NodeReader;
import com.mine.IO.NodeWriter;
import com.mine.structure.Edge;
import com.mine.structure.Node;

import com.mine.structure.Graphics.Minlength;

public class MainFrame {
	private MyFrame frame;
	private MyFrame controlor;
	private int defaultX;
	private int defaultY;
	private MyFileDialog loadDialog, saveDialog;
	private com.mine.structure.Graphics graphics;

	private class itermButtonTheme extends ButtonTheme {
		String s;
		int x;
		int y;
		Font font;

		public itermButtonTheme(String s, int x, int y) {
			this.s = s;
			this.x = x;
			this.y = y;
			font = new Font("楷体", Font.BOLD, 18);
		}

		/**
		 * 给mybutton设定背景图片
		 * 
		 * @param g
		 *            mybutton的画笔
		 */
		public void setBackgroudImage(Graphics g) {
			Graphics g0 = g.create();
			Color c0 = g0.getColor();
			Color c1 = new Color(0x141947);
			g0.setColor(c);
			switch (status) {
			case release:
				g0.setColor(new Color(0xb7d8fa));
				break;
			case press:
				g0.setColor(new Color(0x6981aa));
				break;
			case hover:
				g0.setColor(new Color(0x8bb7e5));
				break;
			default:
				break;
			}
			g0.fillRect(0, 0, mybutton.getWidth(), mybutton.getHeight());
			g0.setFont(font);
			g0.setColor(c1);
			g0.drawString(s, x, y);
			g0.setColor(c0);
		}
	}

	public MainFrame() {
		frame = new MyFrame(new FrameTheme("img/backgroud.png") {
		});
		controlor = new MyFrame(new FrameTheme("img/controlor.png") {
		});
		MyButton close = new MyButton(
				new ButtonTheme("img/close_button.png", "img/close_button_press.png", "img/close_button_hover.png") {
					@Override
					public void onClick(MouseEvent e) {
						super.onClick(e);
						System.exit(0);
					}
				});

		frame.setLocationRelativeTo(null);

		controlor.add(close);
		close.setLocation(138, 22);
		close.setSize(20, 20);

		MyPanel map = new MyPanel(new MapPanelTheme("img/csu_new.png"));
		map.setSize(590, 360);
		map.setLocation(120, 110);
		frame.add(map);

		MyButton refresh = new MyButton(new itermButtonTheme("刷新地图", 16, 22) {
			@Override
			public void onClick(MouseEvent e) {
				super.onClick(e);
				((MapPanelTheme) (map.getTheme())).reset();
				frame.setLocation(defaultX, defaultY);
				map.repaint();
			}
		});
		refresh.setSize(110, 30);
		controlor.add(refresh);
		refresh.setLocation(32, 50);

		MyButton relocate = new MyButton(new itermButtonTheme("查询道路", 16, 22) {
			@Override
			public void onClick(MouseEvent e) {
				if (graphics == null) {
					JOptionPane.showConfirmDialog(null, "未导入地图", "错误", JOptionPane.YES_NO_OPTION);
					return;
				}
				super.onClick(e);
				String s = JOptionPane.showInputDialog("请输入道路类型");
				if (s == null)
					return;
				ArrayList<Edge> es = graphics.getEdges();
				ArrayList<Edge> results = new ArrayList<>();
				if (s.contains("走")) {
					for (Edge edge : es) {
						if (edge.wark && !results.contains(edge))
							results.add(edge);
					}
				}
				if (s.contains("骑")) {
					for (Edge edge : es) {
						if (edge.bike && !results.contains(edge))
							results.add(edge);
					}
				}
				if (s.contains("校车")) {
					for (Edge edge : es) {
						if (edge.car && !results.contains(edge))
							results.add(edge);
					}
				}
				JOptionPane.showConfirmDialog(null, getEdgesString(results) + "\n", "满足条件的路",
						JOptionPane.YES_NO_OPTION);
			}
		});
		relocate.setSize(110, 30);
		controlor.add(relocate);
		relocate.setLocation(32, 200);

		MyButton open = new MyButton(new itermButtonTheme("打开文件", 16, 22) {
			@Override
			public void onClick(MouseEvent e) {
				super.onClick(e);
				File file = openLoadWindow();
				if (file == null)
					return;

				NodeReader r = new NodeReader(file);

				graphics = r.getGraphics();
				frame.repaint();
			}
		});
		open.setSize(110, 30);
		controlor.add(open);
		open.setLocation(32, 100);

		MyButton save = new MyButton(new itermButtonTheme("保存文件", 16, 22) {
			@Override
			public void onClick(MouseEvent e) {
				super.onClick(e);
				if (graphics == null) {
					JOptionPane.showConfirmDialog(null, "未导入地图", "错误", JOptionPane.YES_NO_OPTION);
					return;
				}
				File file = openSaveWindow();
				if (!file.exists())
					try {
						file.createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				if (graphics != null && file != null)
					NodeWriter.writeNode(graphics, file);
			}
		});
		save.setSize(110, 30);
		controlor.add(save);
		save.setLocation(32, 150);

		MyButton detail = new MyButton(new itermButtonTheme("地点详情", 16, 22) {
			@Override
			public void onClick(MouseEvent e) {
				if (graphics == null) {
					JOptionPane.showConfirmDialog(null, "未导入地图", "错误", JOptionPane.YES_NO_OPTION);
					return;
				}
				super.onClick(e);
				boolean hasnull = true;
				for (Node n : graphics.getNodes()) {
					if (n.c.equals(Color.BLUE)) {
						hasnull = false;
						JOptionPane.showConfirmDialog(null, n.message(), n.name(), JOptionPane.YES_NO_OPTION);
					}
				}
				if (hasnull)
					JOptionPane.showConfirmDialog(null, "未选择点", "错误", JOptionPane.YES_NO_OPTION);
			}
		});
		detail.setSize(110, 30);
		controlor.add(detail);
		detail.setLocation(32, 250);

		MyButton min = new MyButton(new itermButtonTheme("最近距离", 16, 22) {
			@Override
			public void onClick(MouseEvent e) {
				if (graphics == null) {
					JOptionPane.showConfirmDialog(null, "未导入地图", "错误", JOptionPane.YES_NO_OPTION);
					return;
				}
				super.onClick(e);
				if (count < 2) {
					JOptionPane.showConfirmDialog(null, "至少选择两个点", "错误", JOptionPane.YES_NO_OPTION);
					return;
				}
				int[] no = new int[count];
				int index = 0;
				ArrayList<Node> ns = graphics.getNodes();
				for (int i = 0; i < ns.size(); i++) {
					if (ns.get(i).c.equals(Color.BLUE)) {
						no[index++] = i;
					}
				}

				ArrayList<Edge> es = new ArrayList<>();
				Minlength minlength = new Minlength();
				if (index == 2) {
					graphics.minPath(no[0], no[1], es, minlength);
				} else {
					graphics.nodesMinPath(no, es, minlength);
				}

				JOptionPane.showConfirmDialog(null, getEdgesString(es) + "最短距离是:" + minlength.minlength + "\n", "经过的路",
						JOptionPane.YES_NO_OPTION);

			}
		});
		min.setSize(110, 30);
		controlor.add(min);
		min.setLocation(32, 300);

		MyButton direction = new MyButton(new itermButtonTheme("两点方向", 16, 22) {
			@Override
			public void onClick(MouseEvent e) {
				if (graphics == null) {
					JOptionPane.showConfirmDialog(null, "未导入地图", "错误", JOptionPane.YES_NO_OPTION);
					return;
				}
				super.onClick(e);
				if (count < 2) {
					JOptionPane.showConfirmDialog(null, "未选择两个点", "错误", JOptionPane.YES_NO_OPTION);
					return;
				}
				if (count > 2) {
					JOptionPane.showConfirmDialog(null, "只能选择两个点", "错误", JOptionPane.YES_NO_OPTION);
					return;
				}
				int[] no = new int[2];
				int index = 0;
				ArrayList<Node> ns = graphics.getNodes();
				for (int i = 0; i < ns.size(); i++) {
					if (ns.get(i).c.equals(Color.BLUE)) {
						no[index++] = i;
					}
				}

				Node n1 = graphics.getNodes().get(no[0]);
				Node n2 = graphics.getNodes().get(no[1]);
				String direction = "";

				double dx = n1.x() - n2.x();
				double dy = n1.y() - n2.y();
				if (dx == 0) {
					if (dy < 0)
						direction = "正北";
					else if (dy > 0)
						direction = "正南";
					else
						direction = "正中";
				} else if (dx < 0) {
					if (dy < 0)
						direction = "西北";
					else if (dy > 0)
						direction = "西南";
					else
						direction = "正西";
				} else {
					if (dy < 0)
						direction = "东北";
					else if (dy > 0)
						direction = "东南";
					else
						direction = "正东";
				}

				JOptionPane.showConfirmDialog(null, n1.name() + "在" + n2.name() + "的" + direction + "方向" + "\n",
						"两点间方向", JOptionPane.YES_NO_OPTION);

			}
		});
		direction.setSize(110, 30);
		controlor.add(direction);
		direction.setLocation(32, 350);

		MyButton allPath = new MyButton(new itermButtonTheme("两点路径", 16, 22) {
			@Override
			public void onClick(MouseEvent e) {
				if (graphics == null) {
					JOptionPane.showConfirmDialog(null, "未导入地图", "错误", JOptionPane.YES_NO_OPTION);
					return;
				}
				super.onClick(e);
				if (count < 2) {
					JOptionPane.showConfirmDialog(null, "未选择两个点", "错误", JOptionPane.YES_NO_OPTION);
					return;
				}
				if (count > 2) {
					JOptionPane.showConfirmDialog(null, "只能选择两个点", "错误", JOptionPane.YES_NO_OPTION);
					return;
				}
				int[] no = new int[2];
				int index = 0;
				ArrayList<Node> ns = graphics.getNodes();
				for (int i = 0; i < ns.size(); i++) {
					if (ns.get(i).c.equals(Color.BLUE)) {
						no[index++] = i;
					}
				}

				ArrayList<ArrayList<Edge>> paths = new ArrayList<>();
				graphics.allPath(no[0], no[1], paths);
				String result = "";
				for (ArrayList<Edge> es : paths) {
					result += getEdgesString(es) + "\n";
				}
				JOptionPane.showConfirmDialog(null, result,
						graphics.getNodes().get(no[0]).name() + "到" + graphics.getNodes().get(no[1]).name() + "的所有路径",
						JOptionPane.YES_NO_OPTION);

			}
		});
		allPath.setSize(110, 30);
		controlor.add(allPath);
		allPath.setLocation(32, 400);

		controlor.setLocationRelativeTo(frame);
		controlor.setLocation(frame.getX() + frame.getTheme().getBackgroudImage().getWidth(null), frame.getY() + 52);
	}

	public String getEdgesString(ArrayList<Edge> es) {
		String result = "";

		for (Edge e : es) {
			result += (e.name() + ":" + e.message() + ",距离:" + e.weight + "\n");
		}

		return result;
	}

	int count = 0;

	class MapPanelTheme extends DefaultPanelTheme implements MouseListener, MouseMotionListener, MouseWheelListener {

		int x0, y0, x, y, dx, dy;
		boolean ifDragged = false;
		int r = 20;

		public void reset() {
			scale = 1;
		}

		public MapPanelTheme(String backgroundImage) {
			super(backgroundImage);
		}

		double scale = 1;

		@Override
		public void mouseDragged(MouseEvent e) {
			x0 = x;
			y0 = y;
			x = e.getX();
			y = e.getY();
			int tmpx = dx, tmpy = dy;
			tmpx += x - x0;
			if (tmpx <= 0 && (-tmpx) <= backgroundImage.getWidth(null) / scale - mypanel.getWidth())
				dx = tmpx;
			tmpy += y - y0;
			if (tmpy <= 0 && (-tmpy) <= backgroundImage.getHeight(null) / scale - mypanel.getHeight())
				dy = tmpy;
			mypanel.repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		void count(int x) {
			count += x;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (graphics == null)
				return;
			int x = e.getX();
			int y = e.getY();
			for (Node n : graphics.getNodes()) {
				double tmpx = n.x(), tmpy = n.y();
				if (cal(x - dx, y - dy, tmp(tmpx), tmp(tmpy)) <= r / scale) {
					if (n.c.equals(Color.RED)) {
						// if (count <= 1) {
						n.c = Color.BLUE;
						count(1);
						mypanel.repaint();
						// }
					} else {
						if (count >= 0) {
							n.c = Color.RED;
							count(-1);
							mypanel.repaint();
						}
					}
				}
			}
		}

		double tmp(double x) {
			return (x) / scale;
		}

		double cal(double x, double y, double x1, double y1) {
			return Math.sqrt(Math.pow(Math.abs(x - x1), 2) + Math.pow(Math.abs(y - y1), 2));
		}

		@Override
		public void mousePressed(MouseEvent e) {
			x = e.getX();
			y = e.getY();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		/**
		 * 给窗体设置背景图片
		 * 
		 * @param g
		 *            窗体的画笔
		 */
		public void setBackgroudImage(Graphics g) {
			Graphics g0 = g.create();
			if (color != null) {
				g0.setColor(color);
				g0.fillRect(0, 0, mypanel.getWidth(), mypanel.getHeight());
			}
			if (backgroundImage != null) {
				g0.drawImage(backgroundImage, dx, dy, (int) (backgroundImage.getWidth(null) / scale),
						(int) (backgroundImage.getHeight(null) / scale), null);
			}
			if (graphics != null) {
				ArrayList<Node> ns = graphics.getNodes();
				for (Node n : ns) {
					g0.setColor(n.c);
					g0.fillOval((int) (((n.x() - r) / scale + dx)), (int) (((n.y() - r) / scale + dy)),
							(int) (r / scale), (int) (r / scale));
				}
			}
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int direction = e.getWheelRotation();
			double amount = e.getScrollAmount();
			if (direction > 0) {
				double tmpScale = scale * (amount);
				if (backgroundImage.getWidth(null) / tmpScale >= mypanel.getWidth()
						&& backgroundImage.getHeight(null) / tmpScale >= mypanel.getHeight()) {
					scale = tmpScale;
					if (dx + (backgroundImage.getWidth(null) / scale) < mypanel.getWidth())
						dx = (int) (mypanel.getWidth() - backgroundImage.getWidth(null) / scale);

					if (dy + (backgroundImage.getHeight(null) / scale) < mypanel.getHeight())
						dy = (int) (mypanel.getHeight() - backgroundImage.getHeight(null) / scale);
				}
			} else if (direction < 0) {
				scale /= amount;

			}
			mypanel.repaint();
		}

	}

	public void setVisible(boolean visible) {
		controlor.setVisible(visible);
		frame.setVisible(visible);
		defaultX = frame.getX();
		defaultY = frame.getY();
	}

	/**
	 * 创建打开文件的对话框
	 */
	public File openLoadWindow() {
		loadDialog = new MyFileDialog(frame, "打开文件", FileDialog.LOAD);
		loadDialog.setLocationRelativeTo(null);
		loadDialog.setVisible(true);

		String directory, f;
		directory = loadDialog.getDirectory();
		f = loadDialog.getFile();
		if (directory != null && f != null) {
			return new File(directory + f);
		}
		return null;
	}

	/**
	 * 创建保存文件的对话框
	 */
	public File openSaveWindow() {
		saveDialog = new MyFileDialog(frame, "保存文件", FileDialog.SAVE);
		saveDialog.setLocationRelativeTo(null);
		saveDialog.setVisible(true);

		String directory, f;
		directory = saveDialog.getDirectory();
		f = saveDialog.getFile();

		if (directory != null && f != null) {
			return new File(directory + f);
		}
		return null;
	}

	public static void main(String[] args) {
		MainFrame m = new MainFrame();
		m.setVisible(true);
	}
}
