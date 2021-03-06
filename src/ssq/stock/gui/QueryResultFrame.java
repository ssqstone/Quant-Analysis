package ssq.stock.gui;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.TableModel;

import ssq.stock.Stock;
import ssq.utils.Pair;
import ssq.utils.TreeNode;

public class QueryResultFrame extends TableFrame
{
	private static final long	serialVersionUID	= 1L;
	protected TextField			tf;
	protected JButton			button;
	private RecordHistory		history;
								
	public QueryResultFrame(File f) throws FileNotFoundException
	{
		super(new FileInputStream(f));
	}
	
	@Override
	protected void initListeners()
	{
		super.initListeners();
		
		tf.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == Event.ENTER) // 如果检测到输入了Enter键
				{
					TableModel tm = table.getModel();
					int cnt = tm.getRowCount();
					int i = 0;
					for (; i < cnt; i++)
					{
						String tmp = Stock.pad(Integer.valueOf(tm.getValueAt(i, 0).toString()));
						
						if (tmp.equals(tf.getText()))
						{
							table.setRowSelectionInterval(i, i);
							
							Rectangle rect = table.getCellRect(i, 0, true);
							
							table.scrollRectToVisible(rect);
							table.requestFocus();
							break;
						}
					}
					
					if (i == cnt)
					{
						JOptionPane.showMessageDialog(null, "未找到当前代码的股票");
					}
				}
				super.keyReleased(e);
			}
		});
		
		button.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				
				String path = GUI.instance.textFields[5].getText().trim();
				
				if (path.isEmpty())
				{
					JOptionPane.showMessageDialog(null, "未配置通达信路径");
				}
				else
				{
					File file = new File(path, "T0002/blocknew/GSXG.blk");
					if (file.exists())
					{
						Writer writer = null;
						try
						{
							writer = new FileWriter(file);
							
							for (Pair<Integer, TreeNode<Float>> entry : history.records)
							{
								int num = entry.getKey();
								
								String tmp = (num >= 600000 ? "1" : "0") + Stock.pad(num) + '\n';
								
								writer.write(tmp);
							}
						}
						catch (IOException e1)
						{
							JOptionPane.showMessageDialog(null, "无法写入通达信自选板块, 请关闭通达信试试");
						}
						finally
						{
							try
							{
								writer.close();
							}
							catch (IOException e1)
							{
								e1.printStackTrace();
							}
						}
						
						JOptionPane.showMessageDialog(null, "导出成功! 请不再操作通达信自建板块并重启通达信查看. ");
					}
					else
					{
						JOptionPane.showMessageDialog(null, "请在通达信中新建一个名为\"公式选股\"的自定板块, 再次导入本次结果, 并重启通达信. ");
					}
				}
			}
		});
	}
	
	@Override
	protected void initComponent()
	{
		super.initComponent();
		
		tf = new TextField(6);
		JPanel searchJPanel = new JPanel();
		JLabel label = new JLabel("搜索股票");
		button = new JButton("导出到通达信");
		
		label.setFont(GUI.SONGFONT_FONT);
		button.setFont(GUI.SONGFONT_FONT);
		
		searchJPanel.add(label);
		searchJPanel.add(tf);
		searchJPanel.add(button);
		
		add(searchJPanel, BorderLayout.NORTH);
	}
	
	@Override
	protected KeyListener getTableKeyListener()
	{
		return new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == Event.ENTER) // 如果检测到输入了Enter键
				{
					sendKey();
				}
				super.keyReleased(e);
			}
			
		};
	}
	
	public void sendKey()
	{
		int row = table.convertRowIndexToModel(table.getSelectedRow()), column = table.convertColumnIndexToModel(0);
		
		String num = table.getModel().getValueAt(row, column).toString();
		
		String cmd = "sendkey " + num + " " + GUI.instance.textFields[1].getText();
		
		try
		{
			Runtime.getRuntime().exec(cmd);
		}
		catch (IOException e1)
		{
			GUI.statusText("执行跳转时发生意外: " + e1.getLocalizedMessage());
			e1.printStackTrace();
		}
	}
	
	@Override
	public MouseAdapter getTableMouseListener()
	{
		return new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					if (e.getClickCount() == 2)
					{
						sendKey();
					}
				}
				else if (e.getButton() == MouseEvent.BUTTON3)
				{
					int i = table.rowAtPoint(e.getPoint());
					table.setRowSelectionInterval(i, i);
					
					try
					{
						PipedOutputStream po = new PipedOutputStream();
						PipedInputStream pi = new PipedInputStream();
						final ObjectOutputStream o = new ObjectOutputStream(new BufferedOutputStream(po));
						
						pi.connect(po);
						
						new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								try
								{
									int row = table.convertRowIndexToModel(table.getSelectedRow());
									o.writeObject(history.AST);
									o.writeObject(history.records.get(row));
								}
								catch (IOException e)
								{
									e.printStackTrace();
								}
								finally
								{
									try
									{
										o.close();
									}
									catch (IOException e)
									{
									}
								}
							}
						}).start();
						
						new DetailedGradeFrame(pi).show();
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
					
				}
			}
		};
	}
	
	@Override
	public Pair<Object[][], Object[]> toTable() throws FileNotFoundException, IOException
	{
		ObjectInputStream i = new ObjectInputStream(iniData);
		ArrayList<String[]> data = new ArrayList<>();
		String[] names = new String[] { "编号", "名字", "评分" };
		
		try
		{
			history = (RecordHistory) i.readObject();
			setStatusText("选股命令: " + history.AST.toString());
			
			for (Pair<Integer, TreeNode<Float>> pair : history.records)
			{
				int id = pair.getKey();
				data.add(new String[] { Stock.pad(String.valueOf(id)), Stock.stockList.get(-Stock.stockList.findInsertIndex(id) - 1).getValue(), Float.valueOf((pair.getValue().getElement() * 100f)).toString() });
			}
			
		}
		catch (ClassNotFoundException e)
		{
			GUI.statusText(e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		i.close();
		
		return new Pair<Object[][], Object[]>(data.toArray(new String[][] {}), names);
	}
}
