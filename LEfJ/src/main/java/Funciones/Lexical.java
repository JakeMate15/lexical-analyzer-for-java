/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Funciones;

import java.io.*;
import java.util.*;

import javax.swing.plaf.nimbus.State;

/**
 *
 * @author erikm
 */
public class Lexical {
        private static final String paralabrasReservadas[] = { "abstract", "boolean", "break", "byte", "case", "catch", "char",
			"class", "continue", "default", "do", "double", "else", "extends", "final", "finally", "float", "for", "if",
			"implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private",
			"protected", "public", "return", "short", "static", "super", "switch", "synchronized", "this", "throw",
			"throws", "transient", "try", "void", "volatile", "while", "strictfp", "enum", "goto", "const", "assert" }; // 50
        private FileReader fd;
        private int estado;
        private char ch;
        private String info;
        private String temp;
        int lineNum;
        private boolean finArch;
 
        public Lexical() {
                info = "";
                temp = "";
                lineNum = 1;
                finArch = false;
                getChar();

                analyze();

                write(info);
        }

        private void analyze() {

                if (ch == '\uFFFF' && temp.equals(""))
                        return;
                if (ch == '\n')
                        lineNum++;

                switch (estado) {
                case 0:
                        temp = "";

                        //Espacios y saltos
                        if (ch == ' ' || ch == '\r' || ch == '\t' || ch == '\n') {
                                cambioEstado(0);
                        } 
                        //Comentario
                        else if (ch == '/') {
                                cambioEstadoCaracter(1);
                        }
                        //Numero 
                        else if (esDigito(ch)) {
                                cambioEstadoCaracter(5);
                        }
                        //Operadores >,<,/,*,= 
                        else if (esOperador1(ch)) {
                                cambioEstadoCaracter(8);
                        } 
                        else if (ch == '!') {
                                cambioEstadoCaracter(9);
                        } 
                        //? . :
                        else if (esOperador2(ch)) {
                                escribeInfo((ch + ""), "Operador");
                                getChar();
                        } 
                        //, ; () {} []
                        else if (esDelimitador(ch)) {
                                escribeInfo((ch + ""), "Delimitador");
                                getChar();
                        } 
                        else if (ch == '"') {
                                cambioEstadoCaracter(10);
                        } 
                        else if (esLetra(ch)) {
                                cambioEstadoCaracter(11);
                        } 
                        else if (ch == '\'') {
                                cambioEstadoCaracter(14);
                        } 
                        else if (ch == '-' || ch == '+') {
                                cambioEstadoCaracter(16);
                        } 
                        else if (ch == '|') {
                                cambioEstadoCaracter(17);
                        } 
                        else if (ch == '&') {
                                cambioEstadoCaracter(18);
                        } 
                        else if (ch == (char) -1) {
                                // Terminar programa
                        } 
                        //Simbolo no valido
                        else {
                                error(1);
                                return;
                        }
                        break;
                case 1:
                        if (ch == '/') {
                                cambioEstado(2);
                        } 
                        else if (ch == '*') {
                                cambioEstado(3);
                        } 
                        else {
                                estado = 8;
                        }
                        break;
                case 2: // Procesamiento de comentario
                        if (ch == '\n') {
                                estado = 0;
                                getChar();
                        } 
                        else {
                                getChar();
                        }
                        break;
                case 3: // Procesamiento de comentario
                        if (ch == '*') {
                                cambioEstado(4);
                        } 
                        else {
                                getChar();
                                //System.out.println("Comentario infirnio");
                        }
                        break;
                case 4: // Procesamiento de comentario
                        if (ch == '/') {
                                cambioEstado(0);
                        } 
                        else {
                                cambioEstado(3);
                        }
                        break;
                case 5:
                        if (esDigito(ch)) {
                                temp += ch;
                                getChar();
                        } 
                        else {
                                estado = 6;
                        }
                        break;
                case 6:
                        if (ch == '.') {
                                cambioEstadoCaracter(7);
                        } 
                        else {
                                escribeInfo(temp, "constante");
                        }
                        break;
                case 7:
                        if (esDigito(ch)) {
                                cambioEstadoCaracter(13);
                        } else {
                                error(4);
                                return;
                        }
                        break;
                case 8:
                        if (ch == '=') {
                                temp += ch;
                                escribeInfo(temp, "operador");
                                getChar();
                        } 
                        else {
                                escribeInfo(temp, "operador");
                        }
                        break;
                case 9:
                        if (ch == '=') {
                                temp += ch;
                                escribeInfo(temp, "operador");
                                getChar();
                        } 
                        else {
                                error(2);
                                return;
                        }
                        break;
                case 10:
                        if (ch == '"') {
                                temp += ch;
                                escribeInfo(temp, "constante");
                                getChar();
                        } 
                        else if (ch == '\\') {
                                for (int i = 0; i<2; i++) {
                                        temp += ch;
                                        getChar();
                                }
                                estado = 10;
                        } 
                        else {
                                cambioEstadoCaracter(10);
                        }
                        break;
                case 11:
                        if (esDigito(ch) || esLetra(ch) || ch == '_') {
                                cambioEstadoCaracter(11);
                        } 
                        else {
                                estado = 12;
                        }
                        break;
                case 12:
                        if (esReservada(temp)) {
                                escribeInfo(temp, "Palabra reservada");
                                getChar();
                        } else {
                                escribeInfo(temp, "Identificador");
                                getChar();
                        }
                        break;
                case 13:
                        if (esDigito(ch)) {
                                cambioEstadoCaracter(13);
                        } 
                        else {
                                escribeInfo(temp, "Constante");
                        }
                        break;
                case 14:
                        if (ch == '\'') {
                                temp += ch;
                                if (charLegal(temp)) {
                                        escribeInfo(temp, "constante");
                                } 
                                else {
                                        error(9);
                                        return;
                                }
                                getChar();
                        } 
                        else if (ch == '\\') {
                                for (int i = 0; i < 2; i++) {
                                        temp += ch;
                                        getChar();
                                }
                                estado = 14;
                        } 
                        else {
                                cambioEstadoCaracter(14);
                        }
                        break;
                case 16:
                        if (esDigito(ch)) {
                                cambioEstadoCaracter(5);
                        } 
                        else {
                                estado = 8;
                        }
                        break;
                case 17:
                        if (ch == '|') {
                                temp += ch;
                                escribeInfo(temp, "operador");
                                getChar();
                        } 
                        else {
                                escribeInfo(temp, "operador");
                        }
                        break;
                case 18:
                        if (ch == '&') {
                                temp += ch;
                                escribeInfo(temp, "operador");
                                getChar();
                        } 
                        else {
                                escribeInfo(temp, "operador");
                        }
                        break;
                default:
                        error(3);
                        return;
                }

                analyze();
        }

