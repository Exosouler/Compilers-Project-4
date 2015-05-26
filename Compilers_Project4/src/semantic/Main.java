package semantic;

import syntaxtree.*;
import spigletParser.SpigletParser;
import spigletParser.ParseException;
import java.io.*;

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
}