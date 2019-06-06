public class CodeWriter extends Thread
{
    //when set to false, stop when empty
    public static boolean keepRunning = true;
    private StringBuffer asm;
    private int arithJumpFlag; //for gt, lt, eq

    //arithmetic logical commands
    private static final String ADD = 
        "@SP\n" +
        "AM=M-1\n" +
        "D=M\n" +
        "A=A-1\n" +
        "M=D+M\n";
    private static final String SUB =
        "@SP\n" +
        "AM=M-1\n" +
        "D=M\n" +
        "A=A-1\n" +
        "M=M-D\n"; 
    private static final String AND =
        "@SP\n" +
        "AM=M-1\n" +
        "D=M\n" +
        "A=A-1\n" +
        "M=D&M\n";
    private static final String OR =
        "@SP\n" +
        "AM=M-1\n" +
        "D=M\n" +
        "A=A-1\n" +
        "M=D|M\n";
    private static final String NEG =
        "@SP\n" +
        "A=M-1\n" +
        "M=-M\n";
    private static final String NOT =
        "@SP\n" +
        "A=M-1\n" +
        "M=!M\n";

    public CodeWriter()
    {
        this.asm = new StringBuffer();
        arithJumpFlag = 0;
    }

    public void run()
    {
        //REMEMBER TO REMOVE FROM END, also before asm code, comments always added for vm command
        while (keepRunning || !VMTranslator.VMCode.isEmpty())
        {
            if(!VMTranslator.VMCode.isEmpty())
            {
                //arithmetic or logical commands
                if (!(VMTranslator.VMCode.getLast().equals("pop") || VMTranslator.VMCode.getLast().equals("push")))
                {
                    this.arithLogic(VMTranslator.VMCode.removeLast());
                }
                else
                //push/pop commands
                {
                    //wait till it is length 3
                    while (VMTranslator.VMCode.size() < 3);
                    this.pushPop(VMTranslator.VMCode.removeLast(), VMTranslator.VMCode.removeLast(), Integer.parseInt(VMTranslator.VMCode.removeLast()));
                }
            }
        }
        return;
    }

    private void arithLogic(String command)
    {
        this.asm.append("//" + command + "\n");
        //lt, gt, eq
        if ("ltgteq".contains(command))
        {
            this.asm.append("@SP\nAM=M-1\nD=M\nA=A-1\n"); //now two layers above stack pointer is in M, one layer above is in D
            this.asm.append("D=M-D\n"); //sets up for the compare, the asm jump is flipped from the lt, gt, eq
            //@FALSE_num
            //D;jump_condition
            //@SP
            //A=M-1
            //M=-1 //true
            //@CONTINUE_num
            //0;JMP
            //(FALSE_num)
            //@SP
            //A=M-1
            //M=0 //false
            //(CONTINUE_num)
            if (command.equals("lt"))
            {
                this.asm.append("@FALSE" + arithJumpFlag + "\nD;JGE\n@SP\nA=M-1\nM=-1\n@CONTINUE" + arithJumpFlag + "\n0;JMP\n(FALSE" + arithJumpFlag + ")\n@SP\nA=M-1\nM=0\n(CONTINUE" + arithJumpFlag + ")\n");
            }
            else if (command.equals("gt"))
            {
                this.asm.append("@FALSE" + arithJumpFlag + "\nD;JLE\n@SP\nA=M-1\nM=-1\n@CONTINUE" + arithJumpFlag + "\n0;JMP\n(FALSE" + arithJumpFlag + ")\n@SP\nA=M-1\nM=0\n(CONTINUE" + arithJumpFlag + ")\n");
            }
            //eq
            else
            {
                this.asm.append("@FALSE" + arithJumpFlag + "\nD;JNE\n@SP\nA=M-1\nM=-1\n@CONTINUE" + arithJumpFlag + "\n0;JMP\n(FALSE" + arithJumpFlag + ")\n@SP\nA=M-1\nM=0\n(CONTINUE" + arithJumpFlag + ")\n");
            }
            arithJumpFlag++;
        }
        else
        {
            switch (command)
            {
                case "add":
                    this.asm.append(ADD);
                    break;
                case "sub":
                    this.asm.append(SUB);
                    break;
                case "and":
                    this.asm.append(AND);
                    break;
                case "or":
                    this.asm.append(OR);
                    break;
                case "neg":
                    this.asm.append(NEG);
                    break;
                case "not":
                    this.asm.append(NOT);
                    break;
                default:
                    break;
            }
        }
        return;
    }

    private void pushPop(String command, String segment, int i)
    {
        this.asm.append("//" + command + " " + segment + " " + i + "\n");
        //for constant
        if (command.equals("push"))
        {
            if (segment.equals("constant"))
            {
                this.asm.append("@" + i + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            }
            //for temp
            else if (segment.equals("temp"))
            {
                this.asm.append("@R5\nD=M\n@" + (i + 5) + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            }
            //for this, that, arg, local
            else if ("thisthatargumentlocal".contains(segment))
            {
                if (segment.equals("local"))
                {
                    this.asm.append("@LCL\n");
                }
                else if (segment.equals("argument"))
                {
                    this.asm.append("@ARG\n");
                }
                else if (segment.equals("this"))
                {
                    this.asm.append("@THIS\n");
                }
                else
                {
                    this.asm.append("@THAT\n");
                }
                this.asm.append("D=M\n@" + i + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            }
            // pointer
            else if (segment.equals("pointer"))
            {
                if (i == 0)
                {
                    this.asm.append("@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                }
                else
                {
                    this.asm.append("@THAT\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                }
            }
            //static segment starts at 16
            else
            {
                this.asm.append("@" + (i + 16) + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            }
        }
        //pop time
        else
        { //constant segment nonexistent for pop
            //for temp
            if (segment.equals("temp"))
            {
                this.asm.append("@R5\nD=M\n@" + (i + 5) + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
            }
            //thisthatarglocal
            else if ("thisthatargumentlocal".contains(segment))
            {
                if (segment.equals("local"))
                {
                    this.asm.append("@LCL\n");
                }
                else if (segment.equals("argument"))
                {
                    this.asm.append("@ARG\n");
                }
                else if (segment.equals("this"))
                {
                    this.asm.append("@THIS\n");
                }
                else
                {
                    this.asm.append("@THAT\n");
                }
                this.asm.append("D=M\n@" + i + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
            }
            // pointer
            else if (segment.equals("pointer"))
            {
                if (i == 0)
                {
                    this.asm.append("@THIS\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
                }
                else
                {
                    this.asm.append("@THAT\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
                }
            }
            //static segment starts at 16
            else
            {
                this.asm.append("@" + (i + 16) + "\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
            }
        }
        return;
    }

    public String getASM()
    {
        return this.asm.toString();
    }
}