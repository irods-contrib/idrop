*** Settings ***
Resource          ../../iDrop_resource.txt    #Suite Teardown | Close Window | idropMainGui

*** Test Cases ***
Start up iDrop and Log in
    [Tags]    smoke    iDrop
    Open iDrop Main Window
    List Components In Context

Launch iDrop
    [Tags]    smoke    iDrop    skipped
    Start Application In Separate Thread    org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop
    Sleep    5s
    List Windows
    Comment    The Pass Phrase dialog window should already be open
    Select Dialog    passPhraseDialog
    Insert Into Text Field    passPhrase    ${PassPhrasePassword}
    Push Button    btnOk
    Select Dialog    gridMemoryDialog
    Comment    The Grid window should already be open
    ${TableRowCount} =    Get Table Row Count    gridTable
    Log    ${TableRowCount}
    ${HostRow} =    Find Row by Hostname    ${iRODSHost}    ${UserName}
    Log    ${HostRow}
    Select Table Cell    gridTable    ${HostRow}    Host
    Push Button    btnLogIn
    Select Window    idropMainGui
    Comment    Sleep    10s
    List Components In Context

Junk
    [Tags]    smoke    iDrop    skipped
    Comment    ${UserName1} =    Set Variable    firstUser
    Comment    ${UserName2} =    Set Variable    secondUser
    Comment    Sleep    3s
    Comment    Clear Table Selection    gridTable
    Comment    Sleep    3s
    Comment    ${HostRow} =    Find Row by Hostname    ${iRODSHost}    ${UserName1}
    Comment    Select Table Cell    gridTable    ${HostRow}    Host
    Comment    Sleep    3s
    Comment    Clear Table Selection    gridTable
    Comment    Sleep    3s
    Comment    ${HostRow} =    Find Row by Hostname    ${iRODSHost}    ${UserName2}
    Comment    Select Table Cell    gridTable    ${HostRow}    Host
    Comment    Sleep    3s
    Comment    Clear Table Selection    gridTable
    Comment    Sleep    3s

Launch iDrop old
    [Tags]    smoke    iDrop    skipped
    Start Application In Separate Thread    org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop
    Sleep    5s
    List Windows
    Comment    The Pass Phrase dialog window should already be open
    Select Dialog    passPhraseDialog
    Insert Into Text Field    passPhrase    ${Password}
    Push Button    btnOk
    Select Dialog    gridMemoryDialog
    Comment    The Grid window should already be open
    Select Table Cell    gridTable    0    0
    Push Button    btnLogIn
    Sleep    10s
    Select Window    idropMainGui

Open Settings dialog window
    [Tags]    smoke    iDrop    skipped
    Comment    The Main iDrop window should already be open
    Select Window    idropMainGui
    Push Button    btnSettings
    Comment    Select Dialog    idropConfigurationPanel
    Comment    Select Dialog    Settings
    Select Dialog    dialog0
    List Components In Context
    ${context} =    Get Current Context
    Should Be Equal As Strings    ${context}    Settings
    Comment    List Components In Context    formatted
    Comment    Close Dialog    Settings
    Close Dialog    dialog0
    Sleep    5s
    Push Button    Cancel
    Sleep    5s

Waiting
    [Tags]    smoke    iDrop    skipped
    Select Window    idropMainGui
    Sleep    5s

Open Tools dialog window
    [Tags]    smoke    iDrop    skipped
    Comment    The Main iDrop window should already be open
    Select Window    idropMainGui
    Push Button    btnTools
    Comment    Select Dialog    ToolsDialog
    Select Dialog    Advanced Options
    List Components In Context    formatted

Open Tree dialog window
    [Tags]    smoke    iDrop    skipped
    Comment    The Main iDrop window should already be open
    Push Button    btnNavigate
    Select Dialog    dialog0
    ${PathsRowCount} =    Get Table Row Count    listPaths
    Log    ${PathsRowCount}
    Comment    ${ListValues} =    Get List Values    listPaths
    Comment    Log    ${ListValues}
    : FOR    ${Count}    IN RANGE    ${PathsRowCount}
    \    ${RowValue} =    Get Table Cell Value    listPaths    ${Count}    0
    \    Log    ${RowValue}

