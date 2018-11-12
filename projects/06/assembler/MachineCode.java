import java.util.ArrayList;
import java.util.HashMap;

public class MachineCode
{
    private ArrayList<String> finalClean;
    private StringBuilder[] mc;
    private HashMap<String, String> comp;
    private HashMap<String, String> jump;
    private HashMap<String, String> dest;
    
    public MachineCode(ArrayList<String> finalClean)
    {
        this.finalClean = finalClean;
        this.mc = new StringBuilder[finalClean.size()];
        for (int i = 0; i < this.mc.length; i++)
        {
            this.mc[i] = new StringBuilder(16);
        }
        this.comp = new HashMap<String,String>();
        this.comp.put("0", "0101010");
        this.comp.put("1", "0111111");
        this.comp.put("-1","0111010");
        this.comp.put("D", "0001100");
        this.comp.put("A", "0110000");
        this.comp.put("!D", "0001101");
        this.comp.put("!A", "0110001");
        this.comp.put("-D", "0001111");
        this.comp.put("-A", "0110011");
        this.comp.put("D+1","0011111");
        this.comp.put("A+1","0110111");
        this.comp.put("D+A","0000010");
        this.comp.put("D-A","0010011");
        this.comp.put("A-D","0000111");
        this.comp.put("D&A","0000000");
        this.comp.put("D|A","0010101"); 
        this.comp.put("M","1110000");
        this.comp.put("!M","1110001");
        this.comp.put("-M","1110011");
        this.comp.put("M+1","1110111");
        this.comp.put("M-1","1110010");
        this.comp.put("D+M","1000010");
        this.comp.put("D-M","1010011");
        this.comp.put("M-D","1000111");
        this.comp.put("D&M","1000000");
        this.comp.put("D|M","1010101");
        this.comp.put("D-1","0001110");
        this.comp.put("A-1","0110010");
        this.jump = new HashMap<String,String>();
        this.jump.put("null", "000");
        this.jump.put("JGT", "001");
        this.jump.put("JEQ", "010");
        this.jump.put("JGE", "011");
        this.jump.put("JLT","100");
        this.jump.put("JNE","101");
        this.jump.put("JLE","110");
        this.jump.put("JMP", "111");
        this.dest = new HashMap<String, String>();
        this.dest.put("null","000");
        this.dest.put("M","001");
        this.dest.put("D","010");
        this.dest.put("MD","011");
        this.dest.put("A","100");
        this.dest.put("AM","101");
        this.dest.put("AD","110");
        this.dest.put("AMD","111");
    }
    
    public void generate()
    {
        //iterate through internal array and see if A or C instruction
        for (int i = 0; i < this.finalClean.size(); i++)
        {
            //tell which instruction to build based on arraylist index
            if (this.finalClean.get(i).charAt(0) == '@')
            {
                this.buildAIns(i);
            }
            else
            {
                this.buildCIns(i);
            }
        }
        return;
    }
    
    public StringBuilder[] getMachineCode()
    {
        return this.mc;
    }
    
    private void buildAIns(int i)
    {
        String binary = bin(Integer.parseInt(this.finalClean.get(i).substring(1)));
        this.mc[i].append("0");
        this.mc[i].append(new String(new char[15 - binary.length()]).replace("\0", "0"));
        this.mc[i].append(binary);
        //System.out.println(mc[i] + " " + finalClean.get(i));
        return;
    }
    
    private void buildCIns(int i)
    {
        this.mc[i].append("111");
        String temp = this.finalClean.get(i);
        int equal = temp.indexOf("=");
        int sc = temp.indexOf(";");
        //System.out.println(equal + " " + sc + " " + temp);
        //white space does not exist anymore so shouldn't matter
        this.mc[i].append(this.comp.get(temp.substring((equal < 0)?0:(equal + 1), (sc < 0)?temp.length():sc)));
        this.mc[i].append((equal < 0)?"000":this.dest.get(temp.substring(0, equal)));
        this.mc[i].append((sc < 0)?"000":this.jump.get(temp.substring(sc + 1)));
        //this.mc[i].append(" " + finalClean.get(i));
        //System.out.println(mc[i] + " " + finalClean.get(i));
        return;
    }
    
    private String bin(int value)
    {
        return Integer.toBinaryString(value);
    }
}