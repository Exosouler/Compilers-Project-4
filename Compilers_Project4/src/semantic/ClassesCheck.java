package semantic;


import java.util.HashMap;
import syntaxtree.ArrayType;
import syntaxtree.BooleanType;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.Identifier;
import syntaxtree.IntegerType;
import syntaxtree.MainClass;
import syntaxtree.VarDeclaration;
import visitor.GJVoidDepthFirst;
import syntaxtree.Goal;


public class ClassesCheck extends GJVoidDepthFirst<String>{

	HashMap<String,String> Table = new HashMap<String,String>(); //Classes name, ExtendClass name (null if there isn't)
	
	HashMap<String,String> mainTable = new HashMap<String,String>(); //Var name,Var type for main

	Goal syntaxtree;
	String id;
	String type;
	
	public HashMap<String,String> get_table(){
		return this.Table;
	}
	public HashMap<String,String> get_maintable(){
		return this.mainTable;
	}	
	
	/**
	 * Grammar production:
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "{"
	 * f3 -> "public"
	 * f4 -> "static"
	 * f5 -> "void"
	 * f6 -> "main"
	 * f7 -> "("
	 * f8 -> "String"
	 * f9 -> "["
	 * f10 -> "]"
	 * f11 -> Identifier()
	 * f12 -> ")"
	 * f13 -> "{"
	 * f14 -> ( VarDeclaration() )*
	 * f15 -> ( Statement() )*
	 * f16 -> "}"
	 * f17 -> "}"
	 */
	public void visit(MainClass n,String trash) throws Exception{
		
		this.mainTable.put(n.f11.f0.toString(), "Keyword String"); //put Argument Keyword String to table 
		this.Table.put("!"+n.f1.f0.toString(), null); //put classname of class that contains main method
													//!+classname : to void create object of that class
		n.f14.accept(this,null);
		
		

	}
	
	/**
	 * Grammar production:
	 * f0 -> Type()
	 * f1 -> Identifier()
	 * f2 -> ";"
	 */
	public void visit(VarDeclaration n,String trash) throws Exception{
	
		String type;
		n.f0.accept(this,null);
		type = this.type;
		n.f1.accept(this,null);
		this.mainTable.put(n.f1.f0.toString(), type);
		
	}

	/**
	* Grammar production:
	* f0 -> "int"
	*/
	public void visit(IntegerType n, String trash) throws Exception{
		this.type =  "int";
	}
	   /**
	* Grammar production:
	* f0 -> "int"
	* f1 -> "["
	* f2 -> "]"
	*/
	public void visit(ArrayType n, String trash) throws Exception{
		this.type = "int[]";
	}
   
   /**
	* Grammar production:
	* f0 -> "boolean"
	*/
   public void visit(BooleanType n, String trash) throws Exception{
	   this.type = "boolean";
   }
   
   
   public void visit(Identifier n,String trash)  throws Exception
   {		
		this.type = n.f0.toString();
	
   }	   

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
	public void visit(ClassDeclaration n,String trash) throws Exception{
	
		this.Table.put(n.f1.f0.toString(), null); 
	
	}
	
	 /**
	* f0 -> "class"
	* f1 -> Identifier()
	* f2 -> "extends"
	* f3 -> Identifier()
	* f4 -> "{"
	* f5 -> ( VarDeclaration() )*
	* f6 -> ( MethodDeclaration() )*
	* f7 -> "}"
	*/
	public void visit(ClassExtendsDeclaration n,String trash) throws Exception
	{
		   
	   this.Table.put(n.f1.f0.toString(), n.f3.f0.toString());
	}

	
	
}