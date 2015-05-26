package ssq.stock.analyser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.ListIterator;

import ssq.stock.DateData;
import ssq.stock.Stock;
import ssq.utils.LogUtils;

public class RangeAnalyzer extends Analyzer
{

    File outPre = new File("横盘统计.txt");
    File outNow = new File("牛市统计.txt");
    
    public static void main(String[] args) throws Exception
    {
        LogUtils.logString(args[0], "通达信的安装路径", false);
        
        new RangeAnalyzer().run(args[0]);
    }
    
    public RangeAnalyzer()
    {
        if (outNow.isFile())
        {
            outNow.delete();
        }

        if (outPre.isFile())
        {
            outPre.delete();
        }
    }
    
    public static class Recoder
    {
        LinkedList<DateData> dateOrder = new LinkedList<>();
        LinkedList<DateData> valOrder  = new LinkedList<>();
        int                  capacity  = 250;

        public Recoder()
        {
        }
        
        public void insert(DateData data)
        {
            int scaledVal = data.getScaledVal();
            boolean succ = false;
            for (ListIterator<DateData> i = valOrder.listIterator(); i.hasNext();)
            {
                DateData tmpData = i.next();

                if (tmpData.getScaledVal() < scaledVal)
                {
                    succ = true;
                    if (i.hasPrevious())
                    {
                        i.previous();
                        i.add(data);
                    }
                    else
                    {
                        valOrder.addFirst(data);
                    }
                    
                    break;
                }
            }
            
            if (!succ)
            {
                valOrder.addLast(data);
            }

            dateOrder.addFirst(data);
            
            if (dateOrder.size() > capacity)
            {
                valOrder.remove(dateOrder.getLast());
                dateOrder.removeLast();
            }
        }
    }
    
    @Override
    protected void scan(File f)
    {
        try
        {
            Stock s = new Stock(f, -1, -1);
            
            if (s.history.size() == 0)
            {
                throw new Exception(s + " 空文件");
            }
            
            Recoder recoder = new Recoder();
            
            DateData maxInQueue = null;
            DateData minInQueue = null;
            
            for (int i = 0; i < s.history.size(); i++)
            {
                DateData thisDay = s.history.get(i);
                int scaledVal = thisDay.getScaledVal();
                
                recoder.insert(thisDay);
                
                if (minInQueue != null) //在上升序列
                {
                    if (recoder.valOrder.getFirst().getScaledVal() == scaledVal) //比昨天更高了
                    {
                        maxInQueue = thisDay;
                    }
                    else if (scaledVal < maxInQueue.getScaledVal() * 0.9) //跌破
                    {
                        BufferedWriter fout;
                        
                        if (minInQueue.date > 20141100)
                        {
                            fout = new BufferedWriter(new FileWriter(outNow, true));
                        }
                        else if (minInQueue.date < 20141100 && minInQueue.date > 20110800)
                        {
                            fout = new BufferedWriter(new FileWriter(outPre, true));
                        }
                        else
                        {
                            minInQueue = null; //结束上升序列
                            continue;
                        }

                        StringBuilder sb = new StringBuilder();
                        sb.append(s.number).append(' ')
                        .append(minInQueue.date).append(' ').append(minInQueue.closing).append(' ').append(String.valueOf(minInQueue.scale).substring(0, Math.min(4, String.valueOf(minInQueue.scale).length()))).append(' ').append(minInQueue.getScaledVal()).append(' ')
                        .append(maxInQueue.date).append(' ').append(maxInQueue.closing).append(' ').append(String.valueOf(maxInQueue.scale).substring(0, Math.min(4, String.valueOf(maxInQueue.scale).length()))).append(' ').append(maxInQueue.getScaledVal()).append(' ')
                        .append(thisDay.date).append(' ').append(thisDay.closing).append(' ').append(String.valueOf(thisDay.scale).substring(0, Math.min(4, String.valueOf(thisDay.scale).length()))).append(' ').append(thisDay.getScaledVal()).append(' ')
                        .append((DateData.numberToDate(maxInQueue.date).getTime() - DateData.numberToDate(minInQueue.date).getTime()) / (1000 * 3600 * 24)).append(' ')
                        .append((DateData.numberToDate(thisDay.date).getTime() - DateData.numberToDate(maxInQueue.date).getTime()) / (1000 * 3600 * 24)).append(' ')
                        .append((float) maxInQueue.getScaledVal() / (float) minInQueue.getScaledVal() - 1f).append(' ').append(1f - (float) thisDay.getScaledVal() / (float) maxInQueue.getScaledVal()).append(' ')
                        .append("\r\n");
                        
                        fout.write(sb.toString());
                        fout.close();

                        minInQueue = null; //结束上升序列
                    }
                }
                else
                {
                    if (recoder.valOrder.getFirst().getScaledVal() == scaledVal) //是250个交易日以来的新高
                    {
                        maxInQueue = thisDay;
                        minInQueue = thisDay; //开始上升序列
                    }
                }
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