Select a tree node Local Directory
    [Tags]    smoke    iDrop    skipped
    Select Window    idropMainGui
    Comment    Select Window    dialog0
    List Windows
    List Components In Context
    Select Tree Node    localFileTree    Users
    ${CurContext} =    Get Current Context
    Comment    Click On Tree Node    localFileTree    Users    2
    Expand Tree Node    localFileTree    Users
    ${NodeNames} =    Get Tree Node Child Names    localFileTree    Users
    Log    ${NodeNames}
    Tree Node Should Be Expanded    localFileTree    Users
    Comment    Expand All Tree Nodes    localFileTree
    Comment    Expand Tree Node    localFileTree    Users|jerry
    Comment    Expand Tree Node    localFileTree    Users|jerry|trunk
    Comment    Expand Tree Node    localFileTree    Users|jerry|trunk|DataStore
    Comment    Expand Tree Node    localFileTree    Users|jerry|trunk|DataStore|download
    Comment    Select Tree Node    localFileTree    Users|jerry|trunk|DataStore|download
    Click On Tree Node    localFileTree    Users|jerry    2
    Click On Tree Node    localFileTree    Users|jerry|trunk    2
    Click On Tree Node    localFileTree    Users|jerry|trunk|DataStore    2
    Click On Tree Node    localFileTree    Users|jerry|trunk|DataStore|download    2
    Comment    Sleep    10s
    Comment    Component Should Exist    localFileTree
    Comment    Component Should Be Visible    localFileTree
    Comment    Component Should Exist    irodsTree
    Comment    Component Should Be Visible    irodsTree
    Comment    Scroll Component To View    localFileTree
    Comment    ${TreeChildNodes} =    Get Tree Node Child Names    localFileTree    ${4}
    Comment    Log    ${TreeChildNodes}
    Comment    Scroll Component To View    localDrives
    Comment    Select Context    localFileTree
    Comment    Select Window    frame1
    Comment    List Components In Context
    Comment    Click On Tree Node    localFileTree    bin    2
    Comment    Click On Tree Node    localFileTree    bin    2
    Comment    Click On Tree Node    LocalFileTree    bin    2
    Sleep    15s

Upload file from local system
    [Tags]    smoke    iDrop
    Select Window    idropMainGui
    Comment    Select Window    dialog0
    List Windows
    List Components In Context
    Component Should Exist    lblCurrentFile
    Component Should Exist    fileProgress
    Component Should Exist    totalProgress
    Component Should Exist    transferStatusProgress
    Component Should Exist    btnPause
    Select Window    idropMainGui
    Comment    Select Tab Pane    irodsTreePanel
    Comment    Select Context    irodsTreePanel
    List Components In Context
    Focus to Component    irodsTreePanel
    Focus to Component    irodsTree
    Click On Component    irodsTree    1
    Select Tree Node    irodsTree    0
    Click On Component    irodsTree    1
    Click On Tree Node    irodsTree    ${UserName}    2
    Click On Tree Node    irodsTree    ${UserName}|test_download    2
    ${RemoteNames} =    Get Tree Node Child Names    irodsTree    ${UserName}|test_download
    Log    ${RemoteNames}
    Sleep    10s
    Focus to Component    lblLocalDirectory
    Click On Tree Node    localFileTree    Users    2
    Click On Tree Node    localFileTree    Users|jerry    2
    Click On Tree Node    localFileTree    Users|jerry|trunk    2
    Click On Tree Node    localFileTree    Users|jerry|trunk|DataStore    2
    Click On Tree Node    localFileTree    Users|jerry|trunk|DataStore|download    2
    ${LocalNames} =    Get Tree Node Child Names    localFileTree    Users|jerry|trunk|DataStore|download
    Log    ${LocalNames}
    Clear Tree Selection    localFileTree
    Sleep    10s
