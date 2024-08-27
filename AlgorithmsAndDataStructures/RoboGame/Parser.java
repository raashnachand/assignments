import javax.swing.plaf.nimbus.State;
import java.sql.Statement;
import java.util.*;
import java.util.regex.*;

/**
 * See assignment handout for the grammar.
 * You need to implement the parse(..) method and all the rest of the parser.
 * There are several methods provided for you:
 * - several utility methods to help with the parsing
 * See also the TestParser class for testing your code.
 */
public class Parser {


    // Useful Patterns

    static final Pattern NUMPAT = Pattern.compile("-?[1-9][0-9]*|0"); 
    static final Pattern OPENPAREN = Pattern.compile("\\(");
    static final Pattern CLOSEPAREN = Pattern.compile("\\)");
    static final Pattern OPENBRACE = Pattern.compile("\\{");
    static final Pattern CLOSEBRACE = Pattern.compile("\\}");

    //----------------------------------------------------------------
    /**
     * The top of the parser, which is handed a scanner containing
     * the text of the program to parse.
     * Returns the parse tree.
     */
    ProgramNode parse(Scanner s) {
        // Set the delimiter for the scanner.
        s.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");
        // THE PARSER GOES HERE
        // Call the parseProg method for the first grammar rule (PROG) and return the node
        return parseProg(s);
    }

    ProgramNode parseProg(Scanner s){
        if (!s.hasNext()){
            fail("Empty program", s);
            return null;
        }
        List<ProgramNode> nodes = new ArrayList<ProgramNode>();
        ProgramNode ast = parseStatement(s, nodes);
        if (s.hasNext()){
            fail("Extra/unknown token found", s);
            return null;
        }
        return parseStatement(s, nodes);
    }

    ProgramNode parseStatement(Scanner s, List<ProgramNode> nodes){
        if (s.hasNext("move"))          {nodes.add(parseMove(s)); parseStatement(s, nodes);}
        else if (s.hasNext("turnL"))         {nodes.add(parseTurnLeft(s)); parseStatement(s, nodes);}
        else if (s.hasNext("turnR"))         {nodes.add(parseTurnRight(s)); parseStatement(s, nodes);}
        else if (s.hasNext("takeFuel"))      {nodes.add(parseTakeFuel(s)); parseStatement(s, nodes);}
        else if (s.hasNext("wait"))          {nodes.add(parseWait(s)); parseStatement(s, nodes);}
        else if (s.hasNext("turnAround"))    {nodes.add(parseTurnAround(s)); parseStatement(s, nodes);}
        else if (s.hasNext("shieldOn"))      {nodes.add(parseShieldOn(s)); parseStatement(s, nodes);}
        else if (s.hasNext("shieldOff"))     {nodes.add(parseShieldOff(s)); parseStatement(s, nodes);}
        else if (s.hasNext("if"))            {nodes.add(parseIf(s)); parseStatement(s, nodes);}
        else if (s.hasNext("while"))         {nodes.add(parseWhile(s)); parseStatement(s, nodes);}
        else if (s.hasNext("loop"))          {nodes.add(parseLoop(s));}
        if (nodes.isEmpty()){
            fail("No actions found", s);
        }
        return new StatementNode(nodes);
    }

