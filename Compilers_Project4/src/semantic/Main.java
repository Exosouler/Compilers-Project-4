package semantic;

import syntaxtree.*;
import spigletParser.SpigletParser;
import spigletParser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Main {
    public static void main (String [] args){

    	int i;
    	//Test commit
    	for (i=0;i<args.length;i++){

	        FileInputStream fis = null;
	        try{
	        	
	            fis = new FileInputStream(args[i]);
				/* Output File */
	           

	//           String[] parts = args[i].split("/");
    //           String input;
	 //           if (parts!=null)
	//            	input = parts[parts.length-1];
	 //           else
	 //           	input = args[i];
		//		int len = input.length() - 4;
		//		String out_name = input.substring(0, len);
				
		//		File outputFILE = new File("./"+out_name+"spg");
		//		if( !outputFILE.exists() )
		//			outputFILE.createNewFile();
		//		FileWriter fw = new FileWriter( outputFILE.getAbsoluteFile() );
		//		BufferedWriter bw = new BufferedWriter(fw);	      
			   
				SpigletParser parser = new SpigletParser(fis);
	            
	            Goal tree = parser.Goal();
	            ClassesCheck ClassCheck =  new ClassesCheck();
	            
	            tree.accept(ClassCheck,null);
	            System.out.println(ClassCheck.getInstr());
	            Save(ClassCheck.getInstr(),"instructions");
	            Save(ClassCheck.getNext(),"next");
	            Save(ClassCheck.getVarUse(),"varUse");
	            Save(ClassCheck.getVar(),"var");
	            Save(ClassCheck.getVarDef(),"varDef");
	            Save(ClassCheck.getVarMove(),"varMove");
	            Save(ClassCheck.getConstMove(),"constMove");
	          //  System.out.println(code);
				/* Write to output File */
			//	bw.write(code);
		//		bw.close();	            
	        }
	        catch(ParseException ex){
	            System.out.println(ex.getMessage());
	        }
	        catch(FileNotFoundException ex){
	            System.err.println(ex.getMessage());
	        }
	     /*  catch(SemanticError ex){
	            System.err.println(ex.getMessage());
	        }*/
			catch(Exception e){
				System.out.println("Internal Error.");
			}
	        finally{
	            try{
	                if(fis != null) fis.close();
	            }
	            catch(IOException ex){
	                System.err.println(ex.getMessage());
	            }
	        }
    	} 
    }

   
    public static void Save(ArrayList<String> array,String filename){
    	String code = "";
        try{
			File outputFILE = new File("./"+filename+".iris");
			if( !outputFILE.exists() )
				outputFILE.createNewFile();
			FileWriter fw = new FileWriter( outputFILE.getAbsoluteFile() );
			BufferedWriter bw = new BufferedWriter(fw);
			
			Iterator<String> pargs = array.iterator();
			String arg;
			while (pargs.hasNext()){
				arg = pargs.next();
				code +=arg;
				code +="\n";
			}
			System.out.println(code);
			bw.write(code);
			bw.close();	  			
        }
		catch(Exception e){
			System.out.println("Internal Error.");
		}

		
    }
}