param(
    [string]$Usuario = $env:DB_USERNAME,
    [string]$Url = 'jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl',
    [int]$Porta = 8080
)
$ErrorActionPreference = 'Stop'
if (-not $Usuario) { $Usuario = Read-Host 'Usuário Oracle FIAP (RM)' }
$jar = Join-Path $PSScriptRoot 'target\bairro-em-dia-api-1.0.0.jar'
if (-not (Test-Path -LiteralPath $jar -PathType Leaf)) {
    Push-Location $PSScriptRoot
    try {
        & .\mvnw.cmd package -DskipTests
        if ($LASTEXITCODE -ne 0) { throw 'Não foi possível compilar a API.' }
    } finally { Pop-Location }
}
$environmentNames = @('DB_URL','DB_USERNAME','DB_PASSWORD','SPRING_PROFILES_ACTIVE','PORT')
$previousEnvironment = @{}
foreach ($environmentName in $environmentNames) {
    $previousEnvironment[$environmentName] = [Environment]::GetEnvironmentVariable($environmentName, 'Process')
}
$passwordPointer = [IntPtr]::Zero
try {
    if (-not $env:DB_PASSWORD) {
        $securePassword = Read-Host 'Senha Oracle FIAP' -AsSecureString
        $passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
        $env:DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
    }
    $env:DB_URL = $Url
    $env:DB_USERNAME = $Usuario
    $env:SPRING_PROFILES_ACTIVE = 'oracle'
    $env:PORT = [string]$Porta
    & java -Dfile.encoding=UTF-8 -jar $jar
    if ($LASTEXITCODE -ne 0) { throw 'A API encerrou com erro.' }
} finally {
    if ($passwordPointer -ne [IntPtr]::Zero) { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer) }
    foreach ($environmentName in $environmentNames) {
        [Environment]::SetEnvironmentVariable($environmentName, $previousEnvironment[$environmentName], 'Process')
    }
}
