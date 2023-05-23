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
                //Numero decimal
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
        case 1: //Seguimineto cometario
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
        case 4: // Fin comentario varias lineas
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
                        escribeInfo(temp, "Numero");
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
                        escribeInfo(temp, "Operador");
                        getChar();
                } 
                else {
                        escribeInfo(temp, "Operador");
                }
                break;
        case 9:
                if (ch == '=') {
                        temp += ch;
                        escribeInfo(temp, "Operador");
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
                        escribeInfo(temp, "Numero");
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
                        escribeInfo(temp, "Reservada");
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
                        escribeInfo(temp, "Numero");
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
        default:
                error(3);
                return;
        }

        analyze();
    }