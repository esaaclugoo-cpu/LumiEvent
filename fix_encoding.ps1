$templatesPath = "C:\Users\esaac\Documents\ILERNA\Programacion\PROYECTO TRANSVERSAL\NovaTicket\src\main\resources\templates"
$latin1 = [System.Text.Encoding]::GetEncoding("iso-8859-1")
$utf8NoBom = [System.Text.UTF8Encoding]::new($false)

$files = Get-ChildItem -Path $templatesPath -Filter "*.html"
foreach ($file in $files) {
    $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
    # Interpret the bytes as UTF-8 to get the current string content
    $currentContent = [System.Text.Encoding]::UTF8.GetString($bytes)
    # Encode that string to Latin-1 bytes (undoing the double-encoding)
    try {
        $originalBytes = $latin1.GetBytes($currentContent)
        # Now decode those bytes as UTF-8 to get the correct content
        $fixedContent = [System.Text.Encoding]::UTF8.GetString($originalBytes)
        if ($fixedContent -ne $currentContent) {
            [System.IO.File]::WriteAllText($file.FullName, $fixedContent, $utf8NoBom)
            Write-Host "Fixed: $($file.Name)"
        } else {
            Write-Host "No changes: $($file.Name)"
        }
    } catch {
        Write-Host "Error in $($file.Name): $_"
    }
}

