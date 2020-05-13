*** Settings ***
Suite Teardown    Close Dialog    dialog0
Resource          ../iDrop_resource.txt

*** Test Cases ***
Launch iDrop
    [Tags]    smoke    iDrop    RegSkip
    Open iDrop Main Window

Open Tree dialog window
    [Tags]    smoke    iDrop
    Select Window    idropMainGui
    Comment    The Main iDrop window should already be open
    Push Button    btnNavigate
    Select Dialog    dialog0

Home button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnHome

Verify Home button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnHome
    Should Be Equal As Strings    ${ButtonText}    Home

Verify Home button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnHome    Go to the home directory

Home button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnHome

Root button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnRoot

Verify Root button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnRoot
    Should Be Equal As Strings    ${ButtonText}    Root

Verify Root button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnRoot    Go to root directory

Root button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnRoot

Direct Path textbox should exist
    [Tags]    smoke    iDrop
    Component Should Exist    directPathName

Verify Direct Path textbox tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    directPathName    Enter an iRODS absolute path to set as the top of the tree

Set Direct Path button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnSetDirectPath

Verify Set Direct Path button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnSetDirectPath
    Should Be Equal As Strings    ${ButtonText}    Set absolute path

Verify Set Direct Path button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnSetDirectPath    Set absolute path

Set Direct Path button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnSetDirectPath

List Paths table should exist
    [Tags]    smoke    iDrop
    Component Should Exist    listPaths

Verify paths in view are all there
    [Tags]    smoke    iDrop
    ${VerifyPaths} =    Create List    ${Zone}    home    ${UserName}
    Log    ${VerifyPaths}
    ${PathsRowCount} =    Get Table Row Count    listPaths
    Log    ${PathsRowCount}
    : FOR    ${Count}    IN RANGE    ${PathsRowCount}
    \    ${RowValue} =    Get Table Cell Value    listPaths    ${Count}    0
    \    Log    ${RowValue}
    \    Should Contain    ${VerifyPaths}    ${RowValue}
