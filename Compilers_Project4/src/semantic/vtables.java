package semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.MainClass;
import visitor.GJVoidDepthFirst;



public class vtables extends GJVoidDepthFirst<String>{
	HashMap<String,ArrayList<String>>  MethodOrderTable = new HashMap<String,ArrayList<String>>();// <Each class,methods>
	HashMap<String,ArrayList<String>>  VarOrderTable = new HashMap<String,ArrayList<String>>();// <Each class,methods>
	HashMap<String,ArrayList<String>> vtable = new HashMap<String,ArrayList<String>>();
	HashMap<String,ArrayList<String>> varTable = new HashMap<String,ArrayList<String>>();
	String main;
	public void visit(MainClass n,String trash) throws Exception{
		main = n.f1.f0.toString();
		System.out.println(main);
	}	
	public vtables(HashMap<String,ArrayList<String>>  MethodOrderTable,HashMap<String,ArrayList<String>>  VarOrderTable){
		this.MethodOrderTable = MethodOrderTable;
		this.VarOrderTable= VarOrderTable;
		 System.out.println("Constructor of vtables");
	}
	public HashMap<String,ArrayList<String>> getVtable(){
		return this.vtable;
	}
	public HashMap<String,ArrayList<String>> getVartable(){
		return this.varTable;
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
		String ClassName = n.f1.f0.toString();
		System.out.println(ClassName);
		ArrayList<String> methods = MethodOrderTable.get(ClassName);
		ArrayList<String> temp = new ArrayList<String>();
		Iterator<String> Method = methods.iterator();		
		String method;
		
		while (Method.hasNext()){
			method = Method.next();	
			temp.add(ClassName+"_"+method);
			
		}
		this.vtable.put(ClassName, temp);
		temp = new ArrayList<String>();
		ArrayList<String> vars = VarOrderTable.get(ClassName);
		Iterator<String> Var = vars.iterator();	
		String var;
		while (Var.hasNext()){
			var = Var.next();	
			temp.add(ClassName+"_"+var);
			
		}		
		this.varTable.put(ClassName, temp);
		System.out.println("vtable of "+ClassName);
		System.out.println(vtable.get(ClassName));
		System.out.println("vartable of "+ClassName);
		System.out.println(varTable.get(ClassName));
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
		String ClassName = n.f1.f0.toString();
		String ExtendClass = n.f3.f0.toString();
		ArrayList<String> temp = new ArrayList<String>();
		
		ArrayList<String> methods = MethodOrderTable.get(ClassName);
		
		ArrayList<String> vtableFather ;
		Iterator<String> vtableMethod ;
		Iterator<String> Method;
		
		String Class;
		String childMethod;
		String fatherMethod;
		System.out.println(ExtendClass);
		
		if (!this.main.equals(ExtendClass)){
			vtableFather = vtable.get(ExtendClass);
			System.out.println(vtableFather);
			vtableMethod = vtableFather.iterator();			
			
		}
		else
			vtableMethod =null;
		String[] parts;
		while (vtableMethod!=null && vtableMethod.hasNext()){
			
			fatherMethod = vtableMethod.next();
			parts = fatherMethod.split("_");
			fatherMethod = parts[1];
			
			Class = parts[0];
			Method = methods.iterator();
			while (Method.hasNext()){
				childMethod = Method.next();
				if (childMethod.equals(fatherMethod)){
					methods.remove(childMethod);
					Class = ClassName;
					break;
				}
				
			}
			temp.add(Class+"_"+fatherMethod);
		}
		
		Method = methods.iterator();
		while (Method.hasNext()){	
			childMethod = Method.next();
			temp.add(ClassName+"_"+childMethod);
		}
		vtable.put(ClassName, temp);
		this.vtable.put(ClassName, temp);

		temp = new ArrayList<String>();
		Iterator<String> Var;
		ArrayList<String> vars ;
		if (!this.main.equals(ExtendClass)){
			vars = varTable.get(ExtendClass);
			Var = vars.iterator();	
		}
		else
			Var = null;
		String var;
		while (Var!=null && Var.hasNext()){
			var = Var.next();	
			temp.add(var);
			
		}	
		vars = VarOrderTable.get(ClassName);
		Var = vars.iterator();	
		while (Var.hasNext()){
			var = Var.next();	
			temp.add(ClassName+"_"+var);
			
		}		
		this.varTable.put(ClassName, temp);		
		
		System.out.println("vtable of "+ClassName);
		System.out.println(vtable.get(ClassName));
		System.out.println("vartable of "+ClassName);
		System.out.println(varTable.get(ClassName));
	 }	
	
	
}