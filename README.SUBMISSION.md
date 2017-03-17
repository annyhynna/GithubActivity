<b>Team members: Wei-Ling Hsu, Patty Liu</b>

A. Created files:

1. <b>HW4/nginx-rev/swap1.sh</b><br />
`swap1.sh` is to swap from web2 to web1 by changing the address that `proxy_pass` points to in `/etc/nginx/nginx.conf`.<br />
For `swap1.sh`, the address changes from `http://web2:8080/activity/` to `http://web1:8080/activity/`.<br />
For `swap2.sh`, the address changes from `http://web1:8080/activity/` to `http://web2:8080/activity/`.

2. <b>HW4/doswap.sh</b> (takes one argument of the new-image-name that we want to swap to)<br />
    Compile the part C1 commands into this single shell script.
    The shell first kills the running container (either web1 or web2), then run the new container with the image
    provided in the argument. It then execute the corresponding swap shell (swap1.sh if argument is activity;
    swap2.sh if argument is new-activity). Finally, it cleans up the exited containers.

B. Build the following images:
1. `ng` (built from provided nginx-rev folder)
    ```
    > cd ~IdeaProjects/GithubActivity/HW4/nginx-rev/
    > docker build -t ng .
    ```

2. `activity` (built from original implementation of activity folder)
    ```
    > cd ~IdeaProjects/GithubActivity/activity
    > docker build -t activity .
    ```
    
3. `new-activity` (built from updated implementation of activity folder)
    ```
    > cd ~IdeaProjects/GithubActivity/activity
    > docker build -t new-activity .
    ```

C1. To do the hot-swap (without `doswap.sh`)
1. Run `dorun.sh`, which starts the `docker-compose.yml` and `init.sh` to run two containers (`ng` and `web1`).
    ```
    > cd ~IdeaProjects/GithubActivity/HW4
    > ./dorun.sh
    ```

2. Run image `new-activity` in a container named `web2` with network `ecs189_default`
    ``` 
    > docker run -d -P --network ecs189_default --name web2 new-activity 
    ```

3. Checking nginx.conf file, the proxy is currently pointing to http://web1:8080/activity/
   ```
   > docker exec -it ecs189_proxy_1 cat etc/nginx/nginx.conf
   ```

4. Run swap2.sh in the container, and check the nginx.conf file again, the proxy now points to http://web2:8080/activity/
   ```
   > docker exec ecs189_proxy_1 /bin/bash /bin/swap2.sh
   > docker exec -it ecs189_proxy_1 cat etc/nginx/nginx.conf
   ```

5. Kill the old container
   ```
   > docker kill ecs189_web1_1
   ```

6. Clean up exited containers
   ```
   > ./cleanup.sh
   ```

7. Type in `http://localhost:8888/` in browser, Github stalker with new implementation should be up there.

8. Now, to swap from web2 to web1, repeat step 2-7 with image `activity` and shell script `swap1.sh`, and kill the unused container.
   After this, `localhost:8888` should loads Github stalker with original implementation.
   ```
   > docker run -d -P --network ecs189_default --name web1 activity
   > docker exec ecs189_proxy_1 /bin/bash /bin/swap1.sh
   > docker kill web2
   > ./cleanup.sh
   ```
    
C2. To do the hot-swap (with `doswap.sh`)
1. Run `dorun.sh`, which starts the `docker-compose.yml` and `init.sh` to run two containers (`ng` and `web1`).
    ```
    > cd ~IdeaProjects/GithubActivity/HW4
    > ./dorun.sh
    ```

2.  Swap from web1 to web2:
    ``` 
    > ./doswap.sh new-activity
    ```
    
    Swap from web2 to web1:
    ```
    > ./doswap.sh activity
    ```
