; ===============================================
;  🚀 FPL-parser-portable — One-click launcher
; ===============================================

#define MyAppName "FPL Report Generator"
#define MyAppVer "2025.12"
#define AppPublisher "Serhii M"
#define MyIcon "dist\FPL-parser\FPL-parser.ico"
#define MyOutputDir "D:\FPL-reports"

[Setup]
AppName={#MyAppName}
AppVersion={#MyAppVer}
AppPublisher={#AppPublisher}
DefaultDirName={tmp}\fpl-scraper
ShowLanguageDialog=no
PrivilegesRequired=lowest
Compression=lzma2
SolidCompression=yes
OutputBaseFilename=FPL-parser-portable
OutputDir={#MyOutputDir}
SetupIconFile={#MyIcon}

[Files]
Source: "dist\FPL-parser\*"; DestDir: "{app}"; Flags: recursesubdirs createallsubdirs

[Code]
procedure CurStepChanged(CurStep: TSetupStep);
var
  ResultCode: Integer;
begin
  if CurStep = ssPostInstall then begin
    Exec(ExpandConstant('{app}\FPL-parser.exe'),
         '--output=' + ExpandConstant('{#MyOutputDir}'),
         ExpandConstant('{app}'),
         SW_SHOWNORMAL,
         ewWaitUntilTerminated,
         ResultCode);

    DelTree(ExpandConstant('{app}'), True, True, True);
  end;
end;