    /**
     * Move parser
     */
    ProgramNode parseMove (Scanner s){
        require("move", "expecting 'move'", s);
        if (s.hasNext(OPENPAREN)){
            require(OPENPAREN, "expecting open parentheses", s);
            IntNode expr = parseExprNode(s);
            require(CLOSEPAREN, "expecting close parentheses", s);
            require(";", "expecting semi-colon", s);
            if (expr != null){
                return new MoveNode("move", expr);
            }
        }
        require(";", "expecting semi-colon", s);
        return new MoveNode("move", null);
    }
    /**
     * turn left parser
     */
    ProgramNode parseTurnLeft(Scanner s){
        require("turnL", "expecting 'turnL'", s);
        require(";", "expecting semi-colon", s);
        return new TurnLNode("turnL");
    }
    /**
     * turn right parser
     */
    ProgramNode parseTurnRight(Scanner s){
        require("turnR", "expecting 'turnR'", s);
        require(";", "expecting semi-colon", s);
        return new TurnRNode("turnR");
    }
    /**
     * take fuel parser
     */
    ProgramNode parseTakeFuel(Scanner s){
        require("takeFuel", "expecting 'takeFuel'", s);
        require(";", "expecting semi-colon", s);
        return new TakeFuelNode("takeFuel");
    }
    /**
     * wait parser
     */
    ProgramNode parseWait(Scanner s){
        require("wait", "expecting 'wait'", s);
        if (s.hasNext(OPENPAREN)){
            s.next();
            IntNode expr = parseExprNode(s);
            require(CLOSEPAREN, "expecting close parentheses", s);
            require(";", "expecting semi-colon", s);
            return new WaitNode("wait", expr);
        }
        require(";", "expecting semi-colon", s);
        return new WaitNode("wait", null);
    }
    /**
     * loop parser
     */
    ProgramNode parseLoop(Scanner s){
        require("loop", "expecting 'loop'", s);
        BlockNode block = new BlockNode(parseBlock(s));
        return new LoopNode(block);
    }
    /**
     * block parser
     */
    ProgramNode parseBlock(Scanner s){
        require(OPENBRACE, "missing open brace", s);
        List<ProgramNode> blockNodes = new ArrayList<ProgramNode>();
        ProgramNode sn = parseStatement(s, blockNodes);
        require(CLOSEBRACE, "missing close brace", s);
        if (sn != null){
            return new BlockNode(sn);
        }
        fail("No statement node in block", s);
        return null;
    }
    /**
     * turn around parser
     */
    ProgramNode parseTurnAround(Scanner s){
        require("turnAround", "expecting 'turnAround'", s);
        require(";", "expecting semi-colon", s);
        return new TurnAroundNode("turnAround");
    }
    /**
     * shield on parser
     */
    ProgramNode parseShieldOn(Scanner s){
        require("shieldOn", "expecting 'shieldOn'", s);
        require(";", "expecting semi-colon", s);
        return new ShieldOnNode("shieldOn");
    }
    /**
     * shield off parser
     */
    ProgramNode parseShieldOff(Scanner s){
        require("shieldOff", "expecting 'shieldOff'", s);
        require(";", "expecting semi-colon", s);
        return new ShieldOffNode("shieldOff");
    }
    /**
     * if parser
     */
    ProgramNode parseIf(Scanner s){
        require("if", "expecting 'if'", s);
        require(OPENPAREN, "expecting open parentheses pattern for 'if'", s);
        BooleanNode conditionNode = parseConditionNode(s); //parse condition node
        require(CLOSEPAREN, "expecting close parentheses pattern for 'if'", s);
        ProgramNode blockNode1 = parseBlock(s); //parse block node
        if (s.hasNext("else")){
            s.next();
            ProgramNode blockNode2 = parseBlock(s);
            return new IfNode(conditionNode, blockNode1, blockNode2);
        }
        return new IfNode(conditionNode, blockNode1, null);
    }
    /**
     * while parser
     */
    ProgramNode parseWhile(Scanner s){
        require("while", "expecting 'while'", s);
        require(OPENPAREN, "expecting open parentheses pattern for 'while'", s);
        BooleanNode conditionNode = parseConditionNode(s); //parse condition node
        require(CLOSEPAREN, "expecting close parentheses pattern for 'while'", s);
        ProgramNode blockNode = parseBlock(s); //parse block node
        return new WhileNode(conditionNode, blockNode);
    }
    /**
     * condition node parser
     */
    BooleanNode parseConditionNode(Scanner s){
        //looking for "and" "(" COND "," COND ")" rule (or similar) first
        if (s.hasNext("and") || s.hasNext("or")){
            String bool = s.next();
            require(OPENPAREN, "expecting open parentheses pattern for condition", s);
            BooleanNode cond1 = parseConditionNode(s);
            require(",", "expecting comma", s);
            BooleanNode cond2 = parseConditionNode(s);
            require(CLOSEPAREN, "expecting close parentheses pattern for condition", s);
            return new ConditionNode(bool, cond1, cond2, null, null, null);
        } else if (s.hasNext("not")){ //looking for "not" "(" COND ")" rule
            String bool = s.next();
            require(OPENPAREN, "expecting open parentheses pattern for condition", s);
            BooleanNode cond1 = parseConditionNode(s);
            require(CLOSEPAREN, "expecting close parentheses pattern for condition", s);
            return new ConditionNode(bool, cond1, null, null, null, null);
        } else if (s.hasNext("eq") || s.hasNext("gt") || s.hasNext("lt")){//looking for RELOP "(" EXPR "," EXPR ") node
            BooleanNode relop = parseRelOpNode(s);
            require(OPENPAREN, "expecting open parentheses pattern for condition", s);
            IntNode expr1 = parseExprNode(s);
            require(",", "expecting comma", s);
            IntNode expr2 = parseExprNode(s);
            require(CLOSEPAREN, "expecting close parentheses pattern for condition", s);
            return new ConditionNode(null, null, null, relop, expr1, expr2);
        }
        fail("no condition node found", s);
        return null;
    }
    /**
     * relop parser (for eq, lt, gt)
     */
    BooleanNode parseRelOpNode(Scanner s){
        if (s.hasNext("eq")){
            return new RelOpNode(parseEqualsNode(s));
        } else if (s.hasNext("lt")) {
            return new RelOpNode(parseLessThanNode(s));
        } else if (s.hasNext("gt")){
            return new RelOpNode(parseGreaterThanNode(s));
        }
        fail("'eq', 'lt', or 'gt' not found", s);
        return null;
    }

