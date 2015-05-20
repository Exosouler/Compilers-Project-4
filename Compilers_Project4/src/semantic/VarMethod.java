package semantic;

import java.util.ArrayList;
import java.util.HashMap;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.FormalParameterList;
import syntaxtree.FormalParameter;
import syntaxtree.MainClass;
import syntaxtree.VarDeclaration;
import syntaxtree.MethodDeclaration;
import syntaxtree.Type;
import syntaxtree.ArrayType;
import syntaxtree.BooleanType;
import syntaxtree.IntegerType;
import syntaxtree.Identifier;
import visitor.GJVoidDepthFirst;



public class VarMethod extends GJVoidDepthFirst<String>{
	
	//Methods : Methods' information (arguments<String,String>,variables<String,String>,returnType)
	
	HashMap<String,HashMap<String,Methods>>  MethodTable = new HashMap<String,HashMap<String,Methods>>();// <Each class,methods>
	HashMap<String,HashMap<String,String>>  VarTable = new HashMap<String,HashMap<String,String>>();//<Each class,Vars(fields)>
	HashMap<String,String> ClassNames ; //Table of classes and theirs extend class
	HashMap<String,ArrayList<String>>  MethodOrderTable = new HashMap<String,ArrayList<String>>();// <Each class,methods>
	HashMap<String,ArrayList<String>>  VarOrderTable = new HashMap<String,ArrayList<String>>();// <Each class,methods>
	HashMap<String,String> ClassVars ; //For each Class a temp table with its vars
	HashMap<String,Methods> ClassMethods ;//For each Class a temp table with its methods
	ArrayList<String>  ClassMethodsOrder; //save methods order for each class
	ArrayList<String>  ClassVarsOrder; //save methods order for each class
	ArrayList<ArrayList<String>>  arg;
	String type;
	Methods methods;
	String className;
	String Identifier;
	String Method;
	int count;
	public HashMap<String,HashMap<String,Methods>>  getMethodTable(){return MethodTable;}
	public HashMap<String,HashMap<String,String>> getVarTable(){return VarTable;}
	
	public VarMethod(HashMap<String,String> ClassNames) {		
		this.ClassNames = ClassNames; 
		
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
		this.className = "main"; //At main
	}	
	
	   /**
	    * f0 -> "class"
	    * f1 -> Identifier()
	    * f2 -> "{"
	    * f3 -> ( VarDeclaration() )*
	    * f4 -> ( MethodDeclaration() )*
	    * f5 -> "}"
	    */
	public void visit(ClassDeclaration n,String trash)  throws Exception{
		//For each class save methods , vars
		
		this.ClassVars =  new HashMap<String,String>();
		this.ClassMethods =  new HashMap<String,Methods>();
		this.ClassMethodsOrder = new ArrayList<String> (); 
		this.ClassVarsOrder = new ArrayList<String> (); 
		n.f1.accept(this,null);
		
		
		this.className = n.f1.f0.toString(); 
		
		n.f3.accept(this,className);
		this.VarTable.put(className, this.ClassVars);
		
	
	
		n.f4.accept(this,className);
		this.MethodTable.put(className, this.ClassMethods);
		this.MethodOrderTable.put(className, ClassMethodsOrder);
		this.VarOrderTable.put(className, ClassVarsOrder);
		ClassMethods = null;
		this.ClassVars = null;
	
	 }
	
	
	/**
	 * Grammar production:
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "extends"
	 * f3 -> Identifier()
	 * f4 -> "{"
	 * f5 -> ( VarDeclaration() )*
	 * f6 -> ( MethodDeclaration() )*
	 * f7 -> "}"
	 */
	
	public void visit(ClassExtendsDeclaration n,String trash)  throws Exception{
		this.ClassVars =  new HashMap<String,String>();
		this.ClassMethods =  new HashMap<String,Methods>();
		this.ClassMethodsOrder = new ArrayList<String> (); 
		this.ClassVarsOrder = new ArrayList<String> (); 
		n.f1.accept(this,null);
		
		this.className = n.f1.f0.toString(); 
		
		n.f5.accept(this,"*"+className);
		
		this.VarTable.put(className, this.ClassVars);
		this.ClassVars = null;
		
		n.f6.accept(this,"*"+className);
		this.MethodOrderTable.put(className, ClassMethodsOrder);
		this.VarOrderTable.put(className, ClassVarsOrder);
		this.MethodTable.put(className, this.ClassMethods);
		ClassMethods = null;
		

	 }
	
	//For each method check if there are 2 same name of a variable
	public HashMap<String,ArrayList<String>> getMethodOrder(){
		return this.MethodOrderTable;
	}
	public HashMap<String,ArrayList<String>> getVarOrder(){
		return this.VarOrderTable;
	}
	public void checkMethodVars(VarDeclaration n,String methodName) throws Exception{
		Methods method = this.ClassMethods.get(methodName);
		
		n.f0.accept(this,null);
		
		method.variables.put(n.f1.f0.toString(),this.type);
		
		this.ClassMethods.put(methodName, method);		
		
	}
	/**
	 * Grammar production:
	 * f0 -> Type()
	 * f1 -> Identifier()
	 * f2 -> ";"
	 */	
	public void  visit(VarDeclaration n,String className) throws Exception
	{
		
		String Type;
		String ExtendclassName;
		
		HashMap<String,String> Vars; //Vars of class father1
		n.f0.accept(this,null);
		n.f1.accept(this,null);
		 
		
		n.f0.accept(this,null);
		Type = this.type; //Var type
		if (className.charAt(0)=='$'){ //Method Variables
			checkMethodVars(n,className.substring(1));
			return;
		}
		if( this.ClassVars.containsKey(n.f1.f0.toString()) )
		{
				System.out.println("2 Vars with same name: '"+n.f1.f0.toString()+"' in Class:"+className);
				throw new Exception();
		}
		if (className.charAt(0)=='*') {//extend class
			ExtendclassName = ClassNames.get(className.substring(1));

		}
		this.ClassVars.put(n.f1.f0.toString(), Type);	//<VarName,type>
		this.ClassVarsOrder.add(n.f1.f0.toString());
	
	}
		
	

