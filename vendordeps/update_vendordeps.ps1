@('https://www.kauailabs.com/dist/frc/2020/navx_frc.json',
'https://www.revrobotics.com/content/sw/max/sdk/REVRobotics.json',
'https://maven.ctr-electronics.com/release/com/ctre/phoenix/Phoenix-frc2022-latest.json') | Foreach-Object {
    Invoke-WebRequest "$_" -UseBasicParsing -OutFile $(Split-Path -path "$_" -leaf)
}