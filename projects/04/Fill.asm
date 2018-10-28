// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

//note that the screen is 512*256, so 32*256 words = 8192
//reset all colors
@color
M=0
//start of infinite loop
(FOREVER)
	//check if key pressed
	@KBD
	D=M
	//decide where to go
	@SCREENBLACK
	D;JNE
	@SCREENWHITE
	D;JEQ


(SCREENBLACK)
	@color
	M=-1
	@FILLSCREEN
	0;JMP

(SCREENWHITE)
	@color
	M=0
	@FILLSCREEN
	0;JMP
	
(FILLSCREEN)
	@8192
	D=A
	@wordsleft
	M=D
	@SCREEN
	D=A
	@location
	M=D
	//start of loop
	(LOOP)
		@wordsleft
		D=M
		@FOREVER
		//if 0 words left, jump back to forever
		D;JEQ
		@color
		//storing color value
		D=M
		//setting color
		@location
		A=M
		M=D
		//changing location and wordsleft accordingly
		@location
		M=M+1
		@wordsleft
		M=M-1
		@LOOP
		0;JMP