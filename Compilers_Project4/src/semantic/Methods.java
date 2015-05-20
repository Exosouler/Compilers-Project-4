package semantic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
public class Methods {

	String returnType;		
	int argNumber; 
	ArrayList<ArrayList<String>> arguments = new ArrayList<ArrayList<String>>(); 
	HashMap<String,String> variables = new HashMap<String,String>(); 

	public String containsName(String Name){
		
		Iterator<ArrayList<String>> pargs = arguments.iterator();
		ArrayList<String> arg;
		while (pargs.hasNext()){
			
			arg = pargs.next();
			
			if (arg.get(0)==null)
				continue;
			if (arg.get(0).equals(Name)){
				
				return arg.get(1);
			}
			
		}
		
		return null;
	}
	public boolean equals(ArrayList<ArrayList<String>> arg,HashMap<String,String> classes){
		
		Iterator<ArrayList<String>> pargs = arguments.iterator();
		if (arg==null)
			return true;
		Iterator<ArrayList<String>> pargs2 = arg.iterator();
		ArrayList<String> argu1,argu2;
		
		while (pargs.hasNext()){

			argu1 = pargs.next();
			
			if (!pargs2.hasNext()){
				System.out.println("Not same number of arguments in call method");
				return false;
			}
			argu2 = pargs2.next();
			
			if (!argu1.get(1).equals(argu2.get(1))){
				if (!this.Father(argu1.get(1),argu2.get(1), classes)){
					System.out.println("Not same arguments type in call method");
					return false;
				}
				
			}
			
		}		
		
		if (pargs2.hasNext()){
			
			System.out.println("Not same number of arguments in call method");
			return false;
		} 
		return true;
	}
	public String returnVartype(String name){
		Iterator<ArrayList<String>> pargs = arguments.iterator();
		ArrayList<String> argu;
		while (pargs.hasNext()){

			argu = pargs.next();
			if (argu.get(0).equals(name))
				return argu.get(1);
					
		}	
		if (variables.containsKey(name))
			return  variables.get(name);

		return null;
	}
	public boolean checkArgType(HashMap<String,String> classes){
		Iterator<ArrayList<String>> pargs = arguments.iterator();
		ArrayList<String> argu;
		while (pargs.hasNext()){

			argu = pargs.next();
			
			if (!argu.get(1).equals("int") && !argu.get(1).equals("boolean") && !argu.get(1).equals("int[]") && !classes.containsKey(argu.get(1))){
				System.out.println("Object "+argu.get(1)+" doesnt exist");
				return false;
			}
					
			
		}
		if (!returnType.equals("int") && !returnType.equals("boolean") && !returnType.equals("int[]") && !classes.containsKey(returnType)){
			System.out.println("Object "+returnType+" doesnt exist");
			return false;
		}		
		return true;
	}
	public boolean checkVarType(HashMap<String,String> classes){
		
		Set<String> f = variables.keySet();
		Iterator<String> pf = f.iterator();
		String key,type;
		while (pf.hasNext()){

			key = pf.next();
			type = variables.get(key);
			if (type!=null && !type.equals("boolean")&& !type.equals("int") && !type.equals("int[]") && !classes.containsKey(type)){
				System.out.println("Object "+type+" doesnt exist");
				return false;
			}
					
			
		}	
		return true;
	}	
	public boolean compareReturntype(String returntype,HashMap<String,String> classes){
		if (!this.returnType.equals(returntype))
			return Father(this.returnType,returntype,classes);
		else
			return true;
	
	
	}
	public boolean Father(String class1,String class2,HashMap<String,String> classes){
		if (class1==null || class2==null)
				return false;
		if (class1.equals("int") || class1.equals("boolean") || class1.equals("int[]"))
			return false;
		if (class2.equals("int") || class2.equals("boolean") || class2.equals("int[]"))
			return false;	
		String extendClass = class2;
		if (!classes.containsKey(class2))
			return false;
		while ((extendClass = classes.get(extendClass))!=null){
			if (class1.equals(extendClass))
				return true;
			
		}
		return false;
	}	
}
