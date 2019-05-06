sudo su -
kill -9 (lsof -n -i | grep "\*\:7777" | sed -r 's/java[ ]*(\b[0-9]+\b).*$/\1/')
