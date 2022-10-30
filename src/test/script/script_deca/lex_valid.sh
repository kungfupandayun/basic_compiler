#!/bin/sh
for i in ./../../deca/syntax/valid/creat/*.deca ./../../deca/syntax/valid/Class/*.deca ./../../deca/syntax/valid/provided/*.deca
do
echo "*************************************************************"
echo "$i"
# Remplacer <executable> par test_synt ou test_lex
# ou test_context ou decac
echo -e "\e[32m"
test_lex "$i"
echo -e "\e[39m"
done
