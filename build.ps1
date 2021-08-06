# set your java paths here
$env:JAVA_HOME = "C:\Program Files\Java\jdk1.8.0_281"
$env:Path = "C:\Program Files\Java\jdk1.8.0_281\bin;" + $env:Path

Push-Location .\org.eclipse.sed.ifl.root
mvn clean initialize verify
Pop-Location
Pause