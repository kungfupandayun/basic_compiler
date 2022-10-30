#!/bin/sh
# ./../../deca/context/valid/creat/*.deca ./../../deca/context/valid/provided/*.deca
#./../../deca/context/valid/class/*.deca
for i in ./../../deca/codegen/valid/objet/*.deca  ./../../deca/context/valid/class/*.deca
do
echo "*************************************************************"
echo "$i"
# Remplacer <executable> par test_synt ou test_lex
# ou test_context ou decac
echo -e "\e[91m"
test_context "$i"
echo -e "\e[39m"
done
