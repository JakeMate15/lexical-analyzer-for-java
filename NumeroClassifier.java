public class NumeroClassifier {
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
                    else if (c == 'E' || c == 'e') {
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

    public static void main(String[] args) {
        String numero1 = "1234";           // Entero decimal positivo
        String numero2 = "-567";           // Entero decimal negativo
        String numero3 = "+056658";        // Entero octal positivo
        String numero4 = "-0x1A9F";        // Entero hexadecimal negativo
        String numero5 = "0";      // Real con exponente positivo
        String numero6 = "077";         // Real sin exponente negativo

        //int clasificacion1 = clasificarNumero(numero1);
        //int clasificacion2 = clasificarNumero(numero2);
        //int clasificacion3 = clasificarNumero(numero3);
        //int clasificacion4 = clasificarNumero(numero4);
        int clasificacion5 = clasificarNumero(numero5);
        //int clasificacion6 = clasificarNumero(numero6);

        //System.out.println(numero1 + " - " + clasificacion1);
        //System.out.println(numero2 + " - " + clasificacion2);
        //System.out.println(numero3 + " - " + clasificacion3);
        //System.out.println(numero4 + " - " + clasificacion4);
        System.out.println(numero5 + " - " + clasificacion5);
        //System.out.println(numero6 + " - " + clasificacion6);
    }
}
