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

public class Main {
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
	            theDir = new File("./generated-facts/"+out_name +"/").mkdirs();

	            System.out.println("./"+out_name +"/facts");
	            Save(ClassCheck.getInstr(),"instructions","./generated-facts/"+out_name +"/");
	            Save(ClassCheck.getNext(),"next","./generated-facts/"+out_name +"/");
	            Save(ClassCheck.getVarUse(),"varUse","./generated-facts/"+out_name +"/");
	            Save(ClassCheck.getVar(),"var","./generated-facts/"+out_name +"/");
	            Save(ClassCheck.getVarDef(),"varDef","./generated-facts/"+out_name +"/");
	            Save(ClassCheck.getVarMove(),"varMove","./generated-facts/"+out_name +"/");
	            Save(ClassCheck.getConstMove(),"constMove","./generated-facts/"+out_name +"/");
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
                    System.out.println(fileEntry);
                    parser.parse(factsReader);
                 //   System.out.println(fileEntry);
                    // Retrieve the facts and put all of them in factMap
                    System.out.println(parser.getFacts());
                    factMap.putAll(parser.getFacts());
                    
                }
            }
        }
        else {
            System.err.println("Invalid facts directory path");
            System.exit(-1);
        }
        System.out.println("e");
        File rulesFile = new File("./analysis-logic" + "/LiveRangeComputation.iris");
        Reader rulesReader = new FileReader(rulesFile);
        System.out.println("e");
        File queriesFile = new File("./queries"+ "/queries.iris");
        Reader queriesReader = new FileReader(queriesFile);
        System.out.println("e");
        // Parse rules file.
        parser.parse(rulesReader);
        // Retrieve the rules from the parsed file.
        List<IRule> rules = parser.getRules();
        
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

            // Output the variables.
            System.out.println("\n" + query.toString() + "\n" + variableBindings);

            // Output each tuple in the relation, where the term at position i
            // corresponds to the variable at position i in the variable
            // bindings list.
            for (int i = 0; i < relation.size(); i++) {
                System.out.println(relation.get(i));
            }
        }
    }
}