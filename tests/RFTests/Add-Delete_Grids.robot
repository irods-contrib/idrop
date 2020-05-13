*** Settings ***
Resource          ../iDrop_resource.txt

*** Test Cases ***
Launch iDrop
    [Tags]    smoke    iDrop
    Start Application In Separate Thread    org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop
    Sleep    5s
    List Windows
    Comment    The Pass Phrase dialog window should already be open
    Select Dialog    passPhraseDialog
    Insert Into Text Field    passPhrase    ${Password}
    Push Button    btnOk
    Select Dialog    gridMemoryDialog
    Comment    The Grid window should already be open
    ${TableRowCount} =    Get Table Row Count    gridTable
    Log    ${TableRowCount}
    ${HostRow} =    Find Row by Hostname    ${iRODSHost}    ${UserName}
    Log    ${HostRow}
    Select Table Cell    gridTable    ${HostRow}    Host
    Comment    Push Button    btnLogIn
    Comment    Comment    Sleep    5s

Launch iDrop
    [Tags]    smoke    iDrop    RegSkip    skipped
    Open iDrop Main Window

Open Grid Select dialog
    [Tags]    smoke    iDrop    skipped
    Comment    The MainScreen dialog window should already be open
    Select Window    idropMainGui
    Push Button    btnGrids
    Select Dialog    gridMemoryDialog
    Sleep    10s

Open Add New Grid dialog
    [Tags]    smoke    iDrop
    Comment    The iDrop: Grid Accounts dialog window should already be open
    Push Button    btnAddGrid
    Select Dialog    gridInfoDialog
    List Windows
    List Components In Context    formatted

Verify iDrop: Grid Accounts dialog title
    [Tags]    smoke    iDrop
    Select Dialog    gridInfoDialog
    ${TitleIs} =    Get Current Context
    Log    ${TitleIs}
    Should Be Equal As Strings    ${TitleIs}    Create Grid Account Information

Verify Host textbox enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    host

Verify Host label text
    [Tags]    smoke    iDrop    skipped
    ${output} =    Get Label Content    lblHost
    Should Be Equal As Strings    ${output}    Host:

Verify Port textbox enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    port

Verify Port label text
    [Tags]    smoke    iDrop    skipped
    ${output} =    Get Label Content    lblPort
    Should Be Equal As Strings    ${output}    Port:

Verify Zone textbox enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    zone

Verify Zone label text
    [Tags]    smoke    iDrop
    ${output} =    Get Label Content    lblZone
    Should Be Equal As Strings    ${output}    Zone:

Verify User textbox enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    user

Verify User label text
    [Tags]    smoke    iDrop
    ${output} =    Get Label Content    lblUser
    Should Be Equal As Strings    ${output}    User:

Verify Password textbox enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    password

Verify Password label text
    [Tags]    smoke    iDrop
    ${output} =    Get Label Content    lblPassword
    Should Be Equal As Strings    ${output}    Password:

Verify Default Resource textbox enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    host

Verify Default Resource label text
    [Tags]    smoke    iDrop
    ${output} =    Get Label Content    lblDefaultResource
    Should Be Equal As Strings    ${output}    Default Resource:

Verify Starting Collection textbox enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    startingCollection

Verify Starting Collection label text
    [Tags]    smoke    iDrop
    ${output} =    Get Label Content    lblStartingCollection
    Should Be Equal As Strings    ${output}    Starting Collection:

Verify Authorization Scheme combobox enabled
    [Tags]    smoke    iDrop    skipped
    Combo Box Should Be Enabled    STANDARD

Verify Authorization Scheme combobox values
    [Tags]    smoke    iDrop    skipped
    ${output} =    Get Combobox Values    STANDARD

Verify Comment textbox enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    comment

Verify Comment label text
    [Tags]    smoke    iDrop
    ${output} =    Get Label Content    lblComment
    Should Be Equal As Strings    ${output}    Comment:

Verify Error message failure to create grid
    [Tags]    smoke    iDrop
    Insert Into Text Field    host    ${iRODSHost}
    Insert Into Text Field    zone    ${Zone}
    Insert Into Text Field    user    ${UserName}
    Insert Into Text Field    password    ${Password}
    Insert Into Text Field    startingCollection    /${Zone}/home/${UserName}
    Insert Into Text Field    comment    Simple test comment for Grid entry
    Comment    Sleep    10s
    Push Button    btnOK
    List Components In Context
    List Windows
    Dialog Should Be Open    dialog0
    Select Dialog    dialog0
    ${ErrorMsgText} =    Get Label Content    OptionPane.label
    Should Be Equal As Strings    ${ErrorMsgText}    Unable to process login, the server or account appears to be invalid
    Button Should Exist    OptionPane.button
    ${ButtonText} =    Get Button Text    OptionPane.button
    Should Be Equal As Strings    ${ButtonText}    OK
    Verify Tooltip Text    OptionPane.button    None
    Button Should Be Enabled    OptionPane.button
    Push Button    OptionPane.button

Delete Grid
    [Tags]    smoke    iDrop    skipped
    Select Dialog    gridMemoryDialog
    ${TitleIs} =    Get Current Context
    Log    ${TitleIs}
    Should Be Equal As Strings    ${TitleIs}    iDrop: Grid Accounts
    Select Table Cell    gridTable    3    0

*** Keywords ***
Shutdown App and Wait
    Close All Dialogs
    Sleep    15s
