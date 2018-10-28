// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Put your code here.

//	int x, y, out;
//	for (int i = 0; i < y; i++)
//	{
//		x += x;
//	}

//reset R@
@R2
M=0
@i
M=0
(LOOP)
	//the increment variable, resetted above already
	@i
	D=M
	@R1
	D=D-M
	@END
	//checks if i == to R1, if it is, goto END
	D;JEQ
	//adding to multiply
	@R0
	D=M
	@R2
	M=D+M
	//store in RAM[2]
	@i
	M=M+1
	@LOOP
	0;JMP
	
//final infinite loop
(END)
	@END 
	0;JMP