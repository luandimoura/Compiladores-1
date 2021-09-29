
package br.ufmt.compiladores.lexico;


public class Simbolo {
    private String nome;
    private int tipo;
    
    public Simbolo(int tipo, String nome){
        this.tipo= tipo;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
    
    
    
}
