package semantic;


import java.util.ArrayList;
import java.util.HashMap;

import syntaxtree.BinOp;
import syntaxtree.CJumpStmt;
import syntaxtree.Call;
import syntaxtree.ErrorStmt;
import syntaxtree.Exp;
import syntaxtree.HAllocate;
import syntaxtree.HLoadStmt;
import syntaxtree.HStoreStmt;
import syntaxtree.IntegerLiteral;
import syntaxtree.JumpStmt;
import syntaxtree.Label;
import syntaxtree.MoveStmt;
import syntaxtree.NoOpStmt;
import syntaxtree.Operator;
import syntaxtree.PrintStmt;
import syntaxtree.Procedure;
import syntaxtree.SimpleExp;
import syntaxtree.Stmt;
import syntaxtree.StmtExp;
import syntaxtree.StmtList;
import syntaxtree.Temp;
import visitor.GJVoidDepthFirst;
import syntaxtree.Goal;


public class ClassesCheck extends GJVoidDepthFirst<String>{

	
	ArrayList<String> instr = new ArrayList<String>();
	ArrayList<String> var = new ArrayList<String>();
	ArrayList<String> next = new ArrayList<String>();
	ArrayList<String> varMove = new ArrayList<String>();
	ArrayList<String> constMove = new ArrayList<String>();
	ArrayList<String> varUse = new ArrayList<String>();
	ArrayList<String> varDef = new ArrayList<String>();
	HashMap<String,Integer>cjumps = new HashMap<String,Integer>();
	HashMap<String,Integer>jumps = new HashMap<String,Integer>();
	int i_counter = 0;
	boolean Def = false;
	boolean jump = false;
	int Const = -1;
	String expr = "";
	public ArrayList<String> getInstr() {
		return instr;
	}
	public ArrayList<String> getVar() {
		return var;
	}
	public ArrayList<String> getNext() {
		return next;
	}
	public ArrayList<String> getVarMove() {
		return varMove;
	}
	public ArrayList<String> getConstMove() {
		return constMove;
	}
	public ArrayList<String> getVarUse() {
		return varUse;
	}
	public ArrayList<String> getVarDef() {
		return varDef;
	}

   /**
    * f0 -> "MAIN"
    * f1 -> StmtList()
    * f2 -> "END"
    * f3 -> ( Procedure() )*
    * f4 -> <EOF>
    */
   public void visit(Goal n, String _) throws Exception {
	  String methodName = "MAIN";
      n.f0.accept(this, methodName);
      n.f1.accept(this, methodName);
      n.f2.accept(this, methodName);
      n.f3.accept(this, methodName);
      n.f4.accept(this, methodName);
   }

   /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public void visit(StmtList n, String methodName) throws Exception {
	  System.out.println("StmtList");
      n.f0.accept(this, methodName);
   }

   /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
   public void visit(Procedure n, String _) throws Exception {
	  String methodName;
      methodName = n.f0.f0.toString();
      n.f1.accept(this, methodName);
      n.f2.accept(this, methodName);
      n.f3.accept(this, methodName);
      n.f4.accept(this, methodName);
   }

   /**
    * f0 -> NoOpStmt()
    *       | ErrorStmt()
    *       | CJumpStmt()
    *       | JumpStmt()
    *       | HStoreStmt()
    *       | HLoadStmt()
    *       | MoveStmt()
    *       | PrintStmt()
    */
   public void visit(Stmt n, String methodName) throws Exception {
	  jump=false;
      n.f0.accept(this, methodName);
      if (jump ==false)
    	  next.add("next(\""+methodName+"\", "+i_counter+", "+(i_counter+1)+").");
   }

   /**
    * f0 -> "NOOP"
    */
   public void visit(NoOpStmt n, String methodName) throws Exception {
      n.f0.accept(this, methodName);
      jump=true;
   }

