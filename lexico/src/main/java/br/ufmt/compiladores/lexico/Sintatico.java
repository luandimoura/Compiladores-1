
package br.ufmt.compiladores.lexico;

import java.util.HashMap;
import java.util.Map;


public class Sintatico {
    private LexScanner scan;
    private String simbolo;
    private int tipo;
    private Map<String, Simbolo> tabelaSimbolos = new HashMap<>();
            
    
    public Sintatico(String arq){
        scan = new LexScanner(arq);
        
    }
    
    public void analise(){
        obtemToken();
        programa();
        if(simbolo.equals("")){
            System.out.println("Executado com sucesso!");
        }else{
            throw new RuntimeException("Erro sintatico: era esperado um fim de cadeia");
        }
    }
    
    private void obtemToken(){
        Token token = scan.nextToken();
        simbolo ="";
        if(token != null){
            simbolo = token.getTermo();
            System.out.println(simbolo);
            tipo = token.getTipo();
        }
        
    }
    
    private void programa(){
        if(simbolo.equals("program")){
            obtemToken();
            if (tipo == Token.IDENT){
                obtemToken();
                corpo();
                if(simbolo.equals(".")){
                    obtemToken();
                }else{
                  throw new RuntimeException("Erro sintatico: era esperado um ponto (.)");  
                }
            }else{
                throw new RuntimeException("Erro sintatico: era esperado um identificador");
            }
            
        }else{
            throw new RuntimeException("Erro sintatico: era esperado 'program'");
        }
    }
    
    private void corpo(){
        dc();
        if(simbolo.equals("begin")){
            obtemToken();
            comandos();
            if(simbolo.equals("end")){
                obtemToken();
            }else{
              throw new RuntimeException("Erro sintatico: era esperado 'end'");  
            }
        }else{
           throw new RuntimeException("Erro sintatico: era esperado 'begin'"); 
        }
    }
    
    private void dc(){
        if(simbolo.equals("real") || simbolo.equals("integer")){
            dc_v();
            mais_dc();
        }
        
    }
    
    private void mais_dc(){
        if(simbolo.equals(";")){
            obtemToken();
            dc();
        }
    }
    
    private void dc_v(){
        tipo_var();
        if(simbolo.equals(":")){
            obtemToken();
            variaveis();
        }else{
            throw new RuntimeException("Erro sintatico: era esperado ':'");
        }
    }
    
    private void tipo_var(){
        if(simbolo.equals("real") || simbolo.equals("integer")){
            obtemToken();
        }else{
            throw new RuntimeException("Erro sintatico: era esperado 'real' ou 'integer'");
        }
    }
    
    private void variaveis(){
        if(tipo != Token.IDENT){
            throw new RuntimeException("Erro sintatico: era esperado um identificador");
        }
        
        if(tabelaSimbolos.containsKey(simbolo)){
            throw new RuntimeException("Erro semantico: identificador ja encontrado "+simbolo);
        }else{
            tabelaSimbolos.put(simbolo, new Simbolo(this.tipo, simbolo));
        }
        
        obtemToken();
        mais_var();
    }
    
    private void mais_var(){
        if(simbolo.equals(",")){
           obtemToken();
           variaveis();
        }

    }
    
    private void comandos(){
        comando();
        mais_comandos();
    }
    
    private void mais_comandos(){
        if(simbolo.equals(";")){
            obtemToken();
            comandos();
        }
        
    }
    
