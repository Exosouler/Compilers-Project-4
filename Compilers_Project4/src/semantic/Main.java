package semantic;

import org.deri.iris.Configuration;
import org.deri.iris.KnowledgeBase;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.Parser;
import org.deri.iris.optimisations.magicsets.MagicSets;
import org.deri.iris.storage.IRelation;

import syntaxtree.*;
import spigletParser.SpigletParser;
import spigletParser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
	static ArrayList<String> lines;
	static optimiser opt = new optimiser(); 
    public static void main (String [] args){

    	int i;
    	boolean theDir = new File("./generated-facts").mkdirs();
    	
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
			 theDir = new File("./optimised-code/"+out_name +"/").mkdirs();	
			 ArrayList<String> lines = opt.readFile(args[i]);
			
		//		File outputFILE = new File("./"+out_name+"spg");
		//		if( !outputFILE.exists() )
		//			outputFILE.createNewFile();
		//		FileWriter fw = new FileWriter( outputFILE.getAbsoluteFile() );
		//		BufferedWriter bw = new BufferedWriter(fw);	      
			   
				SpigletParser parser = new SpigletParser(fis);
	            
	            Goal tree = parser.Goal();
	            ClassesCheck ClassCheck =  new ClassesCheck();
	            
	            tree.accept(ClassCheck,null);
	            System.out.println("Generating Facts for: "+out_name+".spg");
	            
	            theDir = new File("./generated-facts/"+out_name +"/").mkdirs();
	            opt.Save(ClassCheck.getInstr(),"instructions","./generated-facts/"+out_name +"/");
	            opt.Save(ClassCheck.getNext(),"next","./generated-facts/"+out_name +"/");
	            opt.Save(ClassCheck.getVarUse(),"varUse","./generated-facts/"+out_name +"/");
	            opt.Save(ClassCheck.getVar(),"var","./generated-facts/"+out_name +"/");
	            opt.Save(ClassCheck.getVarDef(),"varDef","./generated-facts/"+out_name +"/");
	            opt.Save(ClassCheck.getVarMove(),"varMove","./generated-facts/"+out_name +"/");
	            opt.Save(ClassCheck.getConstMove(),"constMove","./generated-facts/"+out_name +"/");
	            DatalogMain(out_name);
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

    
    
    public static void DatalogMain(String args) throws Exception {

        Parser parser = new Parser();

        final String projectDirectory = "./generated-facts/";
        Map<IPredicate, IRelation> factMap = new HashMap<>();
        
        /** The following loop -- given a project directory -- will list and read parse all fact files in its "/facts"
         *  subdirectory. This allows you to have multiple .iris files with your program facts. For instance you can
         *  have one file for each relation's facts as our examples show.
         */
        System.out.println(projectDirectory + args+"/");
        final File factsDirectory = new File(projectDirectory + args);
        if (factsDirectory.isDirectory()) {
            for (final File fileEntry : factsDirectory.listFiles()) {
            	System.out.println(fileEntry);
                if (fileEntry.isDirectory())
                    System.out.println("Omitting directory " + fileEntry.getPath());

                else {
                    Reader factsReader = new FileReader(fileEntry);
                    
                    parser.parse(factsReader);
                 //   System.out.println(fileEntry);
                    // Retrieve the facts and put all of them in factMap
                    
                    factMap.putAll(parser.getFacts());
                    
                }
            }
        }
        else {
            System.err.println("Invalid facts directory path");
            System.exit(-1);
        }
        File rulesFile;
        List<IRule> rules;
        List<IRule> rules1;
        Reader rulesReader;
        rulesFile = new File("./analysis-logic" + "/LiveRangeComputation.iris");
        rulesReader = new FileReader(rulesFile);
        parser.parse(rulesReader);        
        rules = parser.getRules();
        
        rulesFile = new File("./analysis-logic" + "/DeadCodeComputation.iris");
        rulesReader = new FileReader(rulesFile);
        parser.parse(rulesReader);
        rules1 =  parser.getRules();;
        rules.addAll(rules1);
        
        rulesFile = new File("./analysis-logic" + "/Constant-CopyPropagation.iris");
        rulesReader = new FileReader(rulesFile);
        parser.parse(rulesReader);
        rules1 =  parser.getRules();;
        rules.addAll(rules1); 
        
        rulesFile = new File("./analysis-logic" + "/BasicBlockBomputation.iris");
        rulesReader = new FileReader(rulesFile);
        parser.parse(rulesReader);
        rules1 =  parser.getRules();;
        rules.addAll(rules1);       
        File queriesFile = new File("./queries"+ "/queries.iris");
        Reader queriesReader = new FileReader(queriesFile);
        // Parse rules file.

        // Retrieve the rules from the parsed file.
        
        
        // Parse queries file.
        parser.parse(queriesReader);
        // Retrieve the queries from the parsed file.
        List<IQuery> queries = parser.getQueries();

        // Create a default configuration.
        Configuration configuration = new Configuration();

        // Enable Magic Sets together with rule filtering.
        configuration.programOptmimisers.add(new MagicSets());

        // Create the knowledge base.
        IKnowledgeBase knowledgeBase = new KnowledgeBase(factMap, rules, configuration);

        // Evaluate all queries over the knowledge base.
        for (IQuery query : queries) {
            List<IVariable> variableBindings = new ArrayList<>();
            IRelation relation = knowledgeBase.execute(query, variableBindings);
            System.out.println("\n" + query.toString() + "\n" + variableBindings);
            if (query.toString().equals("?- dead(?m, ?i, ?v).")){ 
            	opt.deleteDeadCode(args,relation,projectDirectory + args);
            }
            if (query.toString().equals("?- const_progaration(?m, ?i, ?v1, ?c1).")){ 
            	opt.Replace(args,relation,projectDirectory + args);
            }  
            if (query.toString().equals("?- copy_progaration(?m, ?i, ?v, ?k).")){ 
            	opt.Replace(args,relation,projectDirectory + args);
            }              
            // Output the variables.
            

            // Output each tuple in the relation, where the term at position i
            // corresponds to the variable at position i in the variable
            // bindings list.
            for (int i = 0; i < relation.size(); i++) {
                System.out.println(relation.get(i));
            }             

        }
    }
    
    
 
	
}