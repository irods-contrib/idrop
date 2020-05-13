*** Settings ***
Resource          ../../iDrop_resource.txt

*** Test Cases ***
Launch iDrop
    [Tags]    smoke    iDrop    RegSkip    skipped
    Open iDrop Main Window

Open PassPhrase Dialog
    [Tags]    smoke    iDrop
    Start Application In Separate Thread    org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop
    Sleep    5s
    List Windows
    Comment    The Pass Phrase dialog window should already be open
    Select Dialog    passPhraseDialog
    List Components In Context    formatted

Open Grid Select Dialog
    [Tags]    smoke    iDrop
    Comment    The Pass Phrase dialog window should already be open
    Select Dialog    passPhraseDialog
    Insert Into Text Field    passPhrase    ${PassPhrasePassword}
    Push Button    btnOk
    Select Dialog    gridMemoryDialog
    List Components In Context    formatted

Open iDrop Main Window
    [Tags]    smoke    iDrop
    Comment    The Grid window should already be open
    ${TableRowCount} =    Get Table Row Count    gridTable
    Log    ${TableRowCount}
    ${HostRow} =    Find Row by Hostname    ${iRODSHost}    ${UserName}
    Log    ${HostRow}
    Select Table Cell    gridTable    ${HostRow}    Host
    Push Button    btnLogIn
    Comment    Sleep    10s
    Select Window    idropMainGui
    List Components In Context    formatted

Gather Navigate Dialog Components List
    [Tags]    smoke    iDrop
    Select Window    idropMainGui
    Comment    The Main iDrop window should already be open
    Push Button    btnNavigate
    Select Dialog    dialog0
    List Components In Context    formatted
    [Teardown]    Close Dialog    dialog0

Gather Download Dialog Components List
    [Tags]    smoke    iDrop
    Select Window    idropMainGui
    Comment    The Main iDrop window should already be open
    Push Button    btnDownload
    Select Dialog    downloadDialog
    List Components In Context    formatted
    [Teardown]    Close Dialog    downloadDialog

Gather upload Dialog Components List
    [Tags]    smoke    iDrop
    Select Window    idropMainGui
    Comment    The Main iDrop window should already be open
    Push Button    btnUpload
    Select Dialog    uploadDialog
    List Components In Context    formatted
    [Teardown]    Close Dialog    uploadDialog

Gather New Folder Dialog Components List
    [Tags]    smoke    iDrop
    Select Window    idropMainGui
    Comment    The Main iDrop window should already be open
    Click On Component    irodsTree    1
    Push Button    btnNew
    Select Dialog    newDirectoryDialog
    List Components In Context    formatted
    [Teardown]    Close Dialog    newDirectoryDialog

Gather Copy Move Dialog Components List
    [Tags]    smoke    iDrop
    Select Window    idropMainGui
    Comment    The Main iDrop window should already be open
    Click On Component    irodsTree    1
    Push Button    btnCopy
    Select Dialog    copyMoveDialog
    List Components In Context    formatted
    [Teardown]    Close Dialog    copyMoveDialog

Gather Delete Dialog Components List
    [Tags]    smoke    iDrop
    Select Window    idropMainGui
    Comment    The Main iDrop window should already be open
    Click On Component    irodsTree    1
    Push Button    btnDelete
    Select Dialog    deleteIrodsDialog
    List Components In Context    formatted
    [Teardown]    Close Dialog    deleteIrodsDialog

Gather Tools Dialog Components List
    [Tags]    smoke    iDrop
    Select Window    idropMainGui
    Comment    The Main iDrop window should already be open
    Push Button    btnTools
    Comment    Select Dialog    Tools Dialog
    Select Dialog    dialog1
    List Components In Context    formatted
    [Teardown]    Close Dialog    Tools Dialog

Gather Settings Dialog Components List
    [Tags]    smoke    iDrop
    Select Window    idropMainGui
    Comment    The Main iDrop window should already be open
    Push Button    btnSettings
    Select Dialog    Settings
    List Components In Context    formatted
    [Teardown]    Close Dialog    Settings