    /**
     * equals node parser
     */
    BooleanNode parseEqualsNode (Scanner s){
        require("eq", "expecting 'eq'", s);
        return new EqualsNode("eq");
    }
    /**
     * less than parser
     */
    BooleanNode parseLessThanNode(Scanner s){
        require("lt", "expecting 'lt'", s);
        return new LessThanNode("lt");
    }
    /**
     * greater than parser
     */
    BooleanNode parseGreaterThanNode(Scanner s){
        require("gt", "expecting 'gt'", s);
        return new GreaterThanNode("gt");
    }
    /**
     * sensor node parser
     */
    IntNode parseSensorNode(Scanner s){
        if (s.hasNext("fuelLeft"))          {return new SensorNode(parseFuelLeftNode(s));}
        else if (s.hasNext("oppLR"))        {return new SensorNode(parseOppLRNode(s));}
        else if (s.hasNext("oppFB"))        {return new SensorNode(parseOppFBNode(s));}
        else if (s.hasNext("numBarrels"))   {return new SensorNode(parseNumBarrelsNode(s));}
        else if (s.hasNext("barrelLR"))     {return new SensorNode(parseBarrelLRNode(s));}
        else if (s.hasNext("barrelFB"))     {return new SensorNode(parseBarrelFBNode(s));}
        else if (s.hasNext("wallDist"))     {return new SensorNode(parseWallDistNode(s));}
        fail("No sensor node detected", s);
        return null;
    }
    /**
     * fuel left parser
     */
    IntNode parseFuelLeftNode(Scanner s){
        require("fuelLeft", "expecting 'fuelLeft'", s);
        return new FuelLeftNode("fuelLeft");
    }
    /**
     * opposite robot's left-right coordinates parser
     */
    IntNode parseOppLRNode(Scanner s){
        require("oppLR", "expecting 'fuelLeft'", s);
        return new OppLRNode("oppLR");
    }
    /**
     * opposite robot's front-back coordinates parser
     */
    IntNode parseOppFBNode(Scanner s){
        require("oppFB", "expecting 'oppFB'", s);
        return new OppFBNode("oppFB");
    }
    /**
     * number of barrels parser
     */
    IntNode parseNumBarrelsNode(Scanner s){
        require("numBarrels", "expecting 'numBarrels'", s);
        return new NumBarrelNode("numBarrels");
    }
    /**
     * closest barrel's left-right coordinates parser
     */
    IntNode parseBarrelLRNode(Scanner s){
        require("barrelLR", "expecting 'barrelLR'", s);
        return new BarrelLRNode("barrelLR");
    }

