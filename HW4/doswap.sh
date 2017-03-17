#/bin/bash

function killitif {
     docker ps -a  > /tmp/yy_xx$$
     if grep --quiet $1 /tmp/yy_xx$$
      then
      echo "killing older version of $1"
      docker kill `docker ps -a | grep $1  | sed -e 's: .*$::'`
    fi
 }

function containerNotExist {
     docker ps -a  > /tmp/yy_xx$$
     if grep --quiet $1 /tmp/yy_xx$$
        then
        return 1
     else
        return 0
    fi
}

if [ "$1" = "new-activity" ]
then
    NAME=web2
    SWAPFILE=swap2
elif [ "$1" = "activity" ]
then
    NAME=web1
    SWAPFILE=swap1
fi

if containerNotExist $NAME
then
    killitif web1
    killitif web2

    # Run the new container we want to swap to
    echo "building $1"
    docker run -d -P --network ecs189_default --name $NAME $1

    # Run corresponding swap shell script
    echo $SWAPFILE
    docker exec ecs189_proxy_1 /bin/bash /bin/$SWAPFILE.sh
else
    echo "container $1 already running"
fi

EXITEDCONTAINER=$(docker ps -qa --no-trunc --filter "status=exited")
if [ ! -z "$EXITEDCONTAINER" ]
then
    docker rm "$EXITEDCONTAINER"
fi