   /**
    * f0 -> "ERROR"
    */
   public void visit(ErrorStmt n, String methodName) throws Exception {
      n.f0.accept(this, methodName);
      jump=true;
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
   public void visit(CJumpStmt n, String methodName) throws Exception {
	  String temp;
	  expr = "CJUMP ";
      n.f0.accept(this, methodName);
      n.f1.accept(this, methodName);
      temp = expr;
      expr = "";
      n.f2.accept(this, methodName);
      cjumps.put(expr,i_counter);
      temp +=expr;
      
	  instr.add("instruction(\""+methodName+"\", "+ ++i_counter+", \""+temp+"\").");
	  jump=false;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public void visit(JumpStmt n, String methodName) throws Exception {
	  String temp;
	  temp = "JUMP ";	 
	  expr = "";
      n.f0.accept(this, methodName);
      n.f1.accept(this, methodName);
      jumps.put(expr,i_counter);
      temp +=expr;
      instr.add("instruction(\""+methodName+"\", "+ ++i_counter+", \""+temp+"\").");
      jump=false;
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Temp()
    * f2 -> IntegerLiteral()
    * f3 -> Temp()
    */
   public void visit(HStoreStmt n, String methodName) throws Exception {
	  instr.add("instruction(\""+methodName+"\", "+ ++i_counter+", \"HSTORE TEMP " +n.f1.f1.f0.toString()+" "+n.f2.f0.toString() + " TEMP "+n.f3.f1.f0.toString()+"\").");
      n.f0.accept(this, methodName);
      Def =true;
      n.f1.accept(this, methodName);
    //  System.out.println("varDef(\""+methodName+"\", "+i_counter+", \"TEMP "+n.f1.f1.f0.toString()+"\")");
   //   varUse.add("varUse(\""+methodName+"\", "+i_counter+", \"TEMP "+n.f1.f1.f0.toString()+"\")");
      n.f2.accept(this, methodName);
      n.f3.accept(this, methodName);
      varDef.add("varDef(\""+methodName+"\", "+i_counter+", \"TEMP "+n.f3.f1.f0.toString()+"\").");
      jump=false;
    //  System.out.println(instr);
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Temp()
    * f3 -> IntegerLiteral()
    */
   public void visit(HLoadStmt n, String methodName) throws Exception {
	  varDef.add("varDef(\""+methodName+"\", "+i_counter+", \"TEMP "+n.f1.f1.f0.toString()+"\")."); 
	  var.add("var(\""+methodName+"\", \"TEMP "+n.f1.f1.f0.toString()+"\").");
	//  varUse.add("varUse(\""+methodName+"\", "+i_counter+", \"TEMP "+n.f2.f1.f0.toString()+"\")"); 

	  
	  instr.add("instruction(\""+methodName+"\", "+ ++i_counter+", \"HLOAD TEMP " +n.f1.f1.f0.toString() + " TEMP "+n.f2.f1.f0.toString()+" "+ n.f3.f0.toString()+"\").");
      n.f0.accept(this, methodName);
      Def = true;
      n.f1.accept(this, methodName);
      n.f2.accept(this, methodName);
      n.f3.accept(this, methodName);
      jump=false;
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
   public void visit(MoveStmt n, String methodName) throws Exception {
	  String temp = "";
      n.f0.accept(this, methodName);
      expr = "MOVE ";
      Def = true;
      n.f1.accept(this, methodName);
      temp = expr;
      expr= "";
      var.add("var(\""+methodName+"\", \"TEMP "+n.f1.f1.f0.toString()+"\").");
      varDef.add("varDef(\""+methodName+"\", "+i_counter+", \"TEMP "+n.f1.f1.f0.toString()+"\").");
      n.f2.accept(this, methodName);
      if (expr.length()>0)
    	  expr = expr.substring(0, expr.length()-1);
      
      if (Const==1)
    	  varMove.add("varMove(\""+methodName+"\", "+i_counter+ ",\"TEMP "+n.f1.f1.f0.toString()+"\", \""+expr+"\").");
      else if (Const==0)
    	  constMove.add("constMove(\""+methodName+"\", "+i_counter+ ",\"TEMP "+n.f1.f1.f0.toString()+"\", \""+expr+"\").");
      Const = -1;
      temp +=expr;
      System.out.println(temp);
      instr.add("instruction(\""+methodName+"\", "+ ++i_counter+", \""+temp+"\").");
      jump=false;
      
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public void visit(PrintStmt n, String methodName) throws Exception {
	  expr = "PRINT ";
      n.f0.accept(this, methodName);
      n.f1.accept(this, methodName);
      instr.add("instruction(\""+methodName+"\", "+ ++i_counter+", \""+expr+"\").");
      jump=false;
   }

   /**
    * f0 -> Call()
    *       | HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
   public void visit(Exp n, String methodName) throws Exception {
      n.f0.accept(this, methodName);
   }

   /**
    * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> SimpleExp()
    * f4 -> "END"
    */
   public void visit(StmtExp n, String methodName) throws Exception {
      n.f0.accept(this, methodName);
      n.f1.accept(this, methodName);
      n.f2.accept(this, methodName);
      n.f3.accept(this, methodName);
      n.f4.accept(this, methodName);
   }

   /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    * f2 -> "("
    * f3 -> ( Temp() )*
    * f4 -> ")"
    */
   public void visit(Call n, String methodName) throws Exception {
	  expr += "CALL ";
      n.f0.accept(this, methodName);
      n.f1.accept(this, methodName);
      expr +="( ";
      n.f2.accept(this, methodName);
      n.f3.accept(this, methodName);
      n.f4.accept(this, methodName);
      expr +=") ";
      Const = -1;
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
   public void visit(HAllocate n, String methodName) throws Exception {
	  expr += "HALLOCATE ";
      n.f0.accept(this, methodName);
      n.f1.accept(this, methodName);
      Const = -1;
   }

   /**
    * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
   public void visit(BinOp n, String methodName) throws Exception {
      n.f0.accept(this, methodName);
      n.f1.accept(this, methodName);
      n.f2.accept(this, methodName);
      Const = -1;
   }

   /**
    * f0 -> "LT"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    */
   public void visit(Operator n, String methodName) throws Exception {
	  expr += n.f0.choice.toString()+ " ";
      n.f0.accept(this, methodName);
   }

   /**
    * f0 -> Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
   public void visit(SimpleExp n, String methodName) throws Exception {
      n.f0.accept(this, methodName);
   }

   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public void visit(Temp n, String methodName) throws Exception {
      n.f0.accept(this, methodName);
      expr += "TEMP ";
      n.f1.accept(this, methodName);
      if (Def)
    	  Def = false;
      else
    	  varUse.add("varUse(\""+methodName+"\", "+i_counter+", \"TEMP "+n.f1.f0.toString()+"\")"); 
    
      Const = 0;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public void visit(IntegerLiteral n, String methodName) throws Exception {
      n.f0.accept(this, methodName);
      expr += n.f0.toString()+" ";
      Const =1;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public void visit(Label n, String methodName) throws Exception {
	  Const = -1;
	  expr +=n.f0.toString()+ " ";
      n.f0.accept(this, methodName);
      jump=true;
   }

	
	
}