    /**
     * closest barrel's front-back coordinates parser
     */
    IntNode parseBarrelFBNode(Scanner s){
        require("barrelFB", "expecting 'barrelFB'", s);
        return new BarrelFBNode("barrelFB");
    }
    /**
     * coordinates of the wall in front of robot parser
     */
    IntNode parseWallDistNode(Scanner s){
        require("wallDist", "expecting 'wallDist'", s);
        return new WallDistanceNode("wallDist");
    }
    /**
     * number parser
     */
    IntNode parseNumberNode(Scanner s){
        int n = requireInt(NUMPAT, "expecting integer", s);
        return new NumberNode(n);
    }

    /**
     * Expr parser
     */
    IntNode parseExprNode (Scanner s){
        if(s.hasNext(NUMPAT)){ //checks if the expression has a number node
            return new ExprNode(parseNumberNode(s), null, null);
        } else if (s.hasNext("fuelLeft") || s.hasNext("oppLR") || s.hasNext("oppFB") ||
                s.hasNext("numBarrels") || s.hasNext("barrelLR") || s.hasNext("barrelFB") ||
                s.hasNext("wallDist")){ //yeah i know there's probably a better way to check if this is a sensor node
            return new ExprNode(parseSensorNode(s), null, null);
        } else if (s.hasNext("add") || s.hasNext("sub") || s.hasNext("mul") ||
                s.hasNext("div")){ //checks for OP node
            IntNode opNode = parseOpNode(s);
            require(OPENPAREN, "expecting open parentheses", s);
            IntNode expr1 = parseExprNode(s);
            require(",", "expecting comma", s);
            IntNode expr2 = parseExprNode(s);
            require(CLOSEPAREN, "expecting close parentheses", s);
            return new ExprNode(opNode, expr1, expr2);
        }
        fail("EXPR node not found", s);
        return null;
    }

    /**
     * Op node parser
     */
    IntNode parseOpNode(Scanner s){
        if (s.hasNext("add")){
            return new OpNode(parseAddNode(s));
        } else if (s.hasNext("sub")){
            return new OpNode(parseSubNode(s));
        } else if (s.hasNext("mul")){
            return new OpNode(parseMulNode(s));
        } else if (s.hasNext("div")){
            return new OpNode(parseDivNode(s));
        }
        fail("No operation node found", s);
        return null;
    }

    /**
     * Add parser
     */
    IntNode parseAddNode(Scanner s){
        require("add", "expecting 'add'", s);
        return new AddNode("add");
    }

    /**
     * Sub parser
     */
    IntNode parseSubNode(Scanner s){
        require("sub", "expecting 'sub'", s);
        return new SubtractNode("sub");
    }

    /**
     * Mul parser
     */
    IntNode parseMulNode(Scanner s){
        require("mul", "expecting 'mul'", s);
        return new MultiplyNode("mul");
    }

    /**
     * Div parser
     */
    IntNode parseDivNode(Scanner s){
        require("div", "expecting 'div'", s);
        return new DivideNode("div");
    }








    //----------------------------------------------------------------
    // utility methods for the parser
    // - fail(..) reports a failure and throws exception
    // - require(..) consumes and returns the next token as long as it matches the pattern
    // - requireInt(..) consumes and returns the next token as an int as long as it matches the pattern
    // - checkFor(..) peeks at the next token and only consumes it if it matches the pattern

    /**
     * Report a failure in the parser.
     */
    static void fail(String message, Scanner s) {
        String msg = message + "\n   @ ...";
        for (int i = 0; i < 5 && s.hasNext(); i++) {
            msg += " " + s.next();
        }
        throw new ParserFailureException(msg + "...");
    }

    /**
     * Requires that the next token matches a pattern if it matches, it consumes
     * and returns the token, if not, it throws an exception with an error
     * message
     */
    static String require(String p, String message, Scanner s) {
        if (s.hasNext(p)) {return s.next();}
        fail(message, s);
        return null;
    }

    static String require(Pattern p, String message, Scanner s) {
        if (s.hasNext(p)) {return s.next();}
        fail(message, s);
        return null;
    }

