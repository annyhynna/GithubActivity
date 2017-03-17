#/bin/bash

function killitif {
     docker ps -a  > /tmp/yy_xx$$
     if grep --quiet $1 /tmp/yy_xx$$
      then
      echo "killing older version of $1"
      docker rm -f `docker ps -a | grep $1  | sed -e 's: .*$::'`
    fi
 }

if [ "$1" = "new-activity" ]
then
    NAME=web2
    SWAPFILE=swap2
    KILL=web1
elif [ "$1" = "activity" ]
then
    NAME=web1
    SWAPFILE=swap1
    KILL=web2
else
    NAME=$1
fi

# Kill the new container if it previously exists
killitif $NAME

# Run the new container we want to swap to
echo "building $1"
docker run -d -P --network ecs189_default --name $NAME $1

# Run corresponding swap shell script
echo $SWAPFILE
docker exec ecs189_proxy_1 /bin/bash /bin/$SWAPFILE.sh

# Kill the old container
killitif $KILL