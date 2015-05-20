package semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import syntaxtree.AllocationExpression;
import syntaxtree.AndExpression;
import syntaxtree.ArrayAllocationExpression;
import syntaxtree.ArrayAssignmentStatement;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.ArrayType;
import syntaxtree.AssignmentStatement;
import syntaxtree.BooleanType;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.CompareExpression;
import syntaxtree.Expression;
import syntaxtree.ExpressionList;
import syntaxtree.ExpressionTerm;
import syntaxtree.FalseLiteral;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.IfStatement;
import syntaxtree.IntegerLiteral;
import syntaxtree.IntegerType;
import syntaxtree.MainClass;
import syntaxtree.MessageSend;
import syntaxtree.MethodDeclaration;
import syntaxtree.MinusExpression;
import syntaxtree.NotExpression;
import syntaxtree.PlusExpression;
import syntaxtree.PrimaryExpression;
import syntaxtree.PrintStatement;
import syntaxtree.Statement;
import syntaxtree.ThisExpression;
import syntaxtree.TimesExpression;
import syntaxtree.TrueLiteral;
import syntaxtree.VarDeclaration;
import syntaxtree.WhileStatement;
import visitor.GJVoidDepthFirst;

public class spiglet extends GJVoidDepthFirst<String>{
	ArrayList<ArrayList<String>> args = null;
	HashMap<String,String> classes;
	HashMap<String,String> methodVars;
	boolean Return = false;
	HashMap<String,HashMap<String,Methods>> methodTable;
	HashMap<String,HashMap<String,String>> varTable;
	HashMap<String,String> MethodVars = new HashMap<String,String>();
	HashMap<String,ArrayList<String>> vtable ;
	HashMap<String,ArrayList<String>> idTable;	
	HashMap<String,String> mainTable;
	ArrayList<ArrayList<String>> args_method = new ArrayList<ArrayList<String>>();
	int arg_counter=-1;
	HashMap<String,String> classTable = new HashMap<String,String>();
	Methods obj=null;
	