    /**
     * Requires that the next token matches a pattern (which should only match a
     * number) if it matches, it consumes and returns the token as an integer
     * if not, it throws an exception with an error message
     */
    static int requireInt(String p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {return s.nextInt();}
        fail(message, s);
        return -1;
    }

    static int requireInt(Pattern p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {return s.nextInt();}
        fail(message, s);
        return -1;
    }

    /**
     * Checks whether the next token in the scanner matches the specified
     * pattern, if so, consumes the token and return true. Otherwise returns
     * false without consuming anything.
     */
    static boolean checkFor(String p, Scanner s) {
        if (s.hasNext(p)) {s.next(); return true;}
        return false;
    }

    static boolean checkFor(Pattern p, Scanner s) {
        if (s.hasNext(p)) {s.next(); return true;} 
        return false;
    }

}





// You could add the node classes here or as separate java files.
// (if added here, they must not be declared public or private)
// For example:
//  class BlockNode implements ProgramNode {.....
//     with fields, a toString() method and an execute() method
//

/**
 * CLASSES FOR ALL NODES
 */

/**
 * Move node
 */

class MoveNode implements ProgramNode {
    final String name;
    final IntNode expr; //null if no arguments

    public MoveNode (String n, IntNode e){
        this.name = n;
        this.expr = e;
    }
    public void execute (Robot robot){
        if (expr != null){ //if the move node has an argument
            for (int i = 0; i >= expr.evaluate(robot); i++){
                robot.move(); //this parses completely fine but doesnt execute. i dont know why.
            }
        } else {
            robot.move();
        }
    }

    @Override
    public String toString() {
        if (expr != null){
            return "move(" + expr.toString() + ");";
        }
        return "move;";
    }
}

/**
 * Turn Left Node
 */
class TurnLNode implements ProgramNode{
    final String name;

    public TurnLNode (String n){
        this.name = n;
    }
    @Override
    public void execute(Robot robot) {
        robot.turnLeft();
    }

    @Override
    public String toString() {
        return "turnL;";
    }
}

/**
 * Turn Right Node
 */

class TurnRNode implements ProgramNode{
    final String name;

    public TurnRNode (String n){
        this.name = n;
    }
    @Override
    public void execute(Robot robot) {
        robot.turnRight();
    }

    @Override
    public String toString() {
        return "turnR;";
    }
}

/**
 * Take Fuel Node
 */

class TakeFuelNode implements ProgramNode{
    final String name;

    public TakeFuelNode (String n){
        this.name = n;
    }

    @Override
    public void execute(Robot robot) {
        robot.takeFuel();
    }

    @Override
    public String toString() {
        return "takeFuel;";
    }
}

/**
 * Wait node
 */
class WaitNode implements ProgramNode{

    final String name;
    final IntNode expr;

    public WaitNode (String n, IntNode e){
        this.name = n;
        this.expr = e;
    }
    @Override
    public void execute(Robot robot) {
        if (expr != null){
            for (int i = expr.evaluate(robot); i <= 0; i--){
                robot.idleWait();
            }
        } else {
            robot.idleWait();
        }
    }

    @Override
    public String toString() {
        if (expr != null){
            return "wait(" + expr.toString() + ");";
        }
        return "wait;";
    }
}

/**
 * Loop node
 */

class LoopNode implements ProgramNode{
    final ProgramNode block; //Stores the block node

    public LoopNode (ProgramNode b){
        this.block = b;
    }

    @Override
    public void execute(Robot robot) {
        for (int i = 0; i <= 50; i++){
            this.block.execute(robot);
        }
    }

    @Override
    public String toString() {
        return "loop {\n" + this.block.toString() + "\n}";
    }
}

/**
 * Block Node
 */
class BlockNode implements ProgramNode{
    final ProgramNode statement; //stores the following statement

    public BlockNode (ProgramNode s) { this.statement = s; }

    @Override
    public void execute(Robot robot) {
        this.statement.execute(robot);
    }

    @Override
    public String toString() {
        return this.statement.toString();
    }
}

/**
 * Shield On node
 */

class ShieldOnNode implements ProgramNode{
    final String name;

    public ShieldOnNode (String n){
        this.name = n;
    }

    @Override
    public void execute(Robot robot) {
        robot.setShield(true);
    }

