#!/usr/bin/env python3
"""
ce script permet de generer des fichiers .deca
contiennent un grand nombre de declaration
de variables pour tester le dépassement de la pile
"""
import os;
import os.path;
import random;

path = "../../deca/codegen/invalid/dclVarDepassementPile.deca";
# path = "../../deca/context/valid/creat/compilConcur5.deca"
alphabet = "abcdefghijklmnopqrstuvwxyz";
listDeclVar = [];
f = open(path,"w");
f.write("{\n")
for i in range(20000):
    j = random.randint(0,len(alphabet) - 1);
    k = random.randint(0,len(alphabet) - 1);
    l = random.randint(0,len(alphabet) - 1);
    m = random.randint(0,len(alphabet) - 1);
    chaine = alphabet[j]+alphabet[k]+alphabet[l]+alphabet[m];
    if (chaine not in listDeclVar):
        listDeclVar.append(chaine);
        ligne = "int "+chaine+" = "+ str(i)+ ";\n";
        f.write(ligne)
f.write("}\n")
f.close()
