#!/bin/sh
for i in ./../../deca/syntax/invalid/creat/*.deca ./../../deca/syntax/invalid/provided/*.deca ./../../deca/syntax/invalid/class/*.deca
do
echo "*************************************************************"
echo "$i"
# Remplacer <executable> par test_synt ou test_lex
# ou test_context ou decac
echo -e "\e[91m"
test_synt "$i"
echo -e "\e[39m"
done
