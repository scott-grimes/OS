/**
 * SUBLEQ ASSEMBLER
 
grammar
program := list of intructions
intruction := [.] list of items ( ';' | '\n' )
item    := [label:]expression
label   := id
expression := ( term | term+expression | term-expression )
term    := ( -term | (expression) | const | id )
const   := ( number | 'letter' | ? )

 */
import java.io.File;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.io.PrintWriter;

public class Assembler
{
    static String[] m;
    static Scanner in;
    static int pos;
    static Map<String, String> symbols;
    static Map<String, String> unresolvedSymbols;
    public static void main(String args[]){
        m = new String[512*4*4];
        pos = 0;
        symbols = new HashMap<String, String>();
        unresolvedSymbols = new HashMap<String, String>();
        processProgram("test.txt");
       
        exportProgram();
        printProgram();
    }
    
    public static void exportProgram(){
       try{
        PrintWriter out = new PrintWriter(new File("compiled.txt"));
           for(int i = 0;i<pos;i++){
               out.print(m[i]+ " ");
            }
            out.println();
        out.close();
       }catch(Exception e){System.out.println(e);}
    }
    
    //breaks the program into lines based on \n and ';'
    //removes any comments
    public static void processProgram(String fileName){
      try{
          in = new Scanner(new File(fileName));
          in.useDelimiter(";|\\n"); //reads the next line (semicolon or newline)
          String line;
        while(in.hasNext()){
            line = in.next().trim();
            line = line.split("#")[0];
            if(!line.equals(""))
              processInstruction(line);            
        }
      }catch(Exception e){System.out.println(e);}
      in.close();
      resolveLabels();
      insertLabelsInProgram();
    
    }
    
    
    public static void processInstruction(String s){
        //an instruction must be 3 expressions, unless the line begins with '.'
        int commandCount = 0;
        boolean isCommandLine = true;
        Scanner line = new Scanner(s);
        String item = "";
        System.out.print(s+"\t\t||");
        // if line starts with a ".", the line does not require 3 commands
        if(s.substring(0,1).equals(".")){
            isCommandLine = false;
            line.next();
        }
        
        
        //process each item
        while(line.hasNext()){
            item = line.next();
            
            //check to see if item is the start of a string
            if(item.substring(0,1).equals("\"")){
                generateFullString(item,line);
                commandCount++;
                continue;
            }
            processItem(item);
            commandCount++;
        } 
        
        //pad lines which don't have enough commands
        // A; is A A ?
        // A B; is A B ?
        if(isCommandLine){
            if(commandCount == 1){
                push(item);
                commandCount++;
            }
            if(commandCount==2){
                push("?");
        }
        }
        System.out.println();
    }
    
    public static void processItem(String s){
        if(s.indexOf(":")!=-1){
            processLabel(s);
        }
        else{
            processExpression(s);
        }
    }
    
    public static void processLabel(String s){
        String[] a = s.split(":",2);
        // does the label have characters or a string behind it? 
        // EX line->   . H: "Hello";
        if(a[1].equals("")){
            symbols.put(a[0],String.valueOf(pos));
            processExpression(String.valueOf(pos));
            return;
        }
        // if E:E then use store current position in location for label
        else if(a[0].equals(a[1])){
            symbols.put(a[0],String.valueOf(pos));
            processExpression(String.valueOf(pos));
            return;
        }
        //check to see if the value of the label is a number. 
        else if(isNumber(a[1])){
            symbols.put(a[0],String.valueOf(pos));
            processExpression(a[1]);
            return;
        }
        else{
        //the value of the label is another label, add to unsatisfied hash table
        //for later verification
        unresolvedSymbols.put(a[0],a[1]);
        processExpression(a[1]);
    }
    }
    
    public static void processExpression(String s){
        if(s.indexOf("-")!=-1 || s.indexOf("+")!=-1){
            
        }
        else if(s.equals("(-1)")){
            processTerm("-1");
        }
        else{
        processTerm(s);
    }
    }
    
    public static void processTerm(String s){
        push(s);
    }
    
    public static void processConst(String s){
        char i = s.charAt(0);
        push(String.valueOf((int)i));
       
    }
    
    public static void push(String s){
        if(s.equals("?")){
            m[pos] = String.valueOf(pos+1);
            pos++;
        }else
        {
            m[pos] = s;
            pos++;
        }
        System.out.print(m[pos-1]+ " ");
    }
    
    // returns true if the string contains ONLY digits 0-9 and a negative sign
    public static boolean isNumber(String s) {
     try { 
        Integer.parseInt(s); 
     } catch(Exception e){
         return false;
     }
     // only got here if we didn't return false
     return true;
    }
    
    //resolves any unresolved labels
    //replaces any label in our program with it's correct int value
    public static void resolveLabels(){
        
        if(unresolvedSymbols.keySet()==null) return;
        for(String i : unresolvedSymbols.keySet()){
            
            String oldVal = unresolvedSymbols.get(i);
            symbols.put(i,symbols.get(oldVal));
            unresolvedSymbols.remove(i);
            return;
        }
        
    }
    
    
    //replaces any label in our program with it's correct int value
    public static void insertLabelsInProgram(){
        
        for(int i = 0;i<pos;i++){
            if(!isNumber(m[i])){
                m[i] = symbols.get(m[i]);
            }
        }
        
    }
    //printprogram
    public static void printProgram(){
        
        for(int i = 0;i<pos;i++){
            System.out.print(m[i]+" ");
            if((i+1)%3==0)System.out.println();
        }
        
    }
    
    //recursivly build a string, allowing spaces inside until a closing " is reaced
    public static String generateFullString(String s, Scanner line){
        String next = line.next();
        s+=" "+next;
        int len = next.length();
        
        if(!next.substring(len-1).equals("\""))
            s = generateFullString(s, line);
        
        s = s.substring(1,s.length()-1);
        for(int i = 0;i<s.length();i++){
            
            switch(s.charAt(i)){
                case '\\':
                    i++;
                    switch(s.charAt(i)){
                        case 'n': push("12"); break;
                    }
                    break;
                default:
                    processConst(s.substring(i,i+1));
                    break;
            }
        }
        
        return s;
    
    }
    
}
