*** Settings ***
Suite Teardown    Close Dialog    Settings
Resource          ../iDrop_resource.txt

*** Test Cases ***
Launch iDrop
    [Tags]    smoke    iDrop    RegSkip
    Open iDrop Main Window

Open Settings dialog window
    [Tags]    smoke    iDrop
    Comment    The Main iDrop window should already be open
    Select Window    idropMainGui
    Push Button    btnSettings
    Select Dialog    Settings
    List Components In Context    formatted

Verify Settings window title
    [Tags]    smoke    iDrop
    ${WindowTitles} =    List Windows
    ${ContextIs} =    Get Current Context
    Log    ${ContextIs}
    Comment    ${output} =    Get Selected Window Title
    Comment    Log    ${output}
    Should Be Equal As Strings    ${ContextIs}    Settings Dialog

Open User Settings panel
    [Tags]    smoke    iDrop
    Select Dialog    Settings
    Click On Component    cpUserSettings
    Sleep    10s

Open Data Transfer Settings panel
    [Tags]    smoke    iDrop
    Select Dialog    Settings
    Click On Component    cpDataTransferSettings
    Sleep    10s

Open Test Connection panel
    [Tags]    smoke    iDrop
    Select Dialog    Settings
    Comment    Click On Component    cpTestConnection
    Click On Component    btnConnectionTest
    Sleep    10s

Open Pipeline Configuration panel
    [Tags]    smoke    iDrop
    Select Dialog    Settings
    Click On Component    cpPipelineConfig
    Sleep    10s
