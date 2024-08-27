# RoboGame

A game where two robots are programmed to travel as long as possible without their fuel tank emptying. The robots must collect fuel while operating, else their tank will empty. Whichever robot lasts the longest wins.

The important part of the code is the Parser (`Parser.java`). Each robot is given a set of instructions to operate. The parser parses the instructions for commands that the robot can execute. It is able to throw errors when it encounters grammatical errors.

`ParserTester.java` and `ParserTester2.java` can be run to check if the parser parses correctly.