        private boolean charLegal(String temp) {
                char[] ch = temp.toCharArray();
                int length = ch.length;
                boolean charLegal = false;

                /*
                * Char a = '';// error char b = ' ';// length = 3; char c = '\n';//length = 4;
                * b n r t " ' \ char d = '\122'; // length &lt;= 6;
                */
                if (length == 2) { // ''
                        charLegal = false;
                }
                else if (length == 3) {
                        charLegal = true;
                } 
                else if (length == 4) {
                        if ((ch[1] == '\\') && (ch[2] == 'b' || ch[2] == 'n' || ch[2] == 'r' || ch[2] == 't' || ch[2] == '\"' || ch[2] == '\'' || ch[2] == '\\' || esDigito(ch[2]))) {
                            charLegal = true;
                        }
                } 
                else if (length <= 6) {
                        if (ch[1] == '\\') {
                                for (int i = 2; i < (length - 1); i++) {
                                        if (!esDigito(ch[i])) {
                                                charLegal = false;
                                                break;
                                }
                                charLegal = true;
                                }   
                        } 
                        else {
                            System.out.println('*');
                            charLegal = false;
                        }
                }
                else {
                        charLegal = false;
                }

                return charLegal;
        }

        private void cambioEstado(int estado) {
                this.estado = estado;
                getChar();
        }

        private void cambioEstadoCaracter(int estado) {
                temp += ch;
                this.estado = estado;
                getChar();
        }

        private boolean esReservada(String temp2) {
                for(int i = 0; i<50; i++){
                        if(paralabrasReservadas[i] == temp2)
                                return true;
                }
                return false;
        }

        private void escribeInfo(String value, String type) {
                info += lineNum + " " + type + " " + value + "\r\n";
                estado = 0;
        }

        private boolean esLetra(char ch) {
                if ((ch>='a' && ch<='z') || (ch>='A' && ch<='Z'))
                        return true;
                else
                        return false;
        }
    
        private boolean esDelimitador(char ch) {
                if (ch == ',' || ch == ';' || ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == '{' || ch == '}')
                        return true;
                return false;
        }

        private boolean esOperador1(char ch) {
                if (ch == '/' || ch == '*' || ch == '=' || ch == '<' || ch == '>')
                    return true;
                return false;
        }
    
        private boolean esOperador2(char ch) {
                if (ch == '?' || ch == '.' || ch == ':')
                        return true;
                return false;
        }

        private boolean esDigito(char ch) {
                if (ch >= '0' && ch<='9')
                        return true;
                else
                        return false;
        }

        private void error(int i) {
                System.out.println(estado);
                if(estado==-1){
                        info += lineNum + " Comentario sin cierre\r\n";
                }
                else{
                        info += lineNum + " Error\r\n" ; 
                }
                
                //"Error de análisis léxico\r\nUbicación del error:" + i;
        }

        private void getChar() {
                try {
                        if (fd == null) {
                                //C:/Users/erikm/OneDrive/Escritorio/AnalizadorLexico/Main.java
                                fd = new FileReader("C:/Users/erikm/OneDrive/Escritorio/AnalizadorLexico/Main.java");
                        }

                        
                        ch = (char) fd.read();
                        //System.out.println("Antes erro " + (int) ch);

                        if(ch > 255){
                                estado = -1;
                        }

                        if (ch == -1) { 
                                // Al leer datos de un archivo, se devolverá un tipo int -1 al final de los datos para indicar el final
                                //System.out.println("Fin del documetno");
                                fd.close();
                        }
            } catch (IOException e) {
                
            }
        }

        private void write(String info) {
                try {
                        FileWriter fw = new FileWriter("C:/Users/erikm/OneDrive/Escritorio/AnalizadorLexico/result.txt");

                        fw.write(info);
                        fw.flush(); 

                        fw.close();
                } catch (IOException e) {

                }
    }

        public static void main(String[] args){
                new Lexical();
        }
}
