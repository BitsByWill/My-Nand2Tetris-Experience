import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class VMTranslator
{
    public static String outputFile = "";
    public static ArrayDeque<String> VMCode = new ArrayDeque<String>();
    
    public static void main (String [] args) throws Exception
    {
        String filename = args[0];
        outputFile = filename.substring(0, filename.length() - 2) + "asm";
        //add to arraydeque like queue, for codewriter (concurrent)
        //if space, add new, if //, next line
        try
        {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            //start codewriter here
            CodeWriter cw = new CodeWriter();
            cw.start();
            String line = "";
            while ((line = br.readLine()) != null)
            {
                //above grabs a line
                //checks if code here
                if (line.length() >= 2 && !line.substring(0, 2).equals("//"))
                {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < line.length(); i++)
                    {
                        //read word by word (char by char), if space, add buffer as string to deque, clear buffer and again
                        if (line.charAt(i) != ' ')
                        {
                            sb.append(line.charAt(i));
                        }
                        else
                        {
                            //add in front of deque
                            VMCode.addFirst(sb.toString());
                            sb = new StringBuffer();
                        }
                    }
                    //add the remaining in buffer
                    VMCode.addFirst(sb.toString());
                    sb = new StringBuffer();
                }
            }
            CodeWriter.keepRunning = false; //next time when deque empty, stop running
            cw.join();
            if (args.length > 1 && args[1].equals("debug"))
            {
                System.out.println(cw.getASM()); //debug
            }
            //writing output
            FileWriter fw = new FileWriter(outputFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(cw.getASM());
            //exit the program
            bw.write("(END_OF_PROGRAM)\n@END_OF_PROGRAM\n0;JMP");
            bw.close();
            fw.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}