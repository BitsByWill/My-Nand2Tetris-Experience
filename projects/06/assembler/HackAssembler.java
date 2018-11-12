import java.io.FileReader;
import java.io.BufferedReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedWriter;

//main program, file input, output, parses code, calls the correct methods, classes, writes output
public class HackAssembler
{
    public static String output;

    public static void main(String [] args)
    {
        if (args.length < 1)
        {
            System.out.println("Requires input file");
            System.exit(1);
        }
        //output file name
        output = args[0].substring(0, args[0].length() - 4) + ".hack";
        //starting to produce a cleaned version of the source code - no white space, comments, etc.
        StringBuffer buffer = new StringBuffer();
        String uncleanedCode;
        try
        {
            FileReader fr = new FileReader(args[0]);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null)
            {
                buffer.append(line + "\n");
            }
            br.close();
            //finished file reading
            uncleanedCode = buffer.toString();
            //time to remove space
            Pattern p = Pattern.compile("[ \t]");
            Matcher m = p.matcher(uncleanedCode);
            uncleanedCode = m.replaceAll("");
            //time to remove comments
            p = Pattern.compile("\\/\\/.+\n");
            m = p.matcher(uncleanedCode);
            //the replacement with new line cleans up side comments next to code
            uncleanedCode = m.replaceAll("\n");
            //adding in cleaned up strings, line by line, skip all blank lines
            //System.out.println(uncleanedCode);
            ArrayList<String> cleaned = new ArrayList<String>();
            p = Pattern.compile("[\\w|@|(].*\n");
            m = p.matcher(uncleanedCode);
            while (m.find())
            {
                cleaned.add(m.group().substring(0, m.group().length() - 1));
            }
            System.out.println("Parsing stage completed...");
            //start multithreaded symbol table building as new arraylist is built without labels
            SymbolTable sb = new SymbolTable(cleaned);
            sb.run();
            //build new arraylist without labels
            ArrayList<String> finalClean = new ArrayList<String>(cleaned.size());
            for (int i = 0; i < cleaned.size(); i++)
            {
                if (cleaned.get(i).charAt(0) != '(')
                {
                    finalClean.add(cleaned.get(i));
                }
            }
            //waiting till thread stops
            try
            {
                sb.join();
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
            System.out.println("Symbol table generation completed...");
            //symbol substitution time
            for (int i = 0; i < finalClean.size(); i++)
            {
                if (finalClean.get(i).charAt(0) == '@')
                {
                    String temp = finalClean.get(i).substring(1);
                    if (sb.hasKey(temp))
                    {
                        finalClean.set(i, "@" + sb.getValue(temp));
                    }
                }
            }
            System.out.println("Symbol substitution commpleted...");
            //send to machine code
            MachineCode mc = new MachineCode(finalClean);
            mc.generate();
            System.out.println("Finished generating machine code...");
            StringBuilder[] machinecode = mc.getMachineCode();
            try
            {
                FileWriter fw = new FileWriter(output);
                BufferedWriter bw = new BufferedWriter(fw);
                for (int i = 0; i < machinecode.length; i++)
                {
                    bw.write(machinecode[i].toString());
                    if (i != machinecode.length - 1)
                    {
                        bw.write("\n");
                    }
                }
                bw.close();
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
            System.out.println("Written output to " + output + "...");
            System.out.println("Assembly successfully completed");
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}