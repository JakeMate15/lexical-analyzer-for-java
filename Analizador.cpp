#include<bits/stdc++.h>
using namespace std;

int lineaActual = 0;
int idxVector = 0;
map<int,vector<string>> programa;

void lectura();

int main() {
    lectura();

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

        //cout << programa.size() << endl;
        for(auto [lin,vec]: programa){
            cout << lin << ": "; 
            for(auto x: vec){
                cout << x << " ";
            }
            cout << endl;
        }

        archivo.close();
    } else {
        cout << "No se pudo abrir el archivo." << endl;
    }
}

void analiza(){
    
}