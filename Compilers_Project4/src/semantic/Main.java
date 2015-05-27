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
	           

	         String[] parts = args[i].split("/");
             String input;
	         if (parts!=null)
	        	 input = parts[parts.length-1];
	         else
	        	 input = args[i];
	         int len = input.length() - 4;
			 String out_name = input.substring(0, len);
				
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
	            boolean theDir = new File("./"+out_name +"/facts").mkdirs();

	            System.out.println("./"+out_name +"/facts");
	            Save(ClassCheck.getInstr(),"instructions","./"+out_name+"/facts");
	            Save(ClassCheck.getNext(),"next","./"+out_name+"/facts");
	            Save(ClassCheck.getVarUse(),"varUse","./"+out_name+"/facts");
	            Save(ClassCheck.getVar(),"var","./"+out_name+"/facts");
	            Save(ClassCheck.getVarDef(),"varDef","./"+out_name+"/facts");
	            Save(ClassCheck.getVarMove(),"varMove","./"+out_name+"/facts");
	            Save(ClassCheck.getConstMove(),"constMove","./"+out_name+"/facts");
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

   
    public static void Save(ArrayList<String> array,String filename,String folder){
    	String code = "";
        try{
			File outputFILE = new File(folder+"/"+filename+".iris");
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
			
			bw.write(code);
			bw.close();	  			
        }
		catch(Exception e){
			System.out.println("Internal Error.");
		}

		
    }
}