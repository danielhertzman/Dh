package dh.machineCode;

import dh.grammar.DhBaseListener;
import dh.grammar.DhParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;

/**
 * Created by Daniel Hertzman-Ericson on 2016-03-09.
 */
public class Compiler extends DhBaseListener {
    private final String infnam;
    private final boolean traceOn;
    private final HackGen out;
    private final HashMap<String, Integer> varAddr = new HashMap<String, Integer>();
    private final Stack<Integer> addrStack = new Stack<Integer>();

    public static final int SCREEN = 16384;

    public Compiler(String infnam, HackGen out, boolean traceOn){
        this.infnam = infnam;
        this.out = out;
        this.traceOn = traceOn;
    }

    private void tracePrint(String message) {
        if (traceOn)
            System.out.println("At operation " + out.currentCodeAddress() + ": " + message);
    }

    private int getVarAddr(Token tok) {
        String name = tok.getText();
        Integer a = varAddr.get(name);

        if (a == null) {
            return 0;
        } else {
            return a;
        }
    }

    private void error(int line, String msg) {
        System.err.println(infnam + ":" + line + ": " + msg);
    }

//    @Override
//    public void enterCode(DhParser.CodeContext ctx) {
//        tracePrint("Initialize SP");
//        out.emitInitSP();
//    }
//
//    @Override
//    public void enterDecl(DhParser.DeclContext ctx) {
//        String name = ctx.ID().getText();
//        int addr = out.newVarAddr();
//        Integer old = varAddr.put(name, addr);
//        if (old != null) {
//            error(ctx.ID().getSymbol().getLine(), "redefined " + name);
//        }
//    }
//
//    @Override
//    public void exitAssign(DhParser.AssignContext ctx) {
//        int a = getVarAddr(ctx.ID().getSymbol());
//        tracePrint("Pop from stack and put in "+a);
//        out.emitPopD();
//        out.emitAInstr(a);
//        out.emitCInstr(HackGen.DestM, HackGen.CompD, 0);
//    }
//
//
//    @Override
//    public void exitAdd(DhParser.AddContext ctx) {
//        ParseTree operator = ctx.getChild(1); // the second token, if it's there, is the operator
//        if (operator != null && "+".equals(operator.getText())) { // if it's plus, this is an addition
//            // Add the top two numbers on the stack, leaving only the sum.
//            tracePrint("Add top two numbers on the stack, leaving the sum");
//            out.emitGetTwoOperands();         // Get operands.
//            out.emitCInstr(HackGen.DestD, HackGen.DPlusM, 0); // Add them.
//            out.emitReplaceTopWithD();        // Replace top of stack with sum.
//        } else {
//            // No operator we know, so it must be a lone term. Just leave it on the stack.
//        }
//    }

    @Override
    public void enterAtomExpr(DhParser.AtomExprContext ctx) {
        if (ctx.ID() != null) {
            int a = getVarAddr(ctx.ID().getSymbol());
            tracePrint("Push contents of "+a+" on stack");
            out.emitAInstr(a);
            out.emitCInstr(HackGen.DestD, HackGen.CompM, 0);
            out.emitPushD();
        } else if (ctx.INT() != null) {
            int i = Integer.parseInt(ctx.INT().getText());
            tracePrint("Push "+i+" on stack");
            out.emitAInstr(i);
            out.emitCInstr(HackGen.DestD, HackGen.CompA, 0);
            out.emitPushD();
        }
    }

    @Override
    public void exitLoop(DhParser.LoopContext ctx) {

    }
    @Override
    public void exitPrint(DhParser.PrintContext ctx) {

        out.emitPopD();
        out.emitAInstr(SCREEN);
        out.emitCInstr(HackGen.DestM, HackGen.CompD, 0);
    }


}