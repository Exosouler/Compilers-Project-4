package semantic;

import org.deri.iris.storage.IRelation;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;


public class optimiser {
	private static ArrayList<String> lines;
	public ArrayList<String> readFile(String pathname) throws IOException {

        File file = new File(pathname);
        Scanner scanner = new Scanner(file);
        lines = new ArrayList<String>();
        String line;
        try {
        	
            while(scanner.hasNextLine()) {      
            	line = scanner.nextLine();
                lines.add(line);
            }
            return lines;
        } finally {
            scanner.close();
        }
    }
	public String readFile(String pathname,int line_number,String method) throws IOException {

	    File file = new File(pathname);
	    Scanner scanner = new Scanner(file);
	    
	    String line;
	    int counter=0;
	    String scounter;
	    try {
	    	
	        while(scanner.hasNextLine()) {  
	        	
	        	line = scanner.nextLine();
	        	scounter = line.split(",")[1];
	        	scounter = scounter.substring(1);
	        	counter = Integer.valueOf(scounter);
	        	if (line.split("\"")[1].equals(method) && counter==line_number){
	        		scanner.close();
	        		return line;
	        	}
	        	
	           
	        }
	        
	    } finally {
	        scanner.close();
	    }
		return null;
    }   
    public void Save(ArrayList<String> array,String filename,String folder){
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
    public String findInstruction(int counter,String folder,String method) throws Exception{
        
        return readFile(folder+"/instructions.iris",counter,method);
        
      }
    public void ReplaceConst(String arg,IRelation relation,String folder)throws Exception {
  		int i_counter;
  		String line;
  		String counter;
  		String instr;
  		String method;
  		String com;
  		String Code="";
  		String Temp;
  		String value;
  		int index;
  		ArrayList<String> templist = new ArrayList<String>();
  		templist.addAll(lines);
  		boolean flag = false;
  		
          for (int i = 0; i < relation.size(); i++) {
          	System.out.println("arxh");
          	line = relation.get(i).toString();
          	System.out.println(line);
          	counter = line.split(",")[1];
          	counter = counter.substring(1);
          	i_counter = Integer.valueOf(counter);
          	method = line.split("'")[1];
          	Temp = line.split("'")[3];
          	value = line.split("'")[5];
          	instr = findInstruction(i_counter,folder,method);
              System.out.println(instr);
              method = instr.split("\"")[1];
              com = instr.split("\"")[3];
              
              System.out.println(method);
              System.out.println(com);
             
              com = com.replaceAll("\\s+","");
  			Iterator<String> ilines = lines.iterator();
  			String temp;
              while (ilines.hasNext()){
              	line = ilines.next();
              	temp = line.replaceAll("\\s+","");
              	
              	if (line.split(" ")[0].equals(method))
              		flag = true;
              	else if (lines.equals("END"))
              		flag = false;

              	
              	if (flag && temp.equals(com)){
              		index = templist.indexOf(line);
              		if (index!=-1){
  		        		templist.remove(line);
  		        		System.out.println("|"+Temp+"|");
  		        		System.out.println("|"+value+"|");
  		        		line = line.replace(Temp, value);
  		        		System.out.println("|"+line+"|");
  		        		System.out.println("|"+index+"|");
  		        		templist.add(index,line);
  		        		System.out.println("|"+index+"|");
              		}
              	}           		
              	
              }
              
          }
          System.out.println(templist);
  		File outputFILE = new File("./optimised-code/"+arg+"/"+arg+".spg");
  		if( !outputFILE.exists() )
  			outputFILE.createNewFile();
  		FileWriter fw = new FileWriter( outputFILE.getAbsoluteFile() );
  		BufferedWriter bw = new BufferedWriter(fw);	 
  		lines = templist;
  		Iterator<String> ilines = lines.iterator();
  		while (ilines.hasNext()){
           	line = ilines.next();
           	Code +=line+"\n";
  		}
            System.out.println(Code);
  		/*Write to output File */
  			bw.write(Code);
  			bw.close();
  		
  	}   
    public void ReplaceCopy(String arg,IRelation relation,String folder)throws Exception {
  		int i_counter;
  		String line;
  		String counter;
  		String instr;
  		String method;
  		String com;
  		String Code="";
  		String Temp;
  		String value;
  		int index;
  		ArrayList<String> templist = new ArrayList<String>();
  		templist.addAll(lines);
  		boolean flag = false;
  		
          for (int i = 0; i < relation.size(); i++) {
          	System.out.println("arxh");
          	line = relation.get(i).toString();
          	System.out.println(line);
          	counter = line.split(",")[1];
          	counter = counter.substring(1);
          	i_counter = Integer.valueOf(counter);
          	method = line.split("'")[1];
          	Temp = line.split("'")[3];
          	value = line.split("'")[5];
          	instr = findInstruction(i_counter,folder,method);
              System.out.println(instr);
              method = instr.split("\"")[1];
              com = instr.split("\"")[3];
              
              System.out.println(method);
              System.out.println(com);
             
              com = com.replaceAll("\\s+","");
  			Iterator<String> ilines = lines.iterator();
  			String temp;
              while (ilines.hasNext()){
              	line = ilines.next();
              	temp = line.replaceAll("\\s+","");
              	
              	if (line.split(" ")[0].equals(method))
              		flag = true;
              	else if (lines.equals("END"))
              		flag = false;

              	
              	if (flag && temp.equals(com)){
              		index = templist.indexOf(line);
              		if (index!=-1){
  		        		templist.remove(line);
  		        		System.out.println("|"+Temp+"|");
  		        		System.out.println("|"+value+"|");
  		        		line = line.replace(Temp, value);
  		        		System.out.println("|"+line+"|");
  		        		System.out.println("|"+index+"|");
  		        		templist.add(index,line);
  		        		System.out.println("|"+index+"|");
              		}
              	}           		
              	
              }
              
          }
          System.out.println(templist);
  		File outputFILE = new File("./optimised-code/"+arg+"/"+arg+".spg");
  		if( !outputFILE.exists() )
  			outputFILE.createNewFile();
  		FileWriter fw = new FileWriter( outputFILE.getAbsoluteFile() );
  		BufferedWriter bw = new BufferedWriter(fw);	 
  		lines = templist;
  		Iterator<String> ilines = lines.iterator();
  		while (ilines.hasNext()){
           	line = ilines.next();
           	Code +=line+"\n";
  		}
            System.out.println(Code);
  		/*Write to output File */
  			bw.write(Code);
  			bw.close();
  		
  	}       
    public void deleteDeadCode(String arg,IRelation relation,String folder)throws Exception {
  		int i_counter;
  		String line;
  		String counter;
  		String instr;
  		String method;
  		String com;
  		String Code="";
  		ArrayList<String> templist = new ArrayList<String>();
  		templist.addAll(lines);
  		boolean flag = false;
  		
          for (int i = 0; i < relation.size(); i++) {
          	line = relation.get(i).toString();
          	counter = line.split(",")[1];
          	counter = counter.substring(1);
          	i_counter = Integer.valueOf(counter);
          	method = line.split("'")[1];
          	instr = findInstruction(i_counter,folder,method);
              System.out.println(instr);
              method = instr.split("\"")[1];
              com = instr.split("\"")[3];
              System.out.println(method);
              System.out.println(com);
             
              com = com.replaceAll("\\s+","");
  			Iterator<String> ilines = lines.iterator();
  			String temp;
              while (ilines.hasNext()){
              	line = ilines.next();
              	temp = line.replaceAll("\\s+","");
              	
              	if (line.split(" ")[0].equals(method))
              		flag = true;
              	else if (lines.equals("END"))
              		flag = false;

              	
              	if (flag && temp.equals(com)){
              		templist.remove(line);
              	}           		
              	
              }
              
          }
  		File outputFILE = new File("./optimised-code/"+arg+"/"+arg+".spg");
  		if( !outputFILE.exists() )
  			outputFILE.createNewFile();
  		FileWriter fw = new FileWriter( outputFILE.getAbsoluteFile() );
  		BufferedWriter bw = new BufferedWriter(fw);	 
  		lines = templist;
  		Iterator<String> ilines = lines.iterator();
  		while (ilines.hasNext()){
           	line = ilines.next();
           	Code +=line+"\n";
  		}
            System.out.println(Code);
  		/*Write to output File */
  			bw.write(Code);
  			bw.close();
  		
  	}	
	
}