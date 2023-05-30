public class Clasificador {
    public static int clasificarNumero(String numero) {
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
        //1: Cero

        return estado;
    }
}