    @Override
    public String toString() {
        return "shieldOn;";
    }
}

/**
 * Shield off node
 */

class ShieldOffNode implements ProgramNode{
    final String name;

    public ShieldOffNode(String n){
        this.name = n;
    }

    @Override
    public void execute(Robot robot) {
        robot.setShield(false);
    }

    @Override
    public String toString() {
        return "shieldOff";
    }
}

/**
 * Turn around node
 */

class TurnAroundNode implements ProgramNode{
    final String name;

    public TurnAroundNode (String n){
        this.name = n;
    }

    @Override
    public void execute(Robot robot) {
        robot.turnAround();
    }

    @Override
    public String toString() {
        return "turnAround;";
    }
}

/**
 * Statement node
 */
class StatementNode implements ProgramNode{
    final List<ProgramNode> nodes; //the list of nodes contained within the statement node

    public StatementNode(List<ProgramNode> nds){
        this.nodes = nds;
    }

    public String toString(){
        if (nodes.isEmpty()){
            return null;
        }
        StringBuilder list = new StringBuilder("");
        list.append(nodes.get(0));
        for (int i = 1; i<nodes.size(); i++){
           list.append("\n").append(nodes.get(i));
        }
        return list.toString();
    }

    @Override
    public void execute(Robot robot) {
        for (ProgramNode node: nodes) {
            node.execute(robot);
        }
    }
}

/**
 * If Node
 */
class IfNode implements ProgramNode {
    final BooleanNode cond;
    final ProgramNode block1;
    final ProgramNode block2;

    public IfNode(BooleanNode c, ProgramNode b1, ProgramNode b2){
        this.cond = c;
        this.block1 = b1;
        this.block2 = b2;
    }

    @Override
    public String toString() {
        if (this.block2 != null){
            return "if (" + this.cond.toString() + ") {\n" + this.block1.toString() + "\n} else {\n" + this.block2.toString() + "\n}";
        }
        return "if (" + this.cond.toString() + ") {\n" + this.block1.toString() + "\n}";
    }

    public void execute(Robot robot) {
        if (this.cond.evaluate(robot)){
            this.block1.execute(robot);
        } else if (this.block2 != null){
            this.block2.execute(robot);
        }
    }
}

/**
 * While Node
 */
class WhileNode implements ProgramNode {
    final BooleanNode cond;
    final ProgramNode block;

    public WhileNode(BooleanNode c, ProgramNode b){
        this.cond = c;
        this.block = b;
    }

    @Override
    public String toString() {
        return "while (" + this.cond.toString() + ") {\n" + this.block.toString() + "\n}";
    }

    @Override
    public void execute(Robot robot) {
        while (this.cond.evaluate(robot)){
            this.block.execute(robot);
        }
    }
}

class ConditionNode implements BooleanNode{
    final String bool; //contains "and", "or", "not"; will be null if node follows rule RELOP "(" EXPR "," EXPR ")
    final BooleanNode cond1; //will be null if node follows rule RELOP "(" EXPR "," EXPR ")
    final BooleanNode cond2; //will be null if node follows rule RELOP "(" EXPR "," EXPR ") AND if node follows rule "not" "(" COND ")"
    final BooleanNode relop; //will be null if node follows rule "and" "(" COND "," COND ")" and similar
    final IntNode expr1; //will be null if node follows rule "and" "(" COND "," COND ")" and similar
    final IntNode expr2; //will be null if node follows rule "and" "(" COND "," COND ")" and similar

    public ConditionNode (String b, BooleanNode c1, BooleanNode c2, BooleanNode r, IntNode e1, IntNode e2){
        this.bool = b;
        this.cond1 = c1;
        this.cond2 = c2;
        this.relop = r;
        this.expr1 = e1;
        this.expr2 = e2;
    }

    @Override
    public String toString() {
        if (bool != null && cond1 != null && cond2 != null){ //if the node follows the "and" "(" COND "," COND ")" rule (or similar)
            return bool + "(" + cond1.toString() + ", " + cond2.toString() + ")";
        } else if (bool != null && cond1 != null && cond2 == null){ //if the node follows the rule "not" "(" COND ")"
            return bool + "(" + cond1.toString() + ")";
        }
        return relop.toString() + "(" + expr1.toString() + ", " + expr2.toString() + ")"; //for the RELOP "(" EXPR "," EXPR ") rule
    }