	/**
	 * Grammar production:
	 * f0 -> "public"
	 * f1 -> Type()
	 * f2 -> Identifier()
	 * f3 -> "("
	 * f4 -> ( FormalParameterList() )?
	 * f5 -> ")"
	 * f6 -> "{"
	 * f7 -> ( VarDeclaration() )*
	 * f8 -> ( Statement() )*
	 * f9 -> "return"
	 * f10 -> Expression()
	 * f11 -> ";"
	 * f12 -> "}"
	 */
	//put return type,arguments type,method's vars  to methodTable for each method
	public void visit(MethodDeclaration n,String className)  throws Exception
	{
		this.arg = new ArrayList<ArrayList<String>> (); 
		
		String extendClass=null;
		Methods obj = new Methods();
		HashMap<String,Methods>  extendMethods;
		this.count = 0;
		this.Method = this.type;
		n.f1.accept(this,null);
		obj.returnType =this.type;// Return type	

		if( this.ClassMethods.containsKey(n.f2.f0.toString()) )
		{
				System.out.println("2 vars with the same name '"+n.f2.f0.toString()+"' in Class:"+className);
				throw new Exception();
		}
		if (className.charAt(0)=='*') {//extend class
			
			n.f4.accept(this,className.substring(1));
			extendClass = ClassNames.get(className.substring(1));
			while (extendClass!=null){
				if (!MethodTable.containsKey(extendClass))
					extendMethods = null;
				else
					extendMethods = MethodTable.get(extendClass);
				
			
				if(extendMethods!=null &&  extendMethods.containsKey(n.f2.f0.toString())){
				
					if (!obj.returnType.equals(extendMethods.get(n.f2.f0.toString()).returnType)){ //if they haven't the same type ERROR
						System.out.println("Return type isn't the same\n");
						throw new Exception();
					}
					if (this.count != extendMethods.get(n.f2.f0.toString()).argNumber){
						System.out.println("Dont have the same number of arguments");
						throw new Exception();
					}
					break;
				}
				extendClass = ClassNames.get(extendClass);
			}
		}	
		else
			n.f4.accept(this,null);
		obj.argNumber = this.count;
		obj.arguments = this.arg;
		this.ClassMethods.put(n.f2.f0.toString(), obj);	// <MethodName,Other>
		ClassMethodsOrder.add(n.f2.f0.toString());
		n.f7.accept(this,"$"+n.f2.f0.toString());
		
	}
	
	/**
	 * Grammar production:
	 * f0 -> FormalParameter()
	 * f1 -> ( FormalParameterRest() )*
	 */
	public void visit(FormalParameterList n,String className)  throws Exception
	{
	
		n.f0.accept(this,className);
		n.f1.accept(this,className);
	}
	
	/**
	 * Grammar production:
	 * f0 -> Type()
	 * f1 -> Identifier()
	 */
	public void visit(FormalParameter n,String className)  throws Exception
	{
		ArrayList<String> arg = new ArrayList<String>(); 
		Methods method  = null;

		String type ;
		this.count++;
		method = new Methods();
		method.arguments = this.arg;
		if(method!=null && method.containsName(n.f1.f0.toString())!=null )
		{
			System.out.println("Same argument Name '"+n.f1.f0.toString()+"' in Class: "+className);
			throw new Exception();
		}

		n.f0.accept(this,null)	;
		
		type = this.type;

		arg.add(0,n.f1.f0.toString()); //name
		arg.add(1,type); //type
		this.arg.add(arg);
		
		
	}


	/**
	 * Grammar production:
	 * f0 -> ArrayType()
	 *       | BooleanType()
	 *       | IntegerType()
	 *       | Identifier()
	 */
	public void visit(Type n,String trash)  throws Exception
	{
		n.f0.accept(this,null);
	}
	
	/**
	 * Grammar production:
	 * f0 -> "int"
	 * f1 -> "["
	 * f2 -> "]"
	 */
	public void visit(ArrayType n,String trash)  throws Exception
	{		
		this.type = "int[]";
	}
	
	/**
	 * Grammar production:
	 * f0 -> "boolean"
	 */
	public void visit(BooleanType n,String trash)  throws Exception
	{		
		this.type = "boolean";
	}
	
	/**
	 * Grammar production:
	 * f0 -> "int"
	 */
	public void visit(IntegerType n,String trash)  throws Exception
	{		
		this.type = "int";
	}

	/**
	 * Grammar production:
	 * f0 -> <IDENTIFIER>
	 */
	public void visit(Identifier n,String trash)  throws Exception
	{		
		this.type = n.f0.toString();
	
	}


	
}