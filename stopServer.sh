sudo su - 
lsof -n -i | grep "\*\:7777" | sed -r 's/java[ ]*(\b[0-9]+\b).*$/\1/' | xargs -I {} kill -9 {} 
exit
