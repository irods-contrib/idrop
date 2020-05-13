*** Settings ***
Resource          ../../iDrop_resource.txt    #Suite Teardown | Close Window | idropMainGui

*** Test Cases ***
Start iDrop and Log In
    [Tags]    smoke    iDrop
    Open iDrop Main Window
    List Components In Context

Open Tree Dialog and Retrieve Context List
    [Tags]    smoke    iDrop
    ${Title} =    Set Variable    ${EMPTY}
    ${DialogName} =    Set Variable    dialog0
    Return Context    btnNavigate
    [Teardown]    Close Dialog    dialog0

Open Download Dialog and Retrieve Context List
    [Tags]    smoke    iDrop
    ${Title} =    Set Variable    Download Files and Collections from iRODS
    ${DialogName} =    Set Variable    downloadDialog
    Return Context    btnDownload
    [Teardown]    Close Dialog    downloadDialog

Open Upload Dialog and Retrieve Context List
    [Tags]    smoke    iDrop
    ${Title} =    Set Variable    Upload Files and Folders to iRODS
    ${DialogName} =    Set Variable    uploadDialog
    Return Context    btnUpload
    [Teardown]    Close Dialog    uploadDialog

Open New Dialog and Retrieve Context List
    [Documentation]    Needs to select an item in the tree before displaying window.
    [Tags]    smoke    iDrop
    ${Title} =    Set Variable    Create New Folder Dialog
    ${DialogName} =    Set Variable    newDirectoryDialog
    Comment    Return Context    btnNew
    Comment    The Main iDrop window should already be open
    Select Window    idropMainGui
    Click On Tree Node    iRODSTree    4    1
    Sleep    10s
    Push Button    btnNew
    Comment    Select Dialog    ${Title}
    Select Dialog    ${DialogName}
    List Windows
    Comment    ${output} =    Get Selected Window Title
    Comment    Should Be Equal As Strings    ${output}    ${Title}
    List Components In Context
    Comment    Close Dialog    ${DialogName}
    [Teardown]    Close Dialog    newDirectoryDialog

Open Copy/Move Dialog and Retrieve Context List
    [Documentation]    Needs to select an item in the tree before displaying window.
    [Tags]    smoke    iDrop
    ${Title} =    Set Variable    Move or Copy iRODS Files and Collections
    ${DialogName} =    Set Variable    copyMoveDialog
    Return Context    btnCopy
    [Teardown]    Close Dialog    ${DialogName}

Open Tools Dialog
    [Tags]    smoke    iDrop
    ${Title} =    Set Variable    Advanced Options
    ${DialogName} =    Set Variable    dialog0
    Return Context    btnTools
    [Teardown]    Close Dialog    ${Title}

Open Settings Dialog
    [Tags]    smoke    iDrop
    ${Title} =    Set Variable    Settings
    ${DialogName} =    Set Variable    dialog0
    Comment    Return Context    btnSettings
    Comment    The Main iDrop window should already be open
    Select Window    idropMainGui
    Push Button    btnSettings
    Select Dialog    ${Title}
    List Windows
    Comment    ${output} =    Get Selected Window Title
    Comment    Should Be Equal As Strings    ${output}    ${Title}
    List Components In Context
    Comment    Sleep    10s
    Comment    Close Dialog    ${Title}
    Comment    Sleep    5s
    Comment    Close Dialog    ${Title}
    Comment    Sleep    15s
    Comment    Select Window    idropMainGui
    Comment    Sleep    10s
    [Teardown]    Close Dialog    ${Title}

*** Keywords ***
Return Context
    [Arguments]    ${Button}
    Comment    The Main iDrop window should already be open
    Select Window    idropMainGui
    Push Button    ${Button}
    Comment    Select Dialog    ${Title}
    Select Dialog    ${DialogName}
    List Windows
    Comment    ${output} =    Get Selected Window Title
    Comment    Should Be Equal As Strings    ${output}    ${Title}
    List Components In Context
    Comment    Close Dialog    ${DialogName}
