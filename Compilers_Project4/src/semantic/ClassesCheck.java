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
	HashMap<String,Integer>labels = new HashMap<String,Integer>();
	int i_counter = 0;
	boolean Def = false;
	boolean labeled=false;
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
      next.remove(next.size()-1);
      n.f2.accept(this, methodName);
      n.f3.accept(this, methodName);
      n.f4.accept(this, methodName);
   }

   /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public void visit(StmtList n, String methodName) throws Exception {

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
	  expr = "";
      methodName = n.f0.f0.toString();
      i_counter = 0;
      n.f1.accept(this, methodName);
      n.f2.accept(this, methodName);
      n.f3.accept(this, methodName);
      expr = "";
      n.f4.accept(this, methodName);
  //    next.remove(next.size()-1);
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
	  String label="",inst="";
	  label = expr;
	  
	  label = expr;

	  jump=false;
	  if (labeled==true){
	      findLabel(expr,methodName);
	      if (expr.length()>0)
	    	  expr = expr.substring(0, expr.length()-1);      
	      if (!cjumps.containsKey(expr) && !jumps.containsKey(expr)){
	    	  
	    	  labels.put(expr, i_counter+1);
	      }
	      jump = true;
	  }
	  expr="";	 
      n.f0.accept(this, methodName);
 
      if (!label.equals(""))
    	  inst = label+" "+expr;
      else
    	  inst  = expr;
      instr.add("instruction(\""+methodName+"\", "+ i_counter+", \""+inst+"\").");
      if (jump==false)
    	  next.add("next(\""+methodName+"\", "+i_counter+", "+(i_counter+1)+").");
      expr = "";
      
      labeled = false;
   }

   /**
    * f0 -> "NOOP"
    */
   public void visit(NoOpStmt n, String methodName) throws Exception {
	  
      n.f0.accept(this, methodName);
      String temp = "NOOP";
      i_counter++;
     // instr.add("instruction(\""+methodName+"\", "+ ++i_counter+", \""+temp+"\").");
      expr = temp;
      jump=false;
   }

   /**
    * f0 -> "ERROR"
    */
   public void visit(ErrorStmt n, String methodName) throws Exception {
      n.f0.accept(this, methodName);
      String temp = "ERROR";
      i_counter++;
     // instr.add("instruction(\""+methodName+"\", "+ ++i_counter+", \""+temp+"\").");
      
      expr = temp;
      jump=false;
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
   public void visit(CJumpStmt n, String methodName) throws Exception {
	  String temp;
	  expr = "CJUMP "; 
	  i_counter++;
      n.f0.accept(this, methodName);
      n.f1.accept(this, methodName);
      temp = expr;
      expr = "";
      n.f2.accept(this, methodName);
      if (expr.length()>0)
    	  expr = expr.substring(0, expr.length()-1);      

      temp +=expr;
     
	//  instr.add("instruction(\""+methodName+"\", "+ ++i_counter+", \""+temp+"\").");
	  cjumps.put(expr,i_counter);
	  expr = temp;
	  jump=false;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public void visit(JumpStmt n, String methodName) throws Exception {
	  String temp,element="";
	  temp = "JUMP ";	 
	  expr = "";
	  i_counter++;
      n.f0.accept(this, methodName);
      n.f1.accept(this, methodName);
      if (expr.length()>0)
    	  expr = expr.substring(0, expr.length()-1);      
      
      temp +=expr;
     
  //    instr.add("instruction(\""+methodName+"\", "+ ++i_counter+", \""+temp+"\").");
      if (labels.containsKey(expr)){
    	 int counter = labels.get(expr);
		 element = "next(\""+methodName+"\", "+i_counter+", "+counter+").";
		 next.add(element);	
		 jump = true;
      }
      else
    	  jump=false; 
      jumps.put(expr,i_counter);
      expr = temp;
     
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Temp()
    * f2 -> IntegerLiteral()
    * f3 -> Temp()
    */
   public void visit(HStoreStmt n, String methodName) throws Exception {
	   
	  //instr.add("instruction(\""+methodName+"\", "+ ++i_counter+", \"HSTORE TEMP " +n.f1.f1.f0.toString()+" "+n.f2.f0.toString() + " TEMP "+n.f3.f1.f0.toString()+"\").");
      i_counter++;
	  n.f0.accept(this, methodName);
  //    Def =true;
      n.f1.accept(this, methodName);
    //  System.out.println("varDef(\""+methodName+"\", "+i_counter+", \"TEMP "+n.f1.f1.f0.toString()+"\")");
   //   varUse.add("varUse(\""+methodName+"\", "+i_counter+", \"TEMP "+n.f1.f1.f0.toString()+"\")");
      n.f2.accept(this, methodName);
  //    Def =true;
      n.f3.accept(this, methodName);
     // varDef.add("varDef(\""+methodName+"\", "+i_counter+", \"TEMP "+n.f3.f1.f0.toString()+"\").");
      jump=false;
      expr = "HSTORE TEMP " +n.f1.f1.f0.toString()+" "+n.f2.f0.toString() + " TEMP "+n.f3.f1.f0.toString();
    //  System.out.println(instr);
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Temp()
    * f3 -> IntegerLiteral()
    */
   public void visit(HLoadStmt n, String methodName) throws Exception {
	  
//	  var.add("var(\""+methodName+"\", \"TEMP "+n.f1.f1.f0.toString()+"\").");
	//  varUse.add("varUse(\""+methodName+"\", "+i_counter+", \"TEMP "+n.f2.f1.f0.toString()+"\")"); 

	  i_counter++;
	  varDef.add("varDef(\""+methodName+"\", "+i_counter+", \"TEMP "+n.f1.f1.f0.toString()+"\")."); 
	  
//	  instr.add("instruction(\""+methodName+"\", "+ ++i_counter+", \"HLOAD TEMP " +n.f1.f1.f0.toString() + " TEMP "+n.f2.f1.f0.toString()+" "+ n.f3.f0.toString()+"\").");
      n.f0.accept(this, methodName);
      Def = true;
      n.f1.accept(this, methodName);
      n.f2.accept(this, methodName);
      n.f3.accept(this, methodName);
      expr = "HLOAD TEMP " +n.f1.f1.f0.toString() + " TEMP "+n.f2.f1.f0.toString()+" "+ n.f3.f0.toString();
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
    //  var.add("var(\""+methodName+"\", \"TEMP "+n.f1.f1.f0.toString()+"\").");
      i_counter++;
      n.f2.accept(this, methodName);
      if (expr.length()>0)
    	  expr = expr.substring(0, expr.length()-1);
      
      if (Const==0)
    	  varMove.add("varMove(\""+methodName+"\", "+i_counter+ ",\"TEMP "+n.f1.f1.f0.toString()+"\", \""+expr+"\").");
      else if (Const==1)
    	  constMove.add("constMove(\""+methodName+"\", "+i_counter+ ",\"TEMP "+n.f1.f1.f0.toString()+"\", \""+expr+"\").");
      Const = -1;
      temp +=expr;
   //   System.out.println(temp);
      
      varDef.add("varDef(\""+methodName+"\", "+i_counter+", \"TEMP "+n.f1.f1.f0.toString()+"\").");
      expr = temp;
      jump=false;
      
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public void visit(PrintStmt n, String methodName) throws Exception {
	  expr = "PRINT ";
	  i_counter++;
      n.f0.accept(this, methodName);
      n.f1.accept(this, methodName);
    
    //  instr.add("instruction(\""+methodName+"\", "+ ++i_counter+", \""+expr+"\").");
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
      expr="";
      n.f2.accept(this, methodName);
      expr = "RETURN ";
      ++i_counter;
      
      n.f3.accept(this, methodName);  
      instr.add("instruction(\""+methodName+"\", "+ i_counter+", \""+expr+"\").");
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
	  expr = "CALL ";
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
	  expr = "HALLOCATE ";
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
      if (!var.contains("var(\""+methodName+"\", \"TEMP "+n.f1.f0.toString()+"\")."))
    	  var.add("var(\""+methodName+"\", \"TEMP "+n.f1.f0.toString()+"\").");
      if (Def)
    	  Def = false;
      else
    	  varUse.add("varUse(\""+methodName+"\", "+i_counter+", \"TEMP "+n.f1.f0.toString()+"\")."); 
    
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
   public void findLabel(String label,String methodName) throws Exception {
	  int counter;
	  int index;
	  String element="";
	  String Label="";
      if (label.length()>0)
    	  Label = label.substring(0, label.length()-1);
      else
    	  Label = label;

	  if (cjumps.containsKey(Label)){
		  counter = cjumps.get(Label);
		  element = "next(\""+methodName+"\", "+counter+", "+(counter+1)+").";
		  index = next.indexOf(element);
		  element = "next(\""+methodName+"\", "+counter+", "+(i_counter+1)+").";
		  next.add(index+1,element);
		  
	  }
	  else if (jumps.containsKey(Label)){
		  counter = jumps.get(Label);
		  element = "next(\""+methodName+"\", "+counter+", "+(counter+1)+").";
		  index = next.indexOf(element);
		  next.remove(index);
		  element = "next(\""+methodName+"\", "+counter+", "+(i_counter+1)+").";
		  next.add(index,element);		  
	  }
		  
	   
   }
   /**
    * f0 -> <IDENTIFIER>
    */
   public void visit(Label n, String methodName) throws Exception {
	  Const = -1;
	  expr +=n.f0.toString()+ " ";
      n.f0.accept(this, methodName);
      jump=true;
      labeled=true;
   }

	
	
}