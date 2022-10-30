#!/usr/bin/env python3
"""
ce script permet de generer des fichiers .deca
contiennent un grand nombre de declaration
de variables pour tester le dépassement de la pile
"""
import os
import os.path
import random

path = "../../deca/syntax/invalid/ifImbrique.deca"



def if_imbrique(fichier,n):
    if n == 0:
        return fichier
    else:
        n = n - 1
        fichier = "if (true) \n{" + if_imbrique(fichier,n) + " \n }"
        return fichier


f = open(path,"w");
f.write("{\n")
fichier = if_imbrique("",997)
fichier = if_imbrique(fichier,600)
f.write(fichier)
f.write("\n}")