    private void comando(){
        if(simbolo.equals("read")){
            obtemToken();
            if(simbolo.equals("(")){
                obtemToken();
                if(tipo == Token.IDENT){
                    if(tabelaSimbolos.containsKey(simbolo)){
                        obtemToken();
                        if(simbolo.equals(")")){
                            obtemToken();
                        }else{
                            throw new RuntimeException("Erro sintatico: era esperado ')'");
                        }
                    }else{
                       throw new RuntimeException("Erro semantico: identificador nao declarado"); 
                    }
                }else{
                    throw new RuntimeException("Erro sintatico: era esperado um identificador");
                }

            }else{
                throw new RuntimeException("Erro sintatico: era esperado '('");
            }
        }else if(simbolo.equals("write")){
            obtemToken();
            if(simbolo.equals("(")){
                obtemToken();
                if(tipo == Token.IDENT){
                    if(tabelaSimbolos.containsKey(simbolo)){
                        obtemToken();
                        if(simbolo.equals(")")){
                            obtemToken();
                        }else{
                            throw new RuntimeException("Erro sintatico: era esperado ')'");
                        }
                    }else{
                        throw new RuntimeException("Erro semantico: identificador nao declarado");
                    }
                }else{
                    throw new RuntimeException("Erro sintatico: era esperado um identificador");
                }

            }else{
                throw new RuntimeException("Erro sintatico: era esperado '('");
            }
        }else if(tipo == Token.IDENT){
            if(tabelaSimbolos.containsKey(simbolo)){
                obtemToken();
                if(simbolo.equals(":=")){
                    obtemToken();
                    expressao();
                
                }else{
                    throw new RuntimeException("Erro sintatico: era esperado ':='");  
                }
            }else{
                throw new RuntimeException("Erro semantico: identificador nao declarado");
            }
        }else if(simbolo.equals("if")){
            obtemToken();
            condicao();
            if(simbolo.equals("then")){
                obtemToken();
                comandos();
                pfalsa();
                if(simbolo.equals("$")){
                    obtemToken();
                }else{
                    throw new RuntimeException("Erro sintatico: era esperado '$'");
                }
                
            }else{
                throw new RuntimeException("Erro sintatico: era esperado 'then'");  
            }
        }else{
            throw new RuntimeException("Erro sintatico: era esperado 'read' ou"
                    + "'write' ou 'if' ou um identificador valido");
        }
    }
    
    private void condicao(){
        expressao();
        relacao();
        expressao();
    }
    
    private void relacao(){
        switch(simbolo){
            case "=":
                obtemToken();
                break;
            case "<>":
                obtemToken();
                break;
            case ">=":
                obtemToken();
                break;
            case "<=":
                obtemToken();
                break;
            case ">":
                obtemToken();
                break;
            case "<":
                obtemToken();
                break;
            default:
                throw new RuntimeException("Erro sintatico: era esperado '=' ou '<>' ou '>="
                        + "ou '<=' ou '>' ou '<'");
        }
    }
    
    private void expressao(){
        termo();
        outros_termos();
    }
    
    private void termo(){
        op_un();
        fator();
        mais_fatores();
    }
    
    private void op_un(){
        if(simbolo.equals("-")){
            obtemToken();
        }
        
    }
    
    private void fator(){
        if(tipo == Token.IDENT){
            if(tabelaSimbolos.containsKey(simbolo)){
                obtemToken();
            }else{
                throw new RuntimeException("Erro semantico: identificador nao declarado");
            }     
        }else if(tipo == Token.NUMERO_INTEIRO){
            obtemToken();
        }else if(tipo == Token.NUMERO_REAL){
            obtemToken();
        }else if(simbolo.equals("(")){
            obtemToken();
            expressao();
            if(simbolo.equals(")")){
                obtemToken();
            }else{
                throw new RuntimeException("Erro sintatico: era esperado ')'");
            }
        }else{
            throw new RuntimeException("Erro sintatico: era esperado um identificador ou"
                    + " um inteiro ou um real ou '('");
        }
    }
    
    private void outros_termos(){
        if(simbolo.equals("+") || simbolo.equals("-")){
            op_ad();
            termo();
            outros_termos();
        }
    }
    
    private void op_ad(){
        switch(simbolo){
            case "+":
                obtemToken();
                break;
            case "-":
                obtemToken();
                break;
            default:
                throw new RuntimeException("Erro sintatico: era esperado '+' ou '-'");
        }
    }
    
    private void mais_fatores(){
        if(simbolo.equals("*") || simbolo.equals("/")){
            op_mul();
            fator();
            mais_fatores();
        }
    }
    
    private void op_mul(){
        switch(simbolo){
            case "*":
                obtemToken();
                break;
            case "/":
                obtemToken();
                break;
            default:
                throw new RuntimeException("Erro sintatico: era esperado '*' ou '/'");
        }
    }
    
    private void pfalsa(){
        if(simbolo.equals("else")){
            obtemToken();
            comandos();
        }
    }
      
}