	boolean This;
	boolean store = false;
	boolean message = false;
	String offset = null;
	String store_offset =null;
	String spiglet_code;
	String type;
	String exprtype;
	String id;
	String value;
	String temp_table = "";
	String methodName;
	String expr_value="";
	int method_temp = -1;
	//boolean Main = false;
	String className;
	String extendClass;
	private int temp_counter;
	String Store_temp=null;
	int label_counter=1;
	public spiglet(HashMap<String,String> classes,HashMap<String,HashMap<String,Methods>> methodTable,HashMap<String
	,HashMap<String,String>> varTable, HashMap<String,String> mainTable,HashMap<String,ArrayList<String>> vtable ,HashMap<String,ArrayList<String>> idTable) {		
		this.classes = classes;
		this.methodTable = methodTable;
		this.varTable = varTable;
		this.mainTable = mainTable;
		this.vtable = vtable;
		this.idTable = idTable;	
	
	}	
	/* Compare class1 with class2
	 * if class 2  is an object of class that extends the Class:class1
	 * then its correct
	 */
	public boolean Father(String class1,String class2){
		if (class1==null || class2==null)
				return false;
		if (class1.equals("int") || class1.equals("boolean") || class1.equals("int[]"))
			return false;
		if (class2.equals("int") || class2.equals("boolean") || class2.equals("int[]"))
			return false;	
		String extendClass = class2;
		if (!this.classes.containsKey(class2))
			return false;
		while ((extendClass = this.classes.get(class2))!=null){
			if (class1.equals(extendClass))
				return true;
			
		}
		return false;
	}
	public String getCode(){
		return spiglet_code;
	}
	public void visit(Goal n, String _)  throws Exception{
	
		  n.f0.accept(this,null);
		  n.f1.accept(this,null);
	      
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
		this.className = "main";
		this.temp_counter = 15;
		this.spiglet_code = "MAIN \n";	 
		this.MethodVars.clear();
		   /* Save varDeclarations to TEMPs */
		n.f14.accept(this,null);
		this.temp_counter +=10;		
		n.f15.accept(this,null);	       
	    this.spiglet_code +="END\n\n";
	    
	}

	
	/**
	 * Grammar production:	 
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "{"
	 * f3 -> ( VarDeclaration() )*
	 * f4 -> ( MethodDeclaration() )*
	 * f5 -> "}"	 
	 */
	public void visit(ClassDeclaration n,String trash) throws Exception{		
		this.className = n.f1.f0.toString();
		n.f4.accept(this,this.className);
		Store_temp = null;
	
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
	public void visit(ClassExtendsDeclaration n,String trash)throws Exception{	
	
		this.className = n.f1.f0.toString();
		n.f6.accept(this,"*"+className);
		Store_temp = null;
		
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
	
	public int saveArguments() throws Exception{
		ArrayList<ArrayList<String>> arguments = this.methodTable.get(this.className).get(this.methodName).arguments;
		Iterator<ArrayList<String>> args = arguments.iterator();
		String arg;
		int size=1;
		this.MethodVars.put("this","TEMP 0" );  
		while (args.hasNext()){
			arg = args.next().get(0);
			
			this.temp_counter++;
			this.MethodVars.put(arg,"TEMP "+ size);
			size++;
			
			
		}
		return size;
	}
   /**
    * Grammar production:
    * <PRE>
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    * </PRE>
    */
    public String checkMethodVars(String name)throws Exception{

    
    	if (this.MethodVars.containsKey(name)){
    		if (className.equals("main")){
    				type  = mainTable.get(name);
    				
    		}    				
    		else
    				type = this.methodTable.get(this.className).get(this.methodName).returnVartype(name);
    	
    		return this.MethodVars.get(name);
    	}
    	else
    		return null;
    	
    }
    public void checkClassMethods(String name,String method)throws Exception{
    	offset = null;
    	int oset =0;
    	String fullname;
    	String vmethod;
    	String method_name;
    	String[] parts;
    	String method_class;
		ArrayList<String> ids =this.vtable.get(name);
		Iterator<String> id = ids.iterator();   

		fullname = "";
		
    	while (id.hasNext()){
    		
    		vmethod = id.next();
    		parts = vmethod.split("_");
    		fullname=parts[1];
    		for (int i=2;i<parts.length;i++){
    			fullname +="_"+parts[i];
    		}  		
    		if (fullname.equals(method)){
    			offset = ""+4*oset;
    			break;
			
    		}
    		oset++;
    	}   	
	
    }
    public void checkClassVars(String name)throws Exception{
    //	offset = null;
    	int oset =0;
    	int found = 0;
    	String id_name;
    	String[] parts;
    	
		ArrayList<String> ids =this.idTable.get(this.className);
		Iterator<String> id = ids.iterator();   	
    	while (id.hasNext()){
    		oset++;
    		id_name = id.next();
    		parts = id_name.split("_");
    		id_name=parts[1];
    		for (int i=2;i<parts.length;i++){
    			id_name +="_"+parts[i];
    		}
    		System.out.println(id_name);
    		
    		if (id_name.equals(name)){

    			offset = ""+4*oset;  			
    			found = oset;
    			
    		}
    	}
    	if (found!=0 && store_offset==null){
    		
    		store_offset = offset;
    		
    		if (temp_counter > 800)
    			System.out.println("/offset: "+offset);
    		found = 0;
    	}

    	
		if (offset!=null && Store_temp==null){
			
			this.temp_counter++;
			this.spiglet_code +="\tMOVE TEMP "+temp_counter+" "+ "TEMP 0\n";
			value  = "TEMP "+temp_counter;
			Store_temp = value;
		}    	
    	if (!store && offset!=null){
			this.temp_counter++;
			
			this.spiglet_code +="\tHLOAD TEMP "+temp_counter+ " " + Store_temp +  " "+offset+"\n";
			//this.temp_counter++;
			value = "TEMP "+temp_counter;    		
    	}		
    	
    }
	public void visit(VarDeclaration n, String _) throws Exception{
	   this.MethodVars.put(n.f1.f0.toString(),"TEMP "+ ++temp_counter);
    }
	public void  visit(MethodDeclaration n,String className)throws Exception{
   	 //  this.temp_counter = -1;
   	   int size;
	   this.methodName = n.f2.f0.toString();
	   this.MethodVars.clear();
	   method_temp = -1;
	   /* Save args to TEMPs */
	   size = saveArguments();
	   

	   this.spiglet_code += this.className+"_"+this.methodName+" ["+size+"]"+"\n";
	   
	   /* Save varDeclarations to TEMPs */
	   n.f7.accept(this,null);
	   this.temp_counter +=10;
	   method_temp =-1;
	   /* Begin Statement */
	   this.spiglet_code += " BEGIN\n";
	   n.f8.accept(this,null);
	   
	   Return = true;
	   n.f10.accept(this,"r_value");
	   this.spiglet_code += " RETURN\n";	
	   this.spiglet_code += "\t"+value+"\n END\n\n";
	   Store_temp = null;
	   offset = null;
	   store_offset =null;
	   Return = false;
				
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
	
	/**
	 * Grammar production:	 
	 * f0 -> "this"	 
	 */
   public void visit(ThisExpression n,String trash) throws Exception{
	   this.This = true;
	   this.id = "this";
	   ArrayList<String> arg;
	   this.value = "TEMP 0";

	   this.type = this.className;
		
   }

  
  
   /**    
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | PrimaryExpression()    
    */
   public void visit(Expression n, String trash) throws Exception{	
	   boolean flag = false;
	   if (message) flag = true;
	   n.f0.accept(this, null);
	   if (This == true)
	    System.out.println("VALUE "+value);
	   ArrayList<String> arg;
	   if (flag){ //if f0->MessageSend  we save arguments of calling method
		  
		   arg = args_method.get(this.arg_counter);
		   arg.add(value);
		  // this.args_method.add(arg,counter,value);
			   expr_value += " " + value ;
	   }
	 
	}		  

   public void visit(NotExpression n, String trash)  throws Exception{
		 
	      n.f1.accept(this, null);
	      String expr = value;
	      this.spiglet_code +="MOVE TEMP " + ++this.temp_counter + " 1\n";
	      value = "TEMP "+this.temp_counter;
	      this.spiglet_code +="MOVE TEMP " + ++this.temp_counter + " MINUS "+value + " "+expr+"\n";
	      value = "TEMP "+this.temp_counter;
   }	  
   
   //get Method informations (Methods object) -Search in class and its parents 
	public Methods getMethod(String className,String method){
		String extendClass = className;
		HashMap<String,Methods> methods;
		if (!this.methodTable.containsKey(className))
			return null;
		methods = this.methodTable.get(className);
		if (methods.containsKey(method))
			return methods.get(method);
		while ((extendClass=classes.get(extendClass))!=null){
			if (!this.methodTable.containsKey(extendClass))
				continue;
			methods = this.methodTable.get(extendClass);
	    	if (methods.containsKey(method))
	    		return methods.get(method);
		}
    	
    	
    	return null;
    }
	//get var type-Search in class and its parents 
    public String getVars(String className,String var){
    	String extendClass = className;
    	//if its main search only mainTable 
    	if (className.equals("main")){
    		if (this.mainTable.containsKey(var))
    				return this.mainTable.get(var);
    		else
    				return null;
    		
    	}
    	HashMap<String,String> vars ;
    	if (!this.varTable.containsKey(className))
    		return null;
    	vars = this.varTable.get(className);
    	if (vars.containsKey(var))
    		return vars.get(var);
    	while ((extendClass=classes.get(extendClass))!=null){
    		
    		vars = this.varTable.get(extendClass);
	    	if (vars.containsKey(var))
	    		return vars.get(var);;
    	}
    	
    	
    	return null;
    }
    
    public void  connect_arguments() throws Exception{
    
    	Iterator<String> arg = this.args_method.get(this.arg_counter).iterator();   
    	expr_value ="";
    	while (arg.hasNext()){
    		expr_value +=arg.next();
    	}
    }
    /**
     * Grammar production:
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
	public void  visit(MessageSend n, String trash) throws Exception{
		
		
		String temp = this.className;
		String method,object;
		String method_value;
		String object_value;
		String expr;
		Methods obj;

		n.f0.accept(this,null);
		
		if (This!=true) //find type(class) of object which method is called
			object = this.type;
		else
			object = this.className;			
		
	//	This = true; //f2 -> Identifier() to check this.method
	/*	this.className = object; //to check identifier in class object
		This =
		this.className = temp;*/
		//method = this.id; //method name
		This = false;
		this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter + " " +value +"\n";
		value = "TEMP " + this.temp_counter;
		object_value = value;
		this.spiglet_code +="\tHLOAD TEMP "+ ++this.temp_counter + " " +value +" 0\n";
		value = "TEMP " + this.temp_counter;
	//	n.f2.accept(this,null);
		this.checkClassMethods(object, n.f2.f0.toString());
		
		this.spiglet_code +="\tHLOAD TEMP "+ ++this.temp_counter + " " +value + " " +offset+" \n";
		value = "TEMP " + this.temp_counter;
		method_value  = value;
		
		message = true;
		expr_value =  "";
		this.arg_counter++;
		ArrayList<String> temp_arg = new ArrayList<String>();
		this.args_method.add(temp_arg);
		n.f4.accept(this,null);//save arguments of calling method
		expr = expr_value;

		this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter + " CALL " +method_value + "( "+object_value +" " + expr+ ")\n";
		message = false;
		value = "TEMP " + this.temp_counter;
		This = false;
		
		expr_value =  "";
		this.args_method.remove(args_method.size()-1);
		this.arg_counter--;
	
	}
   /**   
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"   
    */
	/*
	BEGIN
	       HSTORE TEMP 0 8 TEMP 1
	       MOVE TEMP 233 PLUS TEMP 1 1
	       MOVE TEMP 234 4
	       MOVE TEMP 235 TIMES TEMP 233 TEMP 234
	       MOVE TEMP 91 HALLOCATE TEMP 235
	       MOVE TEMP 92 4
	L17    NOOP
	       MOVE TEMP 236 1
	       MOVE TEMP 237 PLUS TEMP 1 TEMP 236
	       MOVE TEMP 238 4
	       MOVE TEMP 239 TEMP 238
	       MOVE TEMP 240 TIMES TEMP 237 TEMP 239
	       MOVE TEMP 241 LT TEMP 92 TEMP 240
	       CJUMP TEMP 241 L18
	       MOVE TEMP 242 PLUS TEMP 91 TEMP 92
	       MOVE TEMP 243 0
	       HSTORE TEMP 242 0 TEMP 243
	       MOVE TEMP 92 PLUS TEMP 92 4
	       JUMP L17
	L18    NOOP
	       MOVE TEMP 244 4
	       MOVE TEMP 245 TIMES TEMP 1 TEMP 244
	       HSTORE TEMP 91 0 TEMP 245
	 */
   public  void visit(ArrayAllocationExpression n, String trash)throws Exception {
	   String expr;
	   String index;
	   String array;
	   String temp_array;
	   n.f3.accept(this,null);
	   expr = value;
	   this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter +" PLUS " + expr + " 1\n";
	   value = "TEMP "+this.temp_counter;
	   this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter +" TIMES " + value + " 4\n";
	   value = "TEMP "+this.temp_counter;
	   this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter +" HALLOCATE " + value + "\n";
	   
	   array = "TEMP "+this.temp_counter;
	   
	   this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter +" 4\n";
	   index = "TEMP "+this.temp_counter;
	   
	   String exp;
	   int temp_label,while_label;
	   label_counter++;
	   while_label = label_counter;
	   this.spiglet_code +=" L"+while_label + "     NOOP"+"\n";
	   this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter +" PLUS " + expr + " 1\n";
	   value = "TEMP "+this.temp_counter;
	   this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter +" TIMES " + value + " 4\n";
	   value = "TEMP "+this.temp_counter;
	   this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter +" LT " + index + " "+ value + "\n";
	   value = "TEMP "+this.temp_counter;	   
	   exp = value;
	   
	   this.spiglet_code +="\tCJUMP "+exp+ " L"+ ++label_counter +"\n";
	   temp_label = label_counter;
	   
	   this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter +" PLUS " + array + " "+ index + "\n";
	   value = "TEMP "+this.temp_counter;	  
	   this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter +" 0\n";
	   temp_array = value;
	   value = "TEMP "+this.temp_counter;	   
	   
	   this.spiglet_code +="\tHSTORE "+temp_array +" 0 " + value + "\n";
	   value = "TEMP "+this.temp_counter;
	   this.spiglet_code +="\tMOVE "+ index +" PLUS " + index  + " 4\n";
	   value = "TEMP "+this.temp_counter;
	   
	   this.spiglet_code +="\tJUMP "+ " L"+ while_label +"\n";
	   this.spiglet_code +=" L"+temp_label+ "     NOOP"+"\n";	   
	   this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter +" TIMES " + expr + " 4\n";
	   value = "TEMP "+this.temp_counter;
	   this.spiglet_code +="\tHSTORE "+array +" 0 " + value + "\n";
	   this.value = array;
	   this.type = "int[]";
   }
   		
   /**    
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"    
    */
   public void visit(AllocationExpression n, String trash) throws Exception{
	  String vtable_temp,idtable_temp;
      String object = n.f1.f0.toString();
      ArrayList<String> vtable = this.vtable.get(object);
      ArrayList<String> idtable = this.idTable.get(object);
      this.spiglet_code  +="\tMOVE TEMP " + ++this.temp_counter + " HALLOCATE "+4*vtable.size() +"\n";
      vtable_temp = "TEMP "+this.temp_counter;
      int size = 4 + 4*idtable.size();
      this.spiglet_code  +="\tMOVE TEMP " + ++this.temp_counter + " HALLOCATE "+ size +"\n";
      idtable_temp = "TEMP "+this.temp_counter;    
	  Iterator<String> method = vtable.iterator();   
	  int offset=0;
      while (method.hasNext()){
          this.spiglet_code  +="\tMOVE TEMP " + ++this.temp_counter + " "+method.next() +"\n"   ;
          value = " TEMP "+this.temp_counter;
          this.spiglet_code +="\tHSTORE "+vtable_temp +" "+4*offset+value+"\n";
          offset++;
    	}   	
      offset =0;
      
      this.spiglet_code +="\tHSTORE "+idtable_temp +" "+4*offset+" "+vtable_temp+"\n";  
	  Iterator<String> id = idtable.iterator(); 
      while (id.hasNext()){
    	  offset++;
    	  id.next();
          this.spiglet_code  +="\tMOVE TEMP " + ++this.temp_counter + " 0" +"\n"   ;
          value = " TEMP "+this.temp_counter;
          this.spiglet_code +="\tHSTORE "+idtable_temp +" "+4*offset+value+"\n";
          
    	}   	  

	  this.value = idtable_temp;
	  this.classTable.put(this.className+"_"+n.f1.f0.toString(),idtable_temp);
	  this.type = n.f1.f0.toString();
    }     
   	
   /**
    * Grammar production:
    * f0 -> <INTEGERtrashLITERAL>
    */
	public void visit(IntegerLiteral n, String trash) throws Exception{
		this.value = n.f0.toString();
		this.temp_counter++;
		this.spiglet_code += "\tMOVE TEMP "+temp_counter +" "+ value+ "\n";
		this.value = "TEMP "+temp_counter;
	}

	/**	 
	 * f0 -> "true"	 
	 */
	public void visit(TrueLiteral n, String trash) throws Exception{
		this.temp_counter++;
		this.spiglet_code += "\tMOVE TEMP "+temp_counter +" 1\n";
		this.value = "TEMP "+temp_counter;
	}


	/**	 
	 * f0 -> "false"	 
	 */
	public void visit(FalseLiteral n, String trash)throws Exception {
		this.temp_counter++;
		this.spiglet_code += "\tMOVE TEMP "+temp_counter +" 0\n";
		this.value = "TEMP "+temp_counter;
	}   



	/**	 
	 * f0 -> PrimaryExpression()
	 * f1 -> "&&"
	 * f2 -> PrimaryExpression()	 
	 */
	public void visit(AndExpression n, String trash) throws Exception{
	 
		n.f0.accept(this, null);
		String lvalue = value;
		String cond;
		this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter + " " + value +"\n";
		value = "TEMP " + this.temp_counter;
		cond = value ;
		this.spiglet_code +="\tCJUMP "+lvalue+" L"+ ++this.label_counter +"\n";
		
		n.f2.accept(this, null);		  
		String rtype = this.value;
		this.spiglet_code +="\tMOVE " +cond+ " " + rtype +"\n";
		this.spiglet_code +="L"+this.label_counter+"       NOOP\n";
		this.value  = cond;

	}	
	/**
	 * Grammar production:
	 * f0 -> PrimaryExpression()
	 * f1 -> "<"
	 * f2 -> PrimaryExpression()
	 */
	 public void visit(CompareExpression n, String trash) throws Exception{
		 
		 String comp = "LT ";
		 n.f0.accept(this, null);
		 String ltemp = this.value; 
		 n.f2.accept(this, null);		  
		 String rtemp = this.value;
		 
		 comp += ltemp+" "+rtemp ;
		 this.temp_counter++;
		 this.spiglet_code += "\tMOVE TEMP "+temp_counter+" "+comp+"\n";
		 this.value = "TEMP " + this.temp_counter;
		// this.MethodVars.put(n.f1.f0.toString(),"TEMP "+ ++this.temp_counter);

	 }
   /**	    
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"	    
    */
	public void visit(ArrayAssignmentStatement n, String trash)throws Exception {
			  
			  String lvalue;
			  String rvalue;
			  String temp_value;
			  String expr;
			  String len;
			  int temp_label;
			  n.f5.accept(this, null);
			  rvalue = value;
			  
			  n.f2.accept(this, null);
			  expr = value;
			  
			  this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter + " 1\n";
			  value =  "TEMP " + this.temp_counter;
			  temp_value = value;
			  
			  this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter + " LT " + expr + " 0\n";
			  value = "TEMP " + this.temp_counter;	  
			  this.spiglet_code +="\tCJUMP "+value + " L" + ++this.label_counter+"\n";
			  temp_label = this.label_counter;
			  this.spiglet_code +="\tERROR\n";
			  this.spiglet_code +="L" +temp_label + "      NOOP\n";
			  
			  boolean flag =false;
			  
			  n.f0.accept(this, null);
			  lvalue = value;
			  this.spiglet_code +="\tHLOAD TEMP "+ ++this.temp_counter + " " + lvalue + " 0\n";
			  value =  "TEMP " + this.temp_counter;
			  len = value;
			  this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter + " TIMES " + expr+ " 4\n";
			  value = "TEMP "+temp_counter;			  
			  this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter + " LT " + value + " "+ len+"\n";
			  value =  "TEMP " + this.temp_counter;				  
			  this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter + " MINUS " + temp_value + " " +value+"\n";
			  value = "TEMP " + this.temp_counter;
			  this.spiglet_code +="\tCJUMP "+value + " L" + ++this.label_counter+"\n";
			  temp_label = this.label_counter;
			  this.spiglet_code +="\tERROR\n";
			  this.spiglet_code +="L" +temp_label + "      NOOP\n";
			  
			  this.spiglet_code +="\tMOVE TEMP "+ ++temp_counter + " PLUS "+expr + " 1\n";
			  value = "TEMP "+temp_counter;
			  temp_value = value;
			  this.spiglet_code +="\tMOVE TEMP "+ ++this.temp_counter + " TIMES " + value + " 4\n";
			  value = "TEMP "+temp_counter;
			  this.spiglet_code +="\tMOVE TEMP "+ ++temp_counter + " PLUS "+lvalue + " " + value+"\n";
			  value = "TEMP "+temp_counter;
			  this.spiglet_code +="\tHSTORE "+value + " 0 "+rvalue  +"\n";
			  value = "TEMP "+temp_counter;			  

		     // String ltype = this.type;
		      
		      rvalue = value;
		  
	}	    	
	/**	    
	 * f0 -> Identifier()
	 * f1 -> "="
	 * f2 -> Expression()
	 * f3 -> ";"	    
	 */
	 public void visit(AssignmentStatement n, String trash) throws Exception{
		  store = true;
		  String lvalue;
		  String rvalue;
		  offset=null;
		  boolean flag =false;
		  n.f0.accept(this, null);
		  store = false;
		  lvalue = value;
		  if (offset==null)
			  flag = true;		  
		  n.f2.accept(this, null);

		  if (flag)
			  store = false;
		  else
			  store = true;
	     // String ltype = this.type;
	      
	      rvalue = value;
	     // String exptype = this.type;
	  //    this.temp_counter++;
	      if (store == false){
	    	  this.spiglet_code +="\tMOVE " +lvalue +" "+rvalue + "\n";
	    	  
	      }
	      else {
	    	  this.spiglet_code +="\tHSTORE "+Store_temp +" "+store_offset + " " + rvalue+"\n";
	      }
	      offset = null;  
	      store_offset = null; 
	      store = false;
	      This = false;


	 }
  /**
    * Grammar production:	    
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()	    
    */
	 public void visit(IfStatement n, String trash) throws Exception{
	   String exp;
	   int temp_laber;
	   n.f2.accept(this,null);
	   exp = value;
	   expr_value = value;
	   this.spiglet_code +="\tCJUMP "+exp + " L"+ ++label_counter +"\n";
	   temp_laber = label_counter;
	   n.f4.accept(this,null);
	   this.spiglet_code +="\tJUMP "+ " L"+ ++label_counter +"\n";
	   this.spiglet_code +=" L"+temp_laber + "     NOOP"+"\n";
	   n.f6.accept(this,null);
	   this.spiglet_code +=" L"+label_counter + "     NOOP"+"\n";
	   
	
   }
	   


	 
   /**
    * Grammar production:	    
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()	    
    */
   public void visit(WhileStatement n, String trash) throws Exception{
	   String exp;
	   int temp_label,while_label;
	   label_counter++;
	   while_label = label_counter;
	   this.spiglet_code +=" L"+while_label + "     NOOP"+"\n";
	   n.f2.accept(this,null);
	   exp = value;
	   
	   this.spiglet_code +="\tCJUMP "+exp+ " L"+ ++label_counter +"\n";
	   temp_label = label_counter;
	   n.f4.accept(this,null);
	   this.spiglet_code +="\tJUMP "+ " L"+ while_label +"\n";
	   this.spiglet_code +=" L"+temp_label+ "     NOOP"+"\n";

	
   }
	   
	   
   /**
    * Grammar production:	    
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"	    
    */
   public void visit(PrintStatement n, String trash) throws Exception{
	  n.f2.accept(this,null);
	  this.spiglet_code +="\tPRINT "+value+"\n";
	  store_offset=null;
   }	   
	   
	/**
	 * Grammar production:		 
	 * f0 -> IntegerLiteral()
	 *       | TrueLiteral()
	 *       | FalseLiteral()
	 *       | Identifier()
	 *       | ThisExpression()
	 *       | ArrayAllocationExpression()
	 *       | AllocationExpression()
	 *       | NotExpression()
	 *       | BracketExpression()		 
	 */
	public void visit(PrimaryExpression n, String trash) throws Exception{
		
		n.f0.accept(this,null);
		this.exprtype = type;
		
	}
	public void visit(Identifier n,String trash)throws Exception
	{		
	 	this.id = n.f0.toString();//name of var
	 	String val=null;
		
		boolean exist = false;

		if( this.This == false ) //type var is possible ClassVar,MethodVar,ArgVar,Class Object
		{
			if ((val = this.checkMethodVars(id))!=null)
				value = val;
			else{
				
				this.checkClassVars(id);
				val = value;
			}

		}

		this.This = false;
	
	}
	 
   /**	    
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()	    
    */
   public void visit(PlusExpression n, String trash) throws Exception{
	 
	
	  n.f0.accept(this, null);
	  String lvalue = value; 
	  n.f2.accept(this, null);
	  String rvalue = value;
	  value = "PLUS " +lvalue + " " +rvalue; 
	  this.spiglet_code += "\tMOVE TEMP " + ++temp_counter + " " + value + "\n";
	  value = "TEMP " + temp_counter;
   }
   
   /**	    
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()	    
    */
   public void visit(MinusExpression n, String trash)throws Exception {
	   
		
		  n.f0.accept(this, null);
		  String lvalue = value; 
		  n.f2.accept(this, null);
		  String rvalue = value;
		  value = "MINUS " +lvalue + " " +rvalue; 
		  this.spiglet_code += "\tMOVE TEMP " + ++temp_counter + " " + value + "\n";
		  value = "TEMP " + temp_counter;	 
	    	  
   }
   
   /**
    
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()	    
    */
   public void visit(TimesExpression n, String trash) throws Exception{
	 
		
		  n.f0.accept(this, null);
		  String lvalue = value; 
		  n.f2.accept(this, null);
		  String rvalue = value;
		  value = "TIMES " +lvalue + " " +rvalue; 
		  this.spiglet_code += "\tMOVE TEMP " + ++temp_counter + " " + value + "\n";
		  value = "TEMP " + temp_counter;	 
	    	
	    	
   }	 
 
   /**	    
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"	    
    */
   public void Error(String id,String index)throws Exception{
	   String len,temp_value;
	   this.spiglet_code +="\tHLOAD TEMP "+ ++temp_counter + " "+id +" 0\n";
	//   value = "TEMP "+temp_counter;
	//   this.spiglet_code +="\tMOVE TEMP "+ ++temp_counter + " "+id +" 0\n";
	   len = "TEMP "+temp_counter;
	   this.spiglet_code +="\tMOVE TEMP "+ ++temp_counter + " 1\n";
	   temp_value ="TEMP "+temp_counter;
	   this.spiglet_code +="\tMOVE TEMP "+ ++temp_counter + " LT "+index +" "+len + "\n";
	   value ="TEMP "+temp_counter;
	   this.spiglet_code +="\tMOVE TEMP "+ ++temp_counter + " MINUS "+temp_value +" "+value + "\n";
	   value = "TEMP "+temp_counter;
	   this.spiglet_code +="\tCJUMP "+ value + " L"+ ++this.label_counter + "\n";
	   this.spiglet_code +="\tERROR\n";
	   this.spiglet_code +="L"+this.label_counter+"      NOOP\n";
   }
   public void  visit(ArrayLookup n, String trash) throws Exception{

		  n.f0.accept(this, null);
		  String id = value; //number

		  n.f2.accept(this, null);
		  String index = value;
		  
		  this.temp_counter++;
		  this.spiglet_code +="\tMOVE TEMP "+temp_counter + " TIMES "+index +" 4\n";
		  index = "TEMP "+temp_counter;
		  Error(id,index);
		  this.spiglet_code +="\tMOVE TEMP "+ ++temp_counter + " 4\n";
		  value = "TEMP "+temp_counter;
		  this.spiglet_code +="\tMOVE TEMP "+ ++temp_counter + " PLUS "+index + " " + value+"\n";
		  value = "TEMP "+temp_counter;
		  this.spiglet_code +="\tMOVE TEMP "+ ++temp_counter + " PLUS "+id + " " + value+"\n";
		  value = "TEMP "+temp_counter;
		  this.spiglet_code +="\tHLOAD TEMP "+ ++temp_counter + " "+value  +" 0\n";
		  value = "TEMP "+temp_counter;
	    this.type = "int";
   }
   
   
   /**	    
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"	    
    */
   public void visit(ArrayLength n, String trash)throws Exception {
	   	  n.f0.accept(this, null);
	   	  String size;
	   	  String counter;
	   	  String index;
	   	  int temp_label;
	  // 	  value = "TEMP " + temp_counter;
	   	  this.spiglet_code +="\tHLOAD TEMP "+ ++temp_counter + " "+value +" 0\n";
	   	  value = "TEMP " + temp_counter;
	   	  size = value;
		  this.spiglet_code +="\tMOVE TEMP "+ ++temp_counter + " 4\n";
		  value = "TEMP "+temp_counter;
		  index = value;
		  this.spiglet_code +="\tMOVE TEMP "+ ++temp_counter + " 1\n";
		  value = "TEMP "+temp_counter;
		  counter = value;	
		  label_counter++;
		  temp_label = label_counter;
		  this.spiglet_code +=" L"+temp_label + "     NOOP"+"\n";		 
		  
		  this.spiglet_code +="\tMOVE TEMP "+ ++temp_counter + " LT "+index +" "+size + "\n";
		  value ="TEMP "+temp_counter;
		  this.spiglet_code +="\tCJUMP "+ value + " L"+ ++this.label_counter + "\n";
		  this.spiglet_code +="\tMOVE "+counter +" PLUS "+counter +" 1\n";
		  this.spiglet_code +="\tMOVE "+index +" PLUS "+index +" 4\n";
		  this.spiglet_code +="\tJUMP "+ " L"+ temp_label +"\n";
		  this.spiglet_code +=" L"+this.label_counter+ "     NOOP"+"\n";
		  this.value = counter;
		  
		  
	   	  
   }	 
	 
	
	
}

