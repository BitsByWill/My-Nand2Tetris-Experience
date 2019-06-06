//push constant 111
@111
D=A
@SP
A=M
M=D
@SP
M=M+1
//push constant 333
@333
D=A
@SP
A=M
M=D
@SP
M=M+1
//push constant 888
@888
D=A
@SP
A=M
M=D
@SP
M=M+1
//pop static 8
@24
D=A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
//pop static 3
@19
D=A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
//pop static 1
@17
D=A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
//push static 3
@19
D=M
@SP
A=M
M=D
@SP
M=M+1
//push static 1
@17
D=M
@SP
A=M
M=D
@SP
M=M+1
//sub
@SP
AM=M-1
D=M
A=A-1
M=M-D
//push static 8
@24
D=M
@SP
A=M
M=D
@SP
M=M+1
//add
@SP
AM=M-1
D=M
A=A-1
M=D+M
(END_OF_PROGRAM)
@END_OF_PROGRAM
0;JMP