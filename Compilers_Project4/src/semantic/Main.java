package semantic;

import syntaxtree.*;
import mjparser.MiniJavaParser;
import mjparser.ParseException;
import java.io.*;

public class Main {
    public static void main (String [] args){

    	int i;
    	
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
				
				File outputFILE = new File("./"+out_name+"spg");
				if( !outputFILE.exists() )
					outputFILE.createNewFile();
				FileWriter fw = new FileWriter( outputFILE.getAbsoluteFile() );
				BufferedWriter bw = new BufferedWriter(fw);	      
			   
	            MiniJavaParser parser = new MiniJavaParser(fis);
	            
	            Goal tree = parser.Goal();
	            ClassesCheck ClassCheck =  new ClassesCheck();
	            
	            tree.accept(ClassCheck,null);
	            VarMethod Phase2Check =  new VarMethod(ClassCheck.get_table());
	           
	            tree.accept(Phase2Check,null);
	            
	            vtables tables = new vtables(Phase2Check.getMethodOrder(),Phase2Check.getVarOrder());
	            
	            tree.accept(tables,null);
	            spiglet Phase4Check =  new spiglet(ClassCheck.get_table(),Phase2Check.getMethodTable(),Phase2Check.getVarTable(),ClassCheck.get_maintable(),tables.getVtable(),tables.getVartable());
	            tree.accept(Phase4Check,null);  	            
	            String code;
	            code = Phase4Check.getCode();
	          //  System.out.println(code);
				/* Write to output File */
				bw.write(code);
				bw.close();	            
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