    @Override
    public Boolean evaluate(Robot robot) {
        if (bool != null && cond1 != null && cond2 != null){ //if the node follows the "and" "(" COND "," COND ")" rule (or similar)
           if (bool.equals("and")){
               if (cond1.evaluate(robot) && cond2.evaluate(robot)) {
                   return true;
               }
           } else if (bool.equals("or")){
               if (cond1.evaluate(robot) || cond2.evaluate(robot)) {
                   return true;
               }
           }
        } else if (bool != null && cond1 != null && cond2 == null){ //if the node follows the rule "not" "(" COND ")"
            if (bool.equals("not")){ //just double checking owo
                if (!cond1.evaluate(robot)){
                    return true;
                }
            }
        }
        //if the node follows the RELOP "(" EXPR "," EXPR ") rule
        if (relop != null && expr1 != null && expr2 != null){
            if (relop.toString().equals("lt")){
                return expr1.evaluate(robot) < expr2.evaluate(robot);
            } else if (relop.toString().equals("gt")){
                return expr2.evaluate(robot) < expr1.evaluate(robot);
            } else if (relop.toString().equals("eq")){
                return expr1.evaluate(robot) == expr2.evaluate(robot);
            }
        }
        return false;
    }
}

/**
 * RelOp node - contains either a "less than", "greater than", or "equals" node.
 */
class RelOpNode implements BooleanNode {
    final BooleanNode relop;

    public RelOpNode(BooleanNode r){
        this.relop = r;
    }

    @Override
    public String toString() {
        return relop.toString();
    }

    @Override
    public Boolean evaluate(Robot robot) {
        return relop.evaluate(robot);
    }
}
/**
 * Less Than Node
 */
class LessThanNode implements BooleanNode {
    final String name;

    public LessThanNode (String n){
        this.name = n;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Boolean evaluate(Robot robot) {
        return true;
    }
}
/**
 * Greater Than Node
 */
class GreaterThanNode implements BooleanNode {
    final String name;

    public GreaterThanNode (String n){
        this.name = n;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Boolean evaluate(Robot robot) {
        return true;
    }
}
/**
 * Equals Node
 */
class EqualsNode implements BooleanNode {
    final String name;

    public EqualsNode (String n){
        this.name = n;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Boolean evaluate(Robot robot) {
        return true;
    }
}
/**
 * Sensor Node
 */
class SensorNode implements IntNode {
    final IntNode method; //stores each of the methods in the Robot class that requires calling to evaluate integers
    // (everything below this class basically)

    public SensorNode (IntNode m){
        this.method = m;
    }

    @Override
    public String toString() {
        return method.toString();
    }

    @Override
    public int evaluate(Robot robot) {
        return method.evaluate(robot);
    }
}

/**
 * Node to return the amount of fuel left
 */
class FuelLeftNode implements IntNode {
    final String fuelLeft;

    public FuelLeftNode (String fl){
        this.fuelLeft = fl;
    }

    @Override
    public String toString() {
        return fuelLeft;
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getFuel();
    }
}

/**
 * Node to return the left-right coordinates of the opponent robot
 */
class OppLRNode implements IntNode {
    final String oppLr;

    public OppLRNode (String olr){
        this.oppLr = olr;
    }

    @Override
    public String toString() {
        return oppLr;
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getOpponentLR();
    }
}
/**
 * Node to return the front-back coordinates of the opponent robot
 */
class OppFBNode implements IntNode {
    final String oppFb;

    public OppFBNode (String ofb){
        this.oppFb = ofb;
    }

    @Override
    public String toString() {
        return oppFb;
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getOpponentFB();
    }
}
/**
 * Node to return the number of barrels left in the world
 */
class NumBarrelNode implements IntNode {
    final String numBarrel;

    public NumBarrelNode (String nb){
        this.numBarrel = nb;
    }

