import java.io.*;
import java.util.*;

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
    private int aux;

    public Lexical() {
        info = "";
        temp = "";
        lineNum = 1;
        finArch = false;
        getChar();

        afd();

        write(info);
    }

    private void afd() {
        String val = "";

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
                //Numero decimal
                else if (esDigito(ch)) {
                        cambioEstadoCaracter(5);
                }
                //Operadores >,<,/,*,= 
                else if (esOperador1(ch)) {
                        cambioEstadoCaracter(8);
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
                //strings
                else if (ch == '"') {
                        cambioEstadoCaracter(10);
                } 
                //Identificadores con _
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
                        return;
                } 
                //Simbolo no valido
                else {
                        error(1);
                        return;
                }
                break;
        case 1: //Aceptacion 1 c9
                if (ch == '/') {
                        escribeInfo("Contenido","Comentario_1_lin");
                        cambioEstado(2);
                } 
                else if (ch == '*') {
                        escribeInfo("Inicio","Comentario_n_lin");
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
                        temp+=ch;
                        getChar();
                }
                break;
        case 3: // Procesamiento de comentario varias lineas
                if (ch == '*') {
                        cambioEstado(4);
                } 
                else {
                        getChar();
                        //System.out.println("Comentario infirnio");
                }
                break;
        case 4: // Aceptacion 2 c9
                if (ch == '/') {
                        escribeInfo("Fin","Comentario_n_lin");
                        cambioEstado(0);
                } 
                else {
                        cambioEstado(3);
                }
                break;
        case 5: //Numeros con posibles errores
                if (esDigito(ch)) {
                        temp += ch;
                        getChar();
                } 
                else if (ch == '.' || ch=='E' || ch=='x' || esHex(ch) || ch=='e' ) {
                        cambioEstadoCaracter(7);
                }
                else {  //Numero decimal u octal
                        val = diccionario(temp);
                        escribeInfo(temp, val);    
                        
                }
                break;
        case 7:
                if (esDigito(ch) || ch == '.' || ch=='E' || ch=='x' || esHex(ch) || ch=='e'  || ch=='-' || ch=='+') {
                        cambioEstadoCaracter(13);
                } else {
                        error(4);
                        return;
                }
                break;
        case 8: //Operadores con = c5 y c6
                if (ch == '=') {//c5
                        temp += ch;
                        escribeInfo(temp, "Operador");
                        getChar();
                } 
                else {//c6
                        escribeInfo(temp, "Operador");
                }
                break;
        case 10://a7 Cedenas
                if (ch == '"') {
                        temp += ch;
                        
                        escribeInfo(temp.replaceAll("\\s", ""), "Cadena");
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
        case 11://Palabras
                if (esDigito(ch) || esLetra(ch) || ch == '_') {
                        cambioEstadoCaracter(11);
                } 
                else {
                        estado = 12;
                }
                break;
        case 12://Resevada o identifcador
                if (esReservada(temp)) {//c7
                        escribeInfo(temp, "Reservada");
                        getChar();
                } else {//c8
                        escribeInfo(temp, "Identificador");
                        getChar();
                }
                break;
        case 13:
                if (esDigito(ch) || ch == '.' || ch=='E' || ch=='x' || esHex(ch) || ch=='e'  || ch=='-' || ch=='+') {
                        cambioEstadoCaracter(13);
                } 
                else {
                        val = diccionario(temp);
                        escribeInfo(temp, val);    
                }
                break;
        case 14:
                if (ch == '\'') {
                        temp += ch;
                        if (charLegal(temp)) {
                                escribeInfo(temp, "Numero");
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
        case 16://+ -
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
                        escribeInfo(temp, "Operador");
                        getChar();
                } 
                else {
                        escribeInfo(temp, "Operador");
                }
                break;
        case 18:
                if (ch == '&') {
                        temp += ch;
                        escribeInfo(temp, "Operador");
                        getChar();
                } 
                else {
                        escribeInfo(temp, "Operador");
                }
                break;
        case 19://Octales y hexadecimales
                break;
        default:
                error(3);
                return;
        }

        afd();
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
                if(paralabrasReservadas[i].equals(temp2))
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
        if (ch == '/' || ch == '*' || ch == '=' || ch == '<' || ch == '>' || ch=='%' ||  ch=='!')           
                return true;
        else                                                                                    
                return false;
    }

    private boolean esOperador2(char ch) {
        if (ch == '?' || ch == '.' || ch == ':')        return true;
        else                                            return false;
    }

    private boolean esDigito(char ch) {
        if (ch >= '0' && ch<='9')       return true;
        else                            return false;
    }

    private boolean esOctal(char ch){
        if(ch>=0 && ch<=7)      return true;
        else                    return false;
    }

    private boolean esHex(char ch){
        if( (ch>=0 && ch<=9 ) || (ch>='A' && ch<='F') ) return true;
        else                                            return false;
    }

    private void error(int i) {
        //System.out.println(estado);
        if(estado==-1){
                info += (lineNum + " Comentario_sin_cierre" + "\tff\r\n");
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
                        fd = new FileReader("Main.java");
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
        } 
        catch (IOException e) {
            
        }
    }

    private void write(String info) {
        try {
            FileWriter fw = new FileWriter("res.txt");

            fw.write(info);
            fw.flush(); 

            fw.close();
        } 
        catch (IOException e) {

        }
    }

    private int clasificarNumero(String numero) {
        int estado = 0;
        boolean esNegativo = false;

        for (char c : numero.toCharArray()) {
            //System.out.println(estado);
            switch (estado) {
                case 0:
                    //hexadecimal u octal
                    if (c == '0') {
                        estado = 1;
                    } 
                    //Decimal
                    else if (c >= '1' && c <= '9') {
                        estado = 2;
                    } 
                    else if(c=='-' || c=='+'){
                        continue;
                    }
                    else{
                        return -1;
                    }
                    break;
                case 1:
                    //Hex
                    if (c == 'x' || c == 'X') {
                        estado = 7;
                    } 
                    //Octal
                    else if (c >= '0' && c <= '7') {
                        estado = 3;
                    }
                    else if(c=='.'){
                        estado = 6;
                    }
                    else {
                        return -1;
                    }
                    break;
                case 2:
                    //Decimal
                    if (c >= '0' && c <= '9') {
                        estado = 2;
                    } 
                    //Real
                    else if (c == '.') {
                        estado = 6;
                    }
                    else if(c=='-'){
                        estado = 8;
                    }
                    else if (c == 'E') {
                        estado = 4;
                    } 
                    else {
                        return -1;
                    }
                    break;
                case 3:
                    if (c >= '0' && c <= '7') {
                        estado = 3;
                    } 
                    else{
                        return -1;
                    }
                    break;
                case 4:
                    if (c >= '1' && c <= '9') {
                        estado = 5;
                    } 
                    else {
                        return -1;
                    }
                    break;
                case 5:
                    if (c >= '0' && c <= '9') {
                        estado = 5;
                    } 
                    else{
                        return -1;
                    }
                    break;
                case 6:
                    if (c >= '0' && c <= '9') {
                        estado = 6;
                    } 
                    else if(c == 'E' || c == 'e'){
                        estado = 9;
                    }
                    else{
                        return -1;
                    }
                    break;
                case 7:
                    //Hex
                    if( (c>='0' && c<='9') || (c>='A' && c<='F')){
                        estado = 7;
                    }
                    else{
                        return -1;
                    }
                    break;
                case 9:
                    if(c=='-' || c=='+'){
                        estado = 4;
                    }
                    else{
                        return -1;
                    }
                    break;
                default:
                    return -1;
            }
        }

        //6: real sin exp
        //3: Octal
        //7: hexadeciaml
        //2: entero deciaml
        //5: real con exp
        //0: Cero

        return estado;
    }

    private String diccionario(String num){
        int tipo = clasificarNumero(num);
        String res = "";
        switch(tipo){
                case 0:
                        res = "Entero_decimal";
                        break;
                case 6: 
                        res = "Real_sin_exponente";
                        break;
                case 3:
                        res = "Octal";
                        break;
                case 7:
                        res = "Hexadecimal";
                        break;
                case 2:
                        res = "Entero_decimal";
                        break;
                case 5:
                        res = "Real_con_exp";
                        break;
                default:
                        res = "error";
                        break;
        }
        return res;
        //6: real sin exp
        //3: Octal
        //7: hexadeciaml
        //2: entero deciaml
        //5: real con exp
        //0: Decimal
    }
        
    public static void main(String[] args){
        new Lexical();
    }

}
