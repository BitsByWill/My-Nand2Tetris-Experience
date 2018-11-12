import java.util.HashMap;
import java.util.ArrayList;

//goal is to find symbols and store all the symbols in a table
public class SymbolTable extends Thread
{
    private HashMap<String, String> symbols;
    private HashMap<String, String> labels;
    private HashMap<String, String> vars;
    //for counting memory location of variables
    int internalCounter;
    ArrayList<String> cleaned = new ArrayList<String>();

    public SymbolTable(ArrayList<String> cleaned)
    {
        this.cleaned = cleaned;
        this.labels = new HashMap<String, String>();
        this.vars = new HashMap<String, String>();
        this.symbols = new HashMap<String, String>();
        this.symbols.put("SP", "0");
        this.symbols.put("LCL", "1");
        this.symbols.put("ARG", "2");
        this.symbols.put("THIS", "3");
        this.symbols.put("THAT", "4");
        this.symbols.put("R0", "0");
        this.symbols.put("R1", "1");
        this.symbols.put("R2", "2");
        this.symbols.put("R3", "3");
        this.symbols.put("R4", "4");
        this.symbols.put("R5", "5");
        this.symbols.put("R6", "6");
        this.symbols.put("R7", "7");
        this.symbols.put("R8", "8");
        this.symbols.put("R9", "9");
        this.symbols.put("R10", "10");
        this.symbols.put("R11", "11");
        this.symbols.put("R12", "12");
        this.symbols.put("R13", "13");
        this.symbols.put("R14", "14");
        this.symbols.put("R15", "15");
        this.symbols.put("SCREEN", "16384");
        this.symbols.put("KBD", "24576");
        this.internalCounter = 16;
    }

    public void run()
    {
        //start scanning for labels and adding them to symboltable's label map
        this.buildLabelTable();
        //start scanning for variables and adding them to symboltable's variable map
        this.buildVarTable();
        //at the same time combine the maps in symbol table and create a final arraylist without labels
        this.combineTables();
        //System.out.println(symbols);
        return;
    }
    
    private void buildLabelTable()
    {
        //labels offset the line numbering, so requires this variable to maintain order
        int toSubtract = 0;
        for (int i = 0; i < cleaned.size(); i++)
        {
            //sees if it is a label declaration
            if (cleaned.get(i).charAt(0) == '(')
            {
                this.labels.put(cleaned.get(i).substring(1, cleaned.get(i).length() - 1), String.valueOf(i - (toSubtract++)));
            }
        }
        return;
    }
    
    private void buildVarTable()
    {
        //scan for A instructions, check if all not digits, check if not in any other tables including this one too, then add into var map
        for (int i = 0; i < cleaned.size(); i++)
        {
            String temp = cleaned.get(i).substring(1);
            if (cleaned.get(i).charAt(0) == '@' && !temp.matches("\\d+"))
            {
                if (!(this.symbols.containsKey(temp) || (this.labels.containsKey(temp)) || (this.vars.containsKey(temp))))
                {
                    this.vars.put(temp, String.valueOf(this.internalCounter++));
                }
            }
        }
        return;
    }
    
    private void combineTables()
    {
        for (String i : this.vars.keySet())
        {
            this.symbols.put(i, this.vars.get(i));
        }
        for (String i : this.labels.keySet())
        {
            this.symbols.put(i, this.labels.get(i));
        }
        return;
    }
    
    public boolean hasKey(String symbol)
    {
        return this.symbols.containsKey(symbol);
    }
    
    public String getValue(String symbol)
    {
        return this.symbols.get(symbol);
    }
}