    @Override
    public String toString() {
        return numBarrel;
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.numBarrels();
    }
}

/**
 * Node to return the left-right location of the closest barrel
 */
class BarrelLRNode implements IntNode {
    final String barrelLR;

    public BarrelLRNode (String blr){
        this.barrelLR = blr;
    }

    @Override
    public String toString() {
        return barrelLR;
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getClosestBarrelLR();
    }
}
/**
 * Node to return the front-back location of the closest barrel
 */
class BarrelFBNode implements IntNode {
    final String barrelFB;

    public BarrelFBNode (String bfb){
        this.barrelFB = bfb;
    }

    @Override
    public String toString() {
        return barrelFB;
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getClosestBarrelFB();
    }
}
/**
 * Node to return the distance to the wall directly in front of robot
 */
class WallDistanceNode implements IntNode {
    final String wallDist;

    public WallDistanceNode (String wd){
        this.wallDist = wd;
    }

    @Override
    public String toString() {
        return wallDist;
    }

    @Override
    public int evaluate(Robot robot) {
        return robot.getDistanceToWall();
    }
}

/**
 * Number Node
 */
class NumberNode implements IntNode {
    final int number;

    public NumberNode (int n){
        this.number = n;
    }

    @Override
    public String toString() {
        return "" + number;
    }

    @Override
    public int evaluate(Robot robot) {
        return number;
    }
}

/**
 * Op Node
 */
class OpNode implements IntNode {
    final IntNode operation;

    public OpNode (IntNode op){ this.operation = op;}

    public String toString() { return operation.toString();}

    @Override
    public int evaluate(Robot robot) {
        return 0;
    }
}

/**
 * Add Node
 */
class AddNode implements IntNode {
    final String add;

    public AddNode (String a){this.add = a;}

    @Override
    public String toString() {
        return "add";
    }

    @Override
    public int evaluate(Robot robot) {
        return 0;
    }
}

/**
 * Subtract Node
 */
class SubtractNode implements IntNode {
    final String subtract;

    public SubtractNode (String s){this.subtract = s;}

    @Override
    public String toString() {
        return "sub";
    }

    @Override
    public int evaluate(Robot robot) {
        return 0;
    }
}

/**
 * Multiply Node
 */
class MultiplyNode implements IntNode {
    final String multiply;

    public MultiplyNode (String m){this.multiply = m;}

    @Override
    public String toString() {
        return "mul";
    }

    @Override
    public int evaluate(Robot robot) {
        return 0;
    }
}

/**
 * Divide Node
 */
class DivideNode implements IntNode {
    final String divide;

    public DivideNode (String d){this.divide = d;}

    @Override
    public String toString() {
        return "div";
    }

    @Override
    public int evaluate(Robot robot) {
        return 0;
    }
}

/**
 * Expr Node
 */
class ExprNode implements IntNode {
    final IntNode node;
    final IntNode expr1;
    final IntNode expr2;

    //e1 and e2 will be NULL if node is not an OpNode
    public ExprNode (IntNode n, IntNode e1, IntNode e2){this.node = n; this.expr1 = e1; this.expr2 = e2;}

    @Override
    public String toString() {
        if ((node.toString().equals("add") || node.toString().equals("sub") ||
                node.toString().equals("mul") || node.toString().equals("div")) && this.expr2 != null
                && this.expr1 != null){ //checks if the IntNode is an OP node
            return node.toString() + "(" + expr1.toString() + "," + expr2.toString() + ")";
        }
        return node.toString(); //returns string for both sensor node and int node
    }

    @Override
    public int evaluate(Robot robot) {
        if (node.toString().equals("add") && expr1 != null && expr2 != null){
            return expr1.evaluate(robot) + expr2.evaluate(robot);
        } else if (node.toString().equals("sub") && expr1 != null && expr2 != null){
            return expr1.evaluate(robot) - expr2.evaluate(robot);
        } else if (node.toString().equals("mul") && expr1 != null && expr2 != null){
            return expr1.evaluate(robot) * expr2.evaluate(robot);
        } else if (node.toString().equals("div") && expr1 != null && expr2 != null){
            return expr1.evaluate(robot) / expr2.evaluate(robot);
        }
        return node.evaluate(robot);
    }
}