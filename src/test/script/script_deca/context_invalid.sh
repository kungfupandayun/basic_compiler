#!/bin/sh
# ./../../deca/context/invalid/creat/*.deca ./../../deca/context/invalid/provided/*.deca
for i in ./../../deca/context/invalid/class/*.deca ./../../deca/context/invalid/creat/*.deca ./../../deca/context/invalid/provided/*.deca
do
echo "*************************************************************"
echo "$i"
# Remplacer <executable> par test_synt ou test_lex
# ou test_context ou decac
echo -e "\e[91m"
test_context "$i"
echo -e "\e[39m"
done
