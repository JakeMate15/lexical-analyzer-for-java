#include<bits/stdc++.h>
using namespace std;

#define all(a)  a.begin(),a.end()
#define impErr(a,b) cout << "Error en linea " << a << " de tipo " << b << "\n";

int lineaActual = 0;
int idxVector = 0;
map<int,vector<string>> programa;
int erroresNum = 0;
int errores = 0;

void lectura();
pair<bool,string> analiza(int);
bool esNun(string);

int main() {
    lectura();
    for(auto [linea,vec]: programa){
        /*
        cout << linea << ": ";
        for(auto x: vec)    cout << x << " ";
        cout << "\n";
        */
        if(count(all(vec),"error")){
            impErr(linea,"numerico");
            errores++;
        }
        else if(count(all(vec),"Comentario_sin_cierre")){
            impErr(linea,"comentario infinito");
            errores++;
        }
        else{
            pair<bool,string>res = analiza(linea);
            
            if(!res.first){
                //cout << linea << res.first << " " << res.second << endl;
                impErr(linea,res.second);
                errores++;
            }
        }
    }

    if(errores==0){
        cout << "No hay errores lexicos" << endl;
    }

    return 0;
}


void lectura(){
    ifstream archivo("res.txt");

    if (archivo.is_open()) {
        int lin;
        string token, contenido;


        while (archivo >> lin >> token >> contenido) {
            programa[lin].push_back(token);
        }

        /*
        for(auto [lin,vec]: programa){
            cout << lin << ": "; 
            for(auto x: vec){
                cout << x << " ";
            }
            cout << endl;
        }
        */

        archivo.close();
    } else {
        cout << "No se pudo abrir el archivo." << endl;
    }
}

pair<bool,string> analiza(int linea){
    int estado = 0;
    
    for(auto x: programa[linea]){
        if(estado == 0){
            if(x == "Operador")             estado = 1;
            else if(x == "Identificador")   estado = 3;
        }
        else if(estado == 1){
            if(x == "Operador")             return {false,"Operador no valido"};
            else                            estado = 0;
        }
        else if(estado == 2){
            return {false,"Operador no valido"};
        }
        else if(estado == 3){
            if(x == "Identificador")        return {false,"Identificador no valido"};
            else if(x=="Operador")          estado = 1;
            else                            estado = 0;
        }
        else if(estado == 4){
            return {false,"Identificador no valido"};
        }
        else{
            estado = -1;
            break;
        }
    }

    return {true,"Correcto"};                         
}

bool esNum(string s){
    if(s=="ED" || s=="RSE" || s=="O" || s=="H" || s=="RCE") return true;
    else    return false